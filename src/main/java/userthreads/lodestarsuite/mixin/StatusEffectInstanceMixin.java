/*
 * This file is part of the Lodestar Suite distribution (https://github.com/userthreads/lodestar-suite).
 * Copyright (c) userthreads.
 */

package userthreads.lodestarsuite.mixin;

import userthreads.lodestarsuite.utils.Utils;
import net.minecraft.entity.effect.StatusEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StatusEffectInstance.class)
public abstract class StatusEffectInstanceMixin {
    @Inject(method = "updateDuration", at = @At("HEAD"), cancellable = true)
    private void tick(CallbackInfo info) {
        if (!Utils.canUpdate()) return;

        // PotionSaver module removed - no potion duration modifications
    }
}
