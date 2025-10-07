/*
 * This file is part of the Lodestar Client distribution (https://github.com/waythread/lodestar-client).
 * Copyright (c) waythread.
 */

package meteordevelopment.meteorclient.mixin;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.EndCrystalEntityRenderer;
import net.minecraft.client.render.entity.state.EndCrystalEntityRenderState;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EndCrystalEntityRenderer.class)
public abstract class EndCrystalEntityRendererMixin {
    // Chams module removed - no crystal rendering modifications

    @Shadow
    @Final
    @Mutable
    private static RenderLayer END_CRYSTAL;

    @Shadow
    @Final
    private static Identifier TEXTURE;

    @Inject(method = "render(Lnet/minecraft/client/render/entity/state/EndCrystalEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("HEAD"))
    private void render$renderLayer(EndCrystalEntityRenderState endCrystalEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        // Chams module removed - use original texture
        END_CRYSTAL = RenderLayer.getEntityTranslucent(TEXTURE);
    }
}