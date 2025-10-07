/*
 * This file is part of the Lodestar Client distribution (https://github.com/waythread/lodestar-client).
 * Copyright (c) waythread.
 */

package meteordevelopment.meteorclient.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mojang.authlib.GameProfile;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.entity.DropItemsEvent;
import meteordevelopment.meteorclient.events.entity.player.PlayerTickMovementEvent;
import meteordevelopment.meteorclient.events.entity.player.SendMovementPacketsEvent;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {
    @Shadow
    public Input input;

    public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(method = "dropSelectedItem", at = @At("HEAD"), cancellable = true)
    private void onDropSelectedItem(boolean dropEntireStack, CallbackInfoReturnable<Boolean> info) {
        if (MeteorClient.EVENT_BUS.post(DropItemsEvent.get(getMainHandStack())).isCancelled()) info.setReturnValue(false);
    }

    @ModifyExpressionValue(method = "tickNausea", at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;currentScreen:Lnet/minecraft/client/gui/screen/Screen;"))
    private Screen modifyNauseaCurrentScreen(Screen original) {
        // Portals module removed - no nausea modification
        return original;
    }

    @ModifyExpressionValue(method = "applyMovementSpeedFactors", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z"))
    private boolean redirectUsingItem(boolean isUsingItem) {
        // NoSlow module removed - no item usage modification
        return isUsingItem;
    }

    @Inject(method = "isSneaking", at = @At("HEAD"), cancellable = true)
    private void onIsSneaking(CallbackInfoReturnable<Boolean> info) {
        // Scaffold and Flight modules removed - no sneaking modifications
    }

    @Inject(method = "shouldSlowDown", at = @At("HEAD"), cancellable = true)
    private void onShouldSlowDown(CallbackInfoReturnable<Boolean> info) {
        // NoSlow module removed - no slowdown modifications
    }

    @Inject(method = "pushOutOfBlocks", at = @At("HEAD"), cancellable = true)
    private void onPushOutOfBlocks(double x, double d, CallbackInfo info) {
        // Velocity module removed - no block push modifications
    }

    @ModifyExpressionValue(method = "canSprint", at = @At(value = "CONSTANT", args = "floatValue=6.0f"))
    private float onHunger(float constant) {
        // NoSlow module removed - no hunger modifications
        return constant;
    }

    // Movement modules removed - no sneaking packet modifications

    @Inject(method = "tickMovement", at = @At("HEAD"))
    private void preTickMovement(CallbackInfo ci) {
        MeteorClient.EVENT_BUS.post(PlayerTickMovementEvent.get());
    }

    // Sprint module removed

    @ModifyExpressionValue(method = "canStartSprinting", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/input/Input;hasForwardMovement()Z"))
    private boolean modifyIsWalking(boolean original) {
        // Sprint module removed - no sprint modifications
        return original;
    }

    @ModifyExpressionValue(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/input/Input;hasForwardMovement()Z"))
    private boolean modifyMovement(boolean original) {
        // Sprint module removed - no movement modifications
        return original;
    }

    @WrapWithCondition(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;setSprinting(Z)V", ordinal = 3))
    private boolean wrapSetSprinting(ClientPlayerEntity instance, boolean b) {
        // Sprint module removed - no sprint wrapping
        return true;
    }

    // Rotations

    @Inject(method = "sendMovementPackets", at = @At("HEAD"))
    private void onSendMovementPacketsHead(CallbackInfo info) {
        MeteorClient.EVENT_BUS.post(SendMovementPacketsEvent.Pre.get());
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V", ordinal = 1))
    private void onTickHasVehicleBeforeSendPackets(CallbackInfo info) {
        MeteorClient.EVENT_BUS.post(SendMovementPacketsEvent.Pre.get());
    }

    @Inject(method = "sendMovementPackets", at = @At("TAIL"))
    private void onSendMovementPacketsTail(CallbackInfo info) {
        MeteorClient.EVENT_BUS.post(SendMovementPacketsEvent.Post.get());
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V", ordinal = 1, shift = At.Shift.AFTER))
    private void onTickHasVehicleAfterSendPackets(CallbackInfo info) {
        MeteorClient.EVENT_BUS.post(SendMovementPacketsEvent.Post.get());
    }
}
