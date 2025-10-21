/*
 * This file is part of the Lodestar Suite distribution (https://github.com/userthreads/lodestar-suite).
 * Copyright (c) userthreads.
 */

package userthreads.lodestarsuite.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.render.entity.model.EndCrystalEntityModel;
import net.minecraft.client.render.entity.state.EndCrystalEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EndCrystalEntityModel.class)
public abstract class EndCrystalEntityModelMixin {
    // Chams module removed - no crystal modifications

    @ModifyExpressionValue(method = "setAngles(Lnet/minecraft/client/render/entity/state/EndCrystalEntityRenderState;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/EndCrystalEntityRenderer;getYOffset(F)F"))
    private float setAngles$bounce(float original, EndCrystalEntityRenderState state) {
        // Chams module removed - return original bounce
        return original;
    }

    @ModifyExpressionValue(method = "setAngles(Lnet/minecraft/client/render/entity/state/EndCrystalEntityRenderState;)V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/render/entity/state/EndCrystalEntityRenderState;age:F", ordinal = 0))
    private float modifySpeed(float original) {
        // Chams module removed - return original speed
        return original;
    }
}
