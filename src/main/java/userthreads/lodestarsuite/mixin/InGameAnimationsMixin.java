/*
 * This file is part of the Lodestar Suite distribution (https://github.com/userthreads/lodestar-suite).
 * Copyright (c) userthreads.
 */

package userthreads.lodestarsuite.mixin;

import userthreads.lodestarsuite.gui.WidgetScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;


@Mixin(WidgetScreen.class)
public abstract class InGameAnimationsMixin extends Screen {
    public InGameAnimationsMixin(Text title) {
        super(title);
    }

}
