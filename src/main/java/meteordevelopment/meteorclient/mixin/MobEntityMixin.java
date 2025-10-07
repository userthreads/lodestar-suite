/*
 * This file is part of the Lodestar Client distribution (https://github.com/waythread/lodestar-client).
 * Copyright (c) waythread.
 */

package meteordevelopment.meteorclient.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.entity.mob.MobEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin {
    @ModifyReturnValue(method = "hasSaddleEquipped", at = @At("RETURN"))
    private boolean hasSaddleEquipped(boolean original) {
        // EntityControl module removed - no saddle modifications
        return original;
    }
}
