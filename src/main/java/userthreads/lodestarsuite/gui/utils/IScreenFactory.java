/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package userthreads.lodestarsuite.gui.utils;

import userthreads.lodestarsuite.gui.GuiTheme;
import userthreads.lodestarsuite.gui.WidgetScreen;

public interface IScreenFactory {
    WidgetScreen createScreen(GuiTheme theme);
}
