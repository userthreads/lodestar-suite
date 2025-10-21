/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package userthreads.lodestarsuite.gui.tabs;

import userthreads.lodestarsuite.gui.GuiTheme;
import userthreads.lodestarsuite.gui.WidgetScreen;
import userthreads.lodestarsuite.gui.utils.Cell;
import userthreads.lodestarsuite.gui.widgets.WWidget;

public abstract class TabScreen extends WidgetScreen {
    public final Tab tab;

    public TabScreen(GuiTheme theme, Tab tab) {
        super(theme, tab.name);

        this.tab = tab;
    }

    public <T extends WWidget> Cell<T> addDirect(T widget) {
        return super.add(widget);
    }
}
