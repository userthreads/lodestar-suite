/*
 * This file is part of the Lodestar Suite distribution (https://github.com/userthreads/lodestar-suite).
 * Copyright (c) userthreads.
 */

package userthreads.lodestarsuite.mixin.sodium;

import net.caffeinemc.mods.sodium.client.render.chunk.compile.ChunkBuildBuffers;
import net.caffeinemc.mods.sodium.client.render.chunk.translucent_sorting.TranslucentGeometryCollector;
import net.caffeinemc.mods.sodium.client.world.LevelSlice;
import net.caffeinemc.mods.sodium.fabric.render.FluidRendererImpl;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = FluidRendererImpl.class, remap = false)
public abstract class SodiumFluidRendererImplMixin {
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void onRender(LevelSlice level, BlockState blockState, FluidState fluidState, BlockPos blockPos, BlockPos offset, TranslucentGeometryCollector collector, ChunkBuildBuffers buffers, CallbackInfo info) {
        // Xray module removed - no alpha modifications
    }
}
