/*
 * This file is part of the Lodestar Suite distribution (https://github.com/userthreads/lodestar-suite).
 * Copyright (c) userthreads.
 */

package userthreads.lodestarsuite.mixin;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import userthreads.lodestarsuite.LodestarSuite;
import userthreads.lodestarsuite.commands.Commands;
import userthreads.lodestarsuite.systems.config.Config;
import userthreads.lodestarsuite.systems.modules.Modules;
import userthreads.lodestarsuite.systems.modules.render.NoRender;
import userthreads.lodestarsuite.utils.Utils;
import userthreads.lodestarsuite.utils.misc.text.MeteorClickEvent;
import userthreads.lodestarsuite.utils.misc.text.RunnableClickEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Screen.class, priority = 500) // needs to be before baritone
public abstract class ScreenMixin {
    @Inject(method = "renderInGameBackground", at = @At("HEAD"), cancellable = true)
    private void onRenderInGameBackground(CallbackInfo info) {
        if (Utils.canUpdate() && Modules.get().get(NoRender.class).noGuiBackground())
            info.cancel();
    }

    @Inject(method = "handleTextClick", at = @At(value = "HEAD"), cancellable = true)
    private void onInvalidClickEvent(@Nullable Style style, CallbackInfoReturnable<Boolean> cir) {
        if (style == null || !(style.getClickEvent() instanceof RunnableClickEvent runnableClickEvent)) return;

        runnableClickEvent.runnable.run();
        cir.setReturnValue(true);
    }

    @Inject(method = "handleBasicClickEvent", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;error(Ljava/lang/String;Ljava/lang/Object;)V", remap = false))
    private static void onHandleBasicClickEvent(ClickEvent clickEvent, MinecraftClient client, Screen screen, CallbackInfo ci) {
        if (clickEvent instanceof MeteorClickEvent meteorClickEvent && meteorClickEvent.value.startsWith(Config.get().prefix.get())) {
            try {
                Commands.dispatch(meteorClickEvent.value.substring(Config.get().prefix.get().length()));
            } catch (CommandSyntaxException e) {
                LodestarSuite.LOG.error("Failed to run command", e);
            }
        }
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void onKeyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        // GUIMove module removed - no key press modifications
    }
}
