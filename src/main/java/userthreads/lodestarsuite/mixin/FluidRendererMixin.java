/*
 * This file is part of the Lodestar Suite distribution (https://github.com/userthreads/lodestar-suite).
 * Copyright (c) userthreads.
 */

package userthreads.lodestarsuite.mixin;

import userthreads.lodestarsuite.systems.modules.Modules;
import userthreads.lodestarsuite.systems.modules.world.Ambience;
import userthreads.lodestarsuite.utils.render.color.Color;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.FluidRenderer;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FluidRenderer.class)
public abstract class FluidRendererMixin {
    @Unique private final ThreadLocal<Integer> alphas = new ThreadLocal<>();
    @Unique private final ThreadLocal<Boolean> ambient = ThreadLocal.withInitial(() -> false);

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void onRender(BlockRenderView world, BlockPos pos, VertexConsumer vertexConsumer, BlockState blockState, FluidState fluidState, CallbackInfo info) {
        Ambience ambience = Modules.get().get(Ambience.class);
        ambient.set(ambience.isActive() && ambience.customLavaColor.get() && fluidState.isIn(FluidTags.LAVA));

        // Xray module removed - no alpha modifications
    }

    @Inject(method = "vertex", at = @At("HEAD"), cancellable = true)
    private void onVertex(VertexConsumer vertexConsumer, float x, float y, float z, float red, float green, float blue, float u, float v, int light, CallbackInfo info) {
        if (ambient.get()) {
            Color color = Modules.get().get(Ambience.class).lavaColor.get();
            vertex(vertexConsumer, x, y, z, color.r, color.g, color.b, color.a, u, v, light);
            info.cancel();
        }
        // Xray module removed - no alpha modifications
    }

    @Unique
    private void vertex(VertexConsumer vertexConsumer, float x, float y, float z, int red, int green, int blue, int alpha, float u, float v, int light) {
        vertexConsumer.vertex(x, y, z).color(red, green, blue, alpha).texture(u, v).light(light).normal(0.0f, 1.0f, 0.0f);
    }
}
