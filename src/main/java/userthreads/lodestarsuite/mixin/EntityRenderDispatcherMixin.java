/*
 * This file is part of the Lodestar Suite distribution (https://github.com/userthreads/lodestar-suite).
 * Copyright (c) userthreads.
 */

package userthreads.lodestarsuite.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import userthreads.lodestarsuite.mixininterface.IEntityRenderState;
import userthreads.lodestarsuite.systems.modules.Modules;
import userthreads.lodestarsuite.systems.modules.render.NoRender;
import userthreads.lodestarsuite.utils.entity.fakeplayer.FakePlayerEntity;
import userthreads.lodestarsuite.utils.render.postprocess.PostProcessShaders;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityHitbox;
import net.minecraft.client.render.entity.state.EntityHitboxAndView;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin {
    @Shadow
    public Camera camera;

    @Inject(method = "render(Lnet/minecraft/entity/Entity;DDDFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/render/entity/EntityRenderer;)V", at = @At("HEAD"), cancellable = true)
    private <E extends Entity, S extends EntityRenderState> void render(E entity, double x, double y, double z, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, EntityRenderer<? super E, S> renderer, CallbackInfo info) {
        if (entity instanceof FakePlayerEntity player && player.hideWhenInsideCamera) {
            int cX = MathHelper.floor(this.camera.getPos().x);
            int cY = MathHelper.floor(this.camera.getPos().y);
            int cZ = MathHelper.floor(this.camera.getPos().z);

            if (cX == entity.getBlockX() && cZ == entity.getBlockZ() && (cY == entity.getBlockY() || cY == entity.getBlockY() + 1)) info.cancel();
        }
    }

    // Hitboxes functionality removed

    @Unique
    private static Entity renderHitboxEntity;

    @Inject(method = "renderHitboxes(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/entity/state/EntityRenderState;Lnet/minecraft/client/render/entity/state/EntityHitboxAndView;Lnet/minecraft/client/render/VertexConsumerProvider;)V", at = @At("HEAD"))
    private void renderHitboxes$setEntity(MatrixStack matrices, EntityRenderState state, EntityHitboxAndView hitbox, VertexConsumerProvider vertexConsumers, CallbackInfo info) {
        renderHitboxEntity = ((IEntityRenderState) state).meteor$getEntity();
    }

    @Inject(method = "renderHitboxes(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/entity/state/EntityRenderState;Lnet/minecraft/client/render/entity/state/EntityHitboxAndView;Lnet/minecraft/client/render/VertexConsumerProvider;)V", at = @At("TAIL"))
    private void renderHitboxes$unsetEntity(MatrixStack matrices, EntityRenderState state, EntityHitboxAndView hitbox, VertexConsumerProvider vertexConsumers, CallbackInfo info) {
        renderHitboxEntity = null;
    }

    @Inject(method = "renderHitbox", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;push()V", shift = At.Shift.AFTER))
    private static void renderHitbox$scale(MatrixStack matrices, VertexConsumer vertexConsumer, EntityHitbox hitbox, CallbackInfo info) {
        // Hitboxes module removed - no scaling applied
    }

    // IEntityRenderState

    @ModifyExpressionValue(method = "render(Lnet/minecraft/entity/Entity;DDDFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/render/entity/EntityRenderer;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/EntityRenderer;getAndUpdateRenderState(Lnet/minecraft/entity/Entity;F)Lnet/minecraft/client/render/entity/state/EntityRenderState;"))
    private <E extends Entity, S extends EntityRenderState> S render$getAndUpdateRenderState(S state, E entity, double x, double y, double z, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, EntityRenderer<? super E, S> renderer) {
        ((IEntityRenderState) state).meteor$setEntity(entity);
        return state;
    }

    // Player model rendering in main menu

    @Inject(method = "renderShadow", at = @At("HEAD"), cancellable = true)
    private static void onRenderShadow(MatrixStack matrices, VertexConsumerProvider vertexConsumers, EntityRenderState renderState, float opacity, WorldView world, float radius, CallbackInfo info) {
        if (PostProcessShaders.rendering) info.cancel();
        if (Modules.get().get(NoRender.class).noDeadEntities() && renderState instanceof LivingEntityRenderState livingEntityRenderState && livingEntityRenderState.deathTime > 0) info.cancel();
    }

    @Inject(method = "getSquaredDistanceToCamera(Lnet/minecraft/entity/Entity;)D", at = @At("HEAD"), cancellable = true)
    private void onGetSquaredDistanceToCameraEntity(Entity entity, CallbackInfoReturnable<Double> info) {
        if (camera == null) info.setReturnValue(0.0);
    }

    @Inject(method = "getSquaredDistanceToCamera(DDD)D", at = @At("HEAD"), cancellable = true)
    private void onGetSquaredDistanceToCameraXYZ(double x, double y, double z, CallbackInfoReturnable<Double> info) {
        if (camera == null) info.setReturnValue(0.0);
    }
}
