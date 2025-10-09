/*
 * This file is part of the Lodestar Client distribution (https://github.com/waythread/lodestar-client).
 * Copyright (c) waythread.
 */

package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.gui.WidgetScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;


@Mixin(WidgetScreen.class)
public abstract class InGameAnimationsMixin extends Screen {
    public InGameAnimationsMixin(Text title) {
        super(title);
    }

}
