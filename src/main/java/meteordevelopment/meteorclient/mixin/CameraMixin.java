/*
 * This file is part of the Lodestar Client distribution (https://github.com/waythread/lodestar-client).
 * Copyright (c) waythread.
 */

package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.mixininterface.ICamera;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.FreeLook;
import meteordevelopment.meteorclient.systems.modules.render.NoRender;
import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(Camera.class)
public abstract class CameraMixin implements ICamera {
    @Shadow private boolean thirdPerson;

    @Shadow private float yaw;
    @Shadow private float pitch;

    @Shadow protected abstract void setRotation(float yaw, float pitch);

    @Unique
    private float tickDelta;


    @ModifyVariable(method = "clipToSpace", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private float modifyClipToSpace(float d) {
        // Freecam and CameraTweaks modules removed - no clip modifications
        return d;
    }

    @Inject(method = "clipToSpace", at = @At("HEAD"), cancellable = true)
    private void onClipToSpace(float desiredCameraDistance, CallbackInfoReturnable<Float> info) {
        // CameraTweaks module removed - no clip modifications
    }

    @Inject(method = "update", at = @At("HEAD"))
    private void onUpdateHead(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo info) {
        this.tickDelta = tickDelta;
    }

    @Inject(method = "update", at = @At("TAIL"))
    private void onUpdateTail(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo info) {
        // Freecam module removed - no third person modifications
    }

    @ModifyArgs(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;setPos(DDD)V"))
    private void onUpdateSetPosArgs(Args args) {
        // Freecam module removed - no position modifications
    }

    @ModifyArgs(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;setRotation(FF)V"))
    private void onUpdateSetRotationArgs(Args args) {
        FreeLook freeLook = Modules.get().get(FreeLook.class);

        if (freeLook.isActive()) {
            args.set(0, freeLook.cameraYaw);
            args.set(1, freeLook.cameraPitch);
        }
    }

    @Override
    public void meteor$setRot(double yaw, double pitch) {
        setRotation((float) yaw, (float) MathHelper.clamp(pitch, -90, 90));
    }
}
