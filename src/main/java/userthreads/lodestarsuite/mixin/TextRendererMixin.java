/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package userthreads.lodestarsuite.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import userthreads.lodestarsuite.systems.modules.Modules;
import userthreads.lodestarsuite.systems.modules.render.NoRender;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "net.minecraft.client.font.TextRenderer$Drawer")
public abstract class TextRendererMixin {
    @ModifyExpressionValue(method = "accept", at = @At(value = "INVOKE", target = "Lnet/minecraft/text/Style;isObfuscated()Z"))
    private boolean onRenderObfuscatedStyle(boolean original) {
        if (Modules.get() == null || Modules.get().get(NoRender.class) == null) {
            return original;
        }
        return !Modules.get().get(NoRender.class).noObfuscation() && original;
    }
}
