/*
 * This file is part of the Lodestar Suite distribution (https://github.com/userthreads/lodestar-suite).
 * Copyright (c) userthreads.
 */

package userthreads.lodestarsuite.mixin;

import net.minecraft.world.border.WorldBorder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldBorder.class)
public abstract class WorldBorderMixin {
    @Inject(method = "canCollide", at = @At("HEAD"), cancellable = true)
    private void canCollide(CallbackInfoReturnable<Boolean> info) {
        // Collisions module removed - no border collision modifications
    }

    @Inject(method = "contains(Lnet/minecraft/util/math/BlockPos;)Z", at = @At("HEAD"), cancellable = true)
    private void contains(CallbackInfoReturnable<Boolean> info) {
        // Collisions module removed - no border modifications
    }
}
