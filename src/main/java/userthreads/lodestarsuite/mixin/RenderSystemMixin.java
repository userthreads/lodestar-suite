/*
 * This file is part of the Lodestar Suite distribution (https://github.com/userthreads/lodestar-suite).
 * Copyright (c) userthreads.
 */

package userthreads.lodestarsuite.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import userthreads.lodestarsuite.renderer.MeshUniforms;
import userthreads.lodestarsuite.systems.modules.render.Blur;
import userthreads.lodestarsuite.utils.render.postprocess.OutlineUniforms;
import userthreads.lodestarsuite.utils.render.postprocess.PostProcessShader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderSystem.class)
public abstract class RenderSystemMixin {
    @Inject(method = "flipFrame", at = @At("TAIL"))
    private static void meteor$flipFrame(CallbackInfo info) {
        MeshUniforms.flipFrame();
        Blur.flipFrame();
        PostProcessShader.flipFrame();
        // ChamsShader removed - no chams shader frame flipping
        OutlineUniforms.flipFrame();
    }
}
