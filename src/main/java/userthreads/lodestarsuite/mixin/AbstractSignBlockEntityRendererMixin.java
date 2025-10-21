/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package userthreads.lodestarsuite.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import userthreads.lodestarsuite.systems.modules.Modules;
import userthreads.lodestarsuite.systems.modules.render.NoRender;
import net.minecraft.client.render.block.entity.AbstractSignBlockEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractSignBlockEntityRenderer.class)
public abstract class AbstractSignBlockEntityRendererMixin {
    @ModifyExpressionValue(method = "renderText", at = @At(value = "CONSTANT", args = {"intValue=4", "ordinal=1"}))
    private int loopTextLengthProxy(int i) {
        if (Modules.get().get(NoRender.class).noSignText()) return 0;
        return i;
    }
}
