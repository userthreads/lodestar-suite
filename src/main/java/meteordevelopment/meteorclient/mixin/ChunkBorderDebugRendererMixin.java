/*
 * This file is part of the Lodestar Client distribution (https://github.com/waythread/lodestar-client).
 * Copyright (c) waythread.
 */

package meteordevelopment.meteorclient.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.util.math.ChunkPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(net.minecraft.client.render.debug.ChunkBorderDebugRenderer.class)
public abstract class ChunkBorderDebugRendererMixin {
    @ModifyExpressionValue(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getChunkPos()Lnet/minecraft/util/math/ChunkPos;"))
    private ChunkPos render$getChunkPos(ChunkPos chunkPos) {
        // Freecam module removed - return original chunk position
        return chunkPos;
    }
}
