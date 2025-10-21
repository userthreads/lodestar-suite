/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package userthreads.lodestarsuite.gui.themes.meteor;

import userthreads.lodestarsuite.gui.renderer.GuiRenderer;
import userthreads.lodestarsuite.gui.utils.BaseWidget;
import userthreads.lodestarsuite.gui.widgets.WWidget;
import userthreads.lodestarsuite.utils.render.color.Color;

public interface MeteorWidget extends BaseWidget {
    default LodestarGuiTheme theme() {
        return (LodestarGuiTheme) getTheme();
    }

    default void renderBackground(GuiRenderer renderer, WWidget widget, Color outlineColor, Color backgroundColor) {
        LodestarGuiTheme theme = theme();
        double s = theme.scale(2);

        renderer.quad(widget.x + s, widget.y + s, widget.width - s * 2, widget.height - s * 2, backgroundColor);

        renderer.quad(widget.x, widget.y, widget.width, s, outlineColor);
        renderer.quad(widget.x, widget.y + widget.height - s, widget.width, s, outlineColor);
        renderer.quad(widget.x, widget.y + s, s, widget.height - s * 2, outlineColor);
        renderer.quad(widget.x + widget.width - s, widget.y + s, s, widget.height - s * 2, outlineColor);
    }

    default void renderBackground(GuiRenderer renderer, WWidget widget, boolean pressed, boolean mouseOver) {
        LodestarGuiTheme theme = theme();
        renderBackground(renderer, widget, theme.outlineColor.get(pressed, mouseOver), theme.backgroundColor.get(pressed, mouseOver));
    }
}
