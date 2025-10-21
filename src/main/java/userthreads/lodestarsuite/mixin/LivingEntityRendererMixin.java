/*
 * This file is part of the Lodestar Suite distribution (https://github.com/userthreads/lodestar-suite).
 * Copyright (c) userthreads.
 */

package userthreads.lodestarsuite.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import userthreads.lodestarsuite.systems.modules.Modules;
import userthreads.lodestarsuite.systems.modules.render.NoRender;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.scoreboard.Team;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static userthreads.lodestarsuite.LodestarSuite.mc;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>> {
    // Player model rendering in main menu

    @ModifyExpressionValue(method = "hasLabel(Lnet/minecraft/entity/LivingEntity;D)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getScoreboardTeam()Lnet/minecraft/scoreboard/Team;"))
    private Team hasLabelClientPlayerEntityGetScoreboardTeamProxy(Team team) {
        return (mc.player == null) ? null : team;
    }

    @Inject(method = "render(Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("HEAD"), cancellable = true)
    private void render$Head(S state, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo info) {
        Entity entity = ((userthreads.lodestarsuite.mixininterface.IEntityRenderState) state).meteor$getEntity();
        if (!(entity instanceof LivingEntity livingEntity)) return;

        if (Modules.get().get(NoRender.class).noDeadEntities() && livingEntity.isDead()) info.cancel();

        // Chams module removed - no through walls rendering
    }
}
