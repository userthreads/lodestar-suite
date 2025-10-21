/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package userthreads.lodestarsuite.mixin;

import userthreads.lodestarsuite.LodestarSuite;
import userthreads.lodestarsuite.events.world.AmbientOcclusionEvent;
import userthreads.lodestarsuite.systems.modules.Modules;
import userthreads.lodestarsuite.systems.modules.render.NoRender;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.class)
public abstract class AbstractBlockMixin {
    @Inject(method = "getAmbientOcclusionLightLevel", at = @At("HEAD"), cancellable = true)
    private void onGetAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos, CallbackInfoReturnable<Float> info) {
        AmbientOcclusionEvent event = LodestarSuite.EVENT_BUS.post(AmbientOcclusionEvent.get());

        if (event.lightLevel != -1) info.setReturnValue(event.lightLevel);
    }

    @Inject(method = "getRenderingSeed", at = @At("HEAD"), cancellable = true)
    private void onRenderingSeed(BlockState state, BlockPos pos, CallbackInfoReturnable<Long> cir) {
        if (Modules.get().get(NoRender.class).noTextureRotations()) cir.setReturnValue(0L);
    }
}
