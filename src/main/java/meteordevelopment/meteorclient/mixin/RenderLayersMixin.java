/*
 * This file is part of the Lodestar Suite distribution (https://github.com/waythread/lodestar-suite).
 * Copyright (c) waythread.
 */

package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.world.Ambience;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.BlockRenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.fluid.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderLayers.class)
public abstract class RenderLayersMixin {
    @Inject(method = "getBlockLayer", at = @At("HEAD"), cancellable = true)
    private static void onGetBlockLayer(BlockState state, CallbackInfoReturnable<BlockRenderLayer> cir) {
        // Xray module removed - no block layer modifications
    }

    @Inject(method = "getFluidLayer", at = @At("HEAD"), cancellable = true)
    private static void onGetFluidLayer(FluidState state, CallbackInfoReturnable<BlockRenderLayer> cir) {
        if (Modules.get() == null) return;

        Ambience ambience = Modules.get().get(Ambience.class);
        int a = ambience.lavaColor.get().a;
        if (ambience.isActive() && ambience.customLavaColor.get() && a > 0 && a < 255) {
            cir.setReturnValue(BlockRenderLayer.TRANSLUCENT);
        }
    }
}
