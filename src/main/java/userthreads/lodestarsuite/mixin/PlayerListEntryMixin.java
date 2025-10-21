/*
 * This file is part of the Lodestar Suite distribution (https://github.com/userthreads/lodestar-suite).
 * Copyright (c) userthreads.
 */

package userthreads.lodestarsuite.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.SkinTextures;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerListEntry.class)
public abstract class PlayerListEntryMixin {
    @Shadow
    public abstract GameProfile getProfile();

    @Inject(method = "getSkinTextures", at = @At("HEAD"), cancellable = true)
    private void onGetTexture(CallbackInfoReturnable<SkinTextures> info) {
        if (getProfile().getName().equals(MinecraftClient.getInstance().getSession().getUsername())) {
            // NameProtect module removed - no skin protection
        }
    }
}
