/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package userthreads.lodestarsuite.mixin;

import userthreads.lodestarsuite.systems.modules.Modules;
import userthreads.lodestarsuite.systems.modules.misc.BetterChat;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ChatScreen.class, priority = 1001)
public abstract class ChatScreenMixin {
    @Shadow protected TextFieldWidget chatField;

    @Inject(method = "init", at = @At(value = "RETURN"))
    private void onInit(CallbackInfo info) {
        if (Modules.get().get(BetterChat.class).isInfiniteChatBox()) chatField.setMaxLength(Integer.MAX_VALUE);
    }
}
