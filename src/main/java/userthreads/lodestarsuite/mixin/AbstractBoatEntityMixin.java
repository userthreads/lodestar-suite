/*
 * This file is part of the Lodestar Suite distribution (https://github.com/userthreads/lodestar-suite).
 * Copyright (c) userthreads.
 */

package userthreads.lodestarsuite.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import userthreads.lodestarsuite.LodestarSuite;
import userthreads.lodestarsuite.events.entity.BoatMoveEvent;
import net.minecraft.entity.vehicle.AbstractBoatEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractBoatEntity.class)
public abstract class AbstractBoatEntityMixin {
    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/vehicle/AbstractBoatEntity;move(Lnet/minecraft/entity/MovementType;Lnet/minecraft/util/math/Vec3d;)V"))
    private void onTickInvokeMove(CallbackInfo info) {
        if ((Object) this instanceof AbstractBoatEntity boatEntity) {
            LodestarSuite.EVENT_BUS.post(BoatMoveEvent.get(boatEntity));
        }
    }

    @ModifyExpressionValue(method = "updatePaddles", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/vehicle/AbstractBoatEntity;pressingLeft:Z"))
    private boolean modifyPressingLeft(boolean original) {
        // BoatFly module removed - no paddle modifications
        return original;
    }

    @ModifyExpressionValue(method = "updatePaddles", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/vehicle/AbstractBoatEntity;pressingRight:Z"))
    private boolean modifyPressingRight(boolean original) {
        // BoatFly module removed - no paddle modifications
        return original;
    }
}
