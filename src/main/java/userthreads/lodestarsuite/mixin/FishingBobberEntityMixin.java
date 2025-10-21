/*
 * This file is part of the Lodestar Suite distribution (https://github.com/userthreads/lodestar-suite).
 * Copyright (c) userthreads.
 */

package userthreads.lodestarsuite.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import static userthreads.lodestarsuite.LodestarSuite.mc;

@Mixin(FishingBobberEntity.class)
public abstract class FishingBobberEntityMixin {
    @WrapOperation(method = "handleStatus", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/FishingBobberEntity;pullHookedEntity(Lnet/minecraft/entity/Entity;)V"))
    private void preventFishingRodPull(FishingBobberEntity instance, Entity entity, Operation<Void> original) {
        if (!instance.getWorld().isClient || entity != mc.player) original.call(instance, entity);

        // Velocity module removed - no fishing rod modifications
        original.call(instance, entity);
    }
}
