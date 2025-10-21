/*
 * This file is part of the Lodestar Suite distribution (https://github.com/userthreads/lodestar-suite).
 * Copyright (c) userthreads.
 */

package userthreads.lodestarsuite.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import userthreads.lodestarsuite.systems.modules.Modules;
import userthreads.lodestarsuite.systems.modules.render.Fullbright;
import userthreads.lodestarsuite.systems.modules.render.NoRender;
import userthreads.lodestarsuite.utils.render.postprocess.PostProcessShaders;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.text.Text;
import net.minecraft.world.LightType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity, S extends EntityRenderState> {

    @Inject(method = "getDisplayName", at = @At("HEAD"), cancellable = true)
    private void onRenderLabel(T entity, CallbackInfoReturnable<Text> cir) {
        if (PostProcessShaders.rendering) cir.setReturnValue(null);
        if (Modules.get().get(NoRender.class).noNametags()) cir.setReturnValue(null);
        // Nametags module removed - no custom nametag handling
    }

    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
    private void shouldRender(T entity, Frustum frustum, double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
        if (Modules.get().get(NoRender.class).noEntity(entity)) cir.setReturnValue(false);
        if (Modules.get().get(NoRender.class).noFallingBlocks() && entity instanceof FallingBlockEntity) cir.setReturnValue(false);
    }

    @ModifyReturnValue(method = "getSkyLight", at = @At("RETURN"))
    private int onGetSkyLight(int original) {
        return Math.max(Modules.get().get(Fullbright.class).getLuminance(LightType.SKY), original);
    }

    @ModifyReturnValue(method = "getBlockLight", at = @At("RETURN"))
    private int onGetBlockLight(int original) {
        return Math.max(Modules.get().get(Fullbright.class).getLuminance(LightType.BLOCK), original);
    }

    @ModifyExpressionValue(method = "updateRenderState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getLightLevel(Lnet/minecraft/world/LightType;Lnet/minecraft/util/math/BlockPos;)I"))
    private int onGetLightLevel(int original) {
        return Math.max(Modules.get().get(Fullbright.class).getLuminance(LightType.BLOCK), original);
    }
}
