/*
 * This file is part of the Lodestar Suite distribution (https://github.com/userthreads/lodestar-suite).
 * Copyright (c) userthreads.
 */

package userthreads.lodestarsuite.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import userthreads.lodestarsuite.LodestarSuite;
import userthreads.lodestarsuite.events.entity.DropItemsEvent;
import userthreads.lodestarsuite.events.entity.player.ClipAtLedgeEvent;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static userthreads.lodestarsuite.LodestarSuite.mc;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {
    @Shadow
    public abstract PlayerAbilities getAbilities();

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "clipAtLedge", at = @At("HEAD"), cancellable = true)
    protected void clipAtLedge(CallbackInfoReturnable<Boolean> info) {
        if (!getWorld().isClient) return;

        ClipAtLedgeEvent event = LodestarSuite.EVENT_BUS.post(ClipAtLedgeEvent.get());
        if (event.isSet()) info.setReturnValue(event.isClip());
    }

    @Inject(method = "dropItem", at = @At("HEAD"), cancellable = true)
    private void onDropItem(ItemStack stack, boolean retainOwnership, CallbackInfoReturnable<ItemEntity> cir) {
        if (getWorld().isClient && !stack.isEmpty()) {
            if (LodestarSuite.EVENT_BUS.post(DropItemsEvent.get(stack)).isCancelled()) cir.setReturnValue(null);
        }
    }

    @Inject(method = "isSpectator", at = @At("HEAD"), cancellable = true)
    private void onIsSpectator(CallbackInfoReturnable<Boolean> info) {
        if (mc.getNetworkHandler() == null) info.setReturnValue(false);
    }

    @Inject(method = "isCreative", at = @At("HEAD"), cancellable = true)
    private void onIsCreative(CallbackInfoReturnable<Boolean> info) {
        if (mc.getNetworkHandler() == null) info.setReturnValue(false);
    }

    @ModifyReturnValue(method = "getBlockBreakingSpeed", at = @At(value = "RETURN"))
    public float onGetBlockBreakingSpeed(float breakSpeed, BlockState block) {
        if (!getWorld().isClient) return breakSpeed;

        // SpeedMine module removed - no block breaking speed modifications
        return breakSpeed;
    }

    @ModifyReturnValue(method = "getMovementSpeed", at = @At("RETURN"))
    private float onGetMovementSpeed(float original) {
        if (!getWorld().isClient) return original;
        // NoSlow module removed - no movement speed modifications
        return original;
    }

    @Inject(method = "getOffGroundSpeed", at = @At("HEAD"), cancellable = true)
    private void onGetOffGroundSpeed(CallbackInfoReturnable<Float> info) {
        if (!getWorld().isClient) return;
        // Flight module removed - no off ground speed modifications
    }

    @WrapWithCondition(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;setVelocity(Lnet/minecraft/util/math/Vec3d;)V"))
    private boolean keepSprint$setVelocity(PlayerEntity instance, Vec3d vec3d) {
        // Sprint module removed - no velocity modifications
        return true;
    }

    @WrapWithCondition(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;setSprinting(Z)V"))
    private boolean keepSprint$setSprinting(PlayerEntity instance, boolean b) {
        // Sprint module removed - no sprint modifications
        return true;
    }

    @ModifyReturnValue(method = "getBlockInteractionRange", at = @At("RETURN"))
    private double modifyBlockInteractionRange(double original) {
        // Reach module removed - no block interaction range modifications
        return original;
    }

    @ModifyReturnValue(method = "getEntityInteractionRange", at = @At("RETURN"))
    private double modifyEntityInteractionRange(double original) {
        // Reach module removed - no entity interaction range modifications
        return original;
    }
}
