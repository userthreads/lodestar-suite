/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package userthreads.lodestarsuite.mixin;

import userthreads.lodestarsuite.systems.modules.Modules;
import userthreads.lodestarsuite.systems.modules.render.BreakIndicators;
import userthreads.lodestarsuite.systems.modules.render.NoRender;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockRenderManager.class)
public abstract class BlockRenderManagerMixin {
    @Inject(method = "renderDamage", at = @At("HEAD"), cancellable = true)
    private void renderDamage(BlockState state, BlockPos pos, BlockRenderView world, MatrixStack matrix, VertexConsumer vertexConsumer, CallbackInfo info) {
        if (Modules.get().isActive(BreakIndicators.class) || Modules.get().get(NoRender.class).noBlockBreakOverlay()) info.cancel();
    }
}
