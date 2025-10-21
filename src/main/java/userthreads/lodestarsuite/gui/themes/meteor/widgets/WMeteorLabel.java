/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package userthreads.lodestarsuite.gui.themes.meteor.widgets;

import userthreads.lodestarsuite.gui.renderer.GuiRenderer;
import userthreads.lodestarsuite.gui.themes.meteor.MeteorWidget;
import userthreads.lodestarsuite.gui.widgets.WLabel;

public class WMeteorLabel extends WLabel implements MeteorWidget {
    public WMeteorLabel(String text, boolean title) {
        super(text, title);
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        if (!text.isEmpty()) {
            renderer.text(text, x, y, color != null ? color : (title ? theme().titleTextColor.get() : theme().textColor.get()), title);
        }
    }
}
