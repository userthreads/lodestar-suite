/*
 * This file is part of the Lodestar Suite distribution (https://github.com/waythread/lodestar-suite).
 * Copyright (c) waythread.
 */

package meteordevelopment.meteorclient.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.render.item.property.numeric.CompassState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CompassState.class)
public abstract class CompassStateMixin {
    @ModifyExpressionValue(method = "getBodyYaw", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getBodyYaw()F"))
    private static float callLivingEntityGetYaw(float original) {
        // Freecam module removed - return original value
        return original;
    }

    @ModifyReturnValue(method = "getAngleTo(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/BlockPos;)D", at = @At("RETURN"))
    private static double modifyGetAngleTo(double original, Entity entity, BlockPos pos) {
        // Freecam module removed - return original value
        return original;
    }
}
