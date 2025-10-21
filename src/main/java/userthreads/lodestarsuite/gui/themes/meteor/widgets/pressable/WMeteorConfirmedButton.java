/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package userthreads.lodestarsuite.gui.themes.meteor.widgets.pressable;

import userthreads.lodestarsuite.gui.renderer.GuiRenderer;
import userthreads.lodestarsuite.gui.renderer.packer.GuiTexture;
import userthreads.lodestarsuite.gui.themes.meteor.LodestarGuiTheme;
import userthreads.lodestarsuite.gui.themes.meteor.MeteorWidget;
import userthreads.lodestarsuite.gui.widgets.pressable.WConfirmedButton;
import userthreads.lodestarsuite.utils.render.color.Color;

public class WMeteorConfirmedButton extends WConfirmedButton implements MeteorWidget {
    public WMeteorConfirmedButton(String text, String confirmText, GuiTexture texture) {
        super(text, confirmText, texture);
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        LodestarGuiTheme theme = theme();
        double pad = pad();

        Color outline = theme.outlineColor.get(pressed, mouseOver);
        Color fg = pressedOnce ? theme.backgroundColor.get(pressed, mouseOver) : theme.textColor.get();
        Color bg = pressedOnce ? theme.textColor.get() : theme.backgroundColor.get(pressed, mouseOver);

        renderBackground(renderer, this, outline, bg);

        String text = getText();

        if (text != null) {
            renderer.text(text, x + width / 2 - textWidth / 2, y + pad, fg, false);
        }
        else {
            double ts = theme.textHeight();
            renderer.quad(x + width / 2 - ts / 2, y + pad, ts, ts, texture, fg);
        }
    }
}
