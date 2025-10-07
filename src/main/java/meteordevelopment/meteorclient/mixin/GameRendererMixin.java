/*
 * This file is part of the Lodestar Client distribution (https://github.com/waythread/lodestar-client).
 * Copyright (c) waythread.
 */

package meteordevelopment.meteorclient.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderSystem;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.render.GetFovEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.render.RenderAfterWorldEvent;
import meteordevelopment.meteorclient.renderer.MeteorRenderPipelines;
import meteordevelopment.meteorclient.renderer.Renderer3D;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.Fov;
import meteordevelopment.meteorclient.systems.modules.render.NoRender;
import meteordevelopment.meteorclient.systems.modules.render.Zoom;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.entity.fakeplayer.FakePlayerEntity;
import meteordevelopment.meteorclient.utils.render.NametagUtils;
import meteordevelopment.meteorclient.utils.render.RenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.profiler.Profilers;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    public abstract void updateCrosshairTarget(float tickDelta);

    @Shadow
    public abstract void reset();

    @Shadow
    @Final
    private Camera camera;

    @Unique
    private Renderer3D renderer;

    @Unique
    private Renderer3D depthRenderer;

    @Unique
    private final MatrixStack matrices = new MatrixStack();

    @Shadow
    protected abstract void bobView(MatrixStack matrices, float tickDelta);

    @Shadow
    protected abstract void tiltViewWhenHurt(MatrixStack matrices, float tickDelta);

    @Inject(method = "renderWorld", at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V", args = {"ldc=hand"}))
    private void onRenderWorld(RenderTickCounter tickCounter, CallbackInfo ci, @Local(ordinal = 0) Matrix4f projection, @Local(ordinal = 2) Matrix4f view, @Local(ordinal = 1) float tickDelta, @Local MatrixStack matrixStack) {
        if (!Utils.canUpdate()) return;

        Profilers.get().push(MeteorClient.MOD_ID + "_render");

        // Create renderer and event

        if (renderer == null) renderer = new Renderer3D(MeteorRenderPipelines.WORLD_COLORED_LINES, MeteorRenderPipelines.WORLD_COLORED);
        if (depthRenderer == null) depthRenderer = new Renderer3D(MeteorRenderPipelines.WORLD_COLORED_LINES_DEPTH, MeteorRenderPipelines.WORLD_COLORED_DEPTH);
        Render3DEvent event = Render3DEvent.get(matrixStack, renderer, depthRenderer, tickDelta, camera.getPos().x, camera.getPos().y, camera.getPos().z);

        // Call utility classes

        RenderUtils.updateScreenCenter(projection, view);
        NametagUtils.onRender(view);

        // Update model view matrix

        RenderSystem.getModelViewStack().pushMatrix().mul(view);

        matrices.push();
        tiltViewWhenHurt(matrices, camera.getLastTickProgress());
        if (client.options.getBobView().getValue()) bobView(matrices, camera.getLastTickProgress());
        RenderSystem.getModelViewStack().mul(matrices.peek().getPositionMatrix().invert());
        matrices.pop();

        // Render

        renderer.begin();
        depthRenderer.begin();
        MeteorClient.EVENT_BUS.post(event);
        renderer.render(matrixStack);
        depthRenderer.render(matrixStack);

        // Revert model view matrix

        RenderSystem.getModelViewStack().popMatrix();

        Profilers.get().pop();
    }

    @Inject(method = "renderWorld", at = @At("TAIL"))
    private void onRenderWorldTail(CallbackInfo info) {
        MeteorClient.EVENT_BUS.post(RenderAfterWorldEvent.get());
    }

    @ModifyReturnValue(method = "findCrosshairTarget", at = @At("RETURN"))
    private HitResult onUpdateTargetedEntity(HitResult original, @Local HitResult hitResult) {
        // NoMiningTrace module removed - no mining trace modifications
        if (original instanceof EntityHitResult entityHitResult && entityHitResult.getEntity() instanceof FakePlayerEntity fakePlayer && fakePlayer.noHit) {
            return hitResult;
        }

        return original;
    }

    @ModifyExpressionValue(method = "findCrosshairTarget", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;raycast(DFZ)Lnet/minecraft/util/hit/HitResult;"))
    private HitResult modifyRaycastResult(HitResult original, Entity entity, double blockInteractionRange, double entityInteractionRange, float tickProgress, @Local(ordinal = 0, argsOnly = true) double maxDistance) {
        // LiquidInteract module removed - no liquid interaction modifications
        return original;
    }

    @Inject(method = "showFloatingItem", at = @At("HEAD"), cancellable = true)
    private void onShowFloatingItem(ItemStack floatingItem, CallbackInfo info) {
        if (floatingItem.getItem() == Items.TOTEM_OF_UNDYING && Modules.get().get(NoRender.class).noTotemAnimation()) {
            info.cancel();
        }
    }


    @ModifyReturnValue(method = "getFov", at = @At("RETURN"))
    private float modifyFov(float original) {
        return MeteorClient.EVENT_BUS.post(GetFovEvent.get(original)).fov;
    }

    // Freecam and HighwayBuilder modules removed - no camera modifications

    @Inject(method = "renderHand", at = @At("HEAD"), cancellable = true)
    private void renderHand(float tickProgress, boolean sleeping, Matrix4f positionMatrix, CallbackInfo ci) {
        if (!Modules.get().get(Zoom.class).renderHands()) {
            ci.cancel();
            return;
        }
        
        // Set flag to indicate we're rendering hands
        Fov.setRenderingHands(true);
    }
    
    @Inject(method = "renderHand", at = @At("TAIL"))
    private void renderHandTail(float tickProgress, boolean sleeping, Matrix4f positionMatrix, CallbackInfo ci) {
        // Clear flag when done rendering hands
        Fov.setRenderingHands(false);
    }
}
