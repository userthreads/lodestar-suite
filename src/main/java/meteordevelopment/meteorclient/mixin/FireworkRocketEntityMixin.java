/*
 * This file is part of the Lodestar Client distribution (https://github.com/waythread/lodestar-client).
 * Copyright (c) waythread.
 */

package meteordevelopment.meteorclient.mixin;

import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FireworkRocketEntity.class)
public abstract class FireworkRocketEntityMixin {
    @Shadow
    private int life;

    @Shadow
    private int lifeTime;

    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo info) {
        // ElytraBoost module removed - no firework modifications
    }

    @Inject(method = "onEntityHit", at = @At("HEAD"), cancellable = true)
    private void onEntityHit(EntityHitResult entityHitResult, CallbackInfo info) {
        // ElytraBoost module removed - no firework modifications
    }

    @Inject(method = "onBlockHit", at = @At("HEAD"), cancellable = true)
    private void onBlockHit(BlockHitResult blockHitResult, CallbackInfo info) {
        // ElytraBoost module removed - no firework modifications
    }
}
