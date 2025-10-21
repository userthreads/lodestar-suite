/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package userthreads.lodestarsuite.gui.screens.settings;

import userthreads.lodestarsuite.gui.GuiTheme;
import userthreads.lodestarsuite.gui.screens.settings.base.CollectionListSettingScreen;
import userthreads.lodestarsuite.gui.widgets.WWidget;
import userthreads.lodestarsuite.settings.Setting;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenHandlerType;

import java.util.List;

public class ScreenHandlerSettingScreen extends CollectionListSettingScreen<ScreenHandlerType<?>> {
    public ScreenHandlerSettingScreen(GuiTheme theme, Setting<List<ScreenHandlerType<?>>> setting) {
        super(theme, "Select Screen Handlers", setting, setting.get(), Registries.SCREEN_HANDLER);
    }

    @Override
    protected WWidget getValueWidget(ScreenHandlerType<?> value) {
        return theme.label(getName(value));
    }

    @Override
    protected String[] getValueNames(ScreenHandlerType<?> type) {
        return new String[]{
            getName(type)
        };
    }

    private static String getName(ScreenHandlerType<?> type) {
        return Registries.SCREEN_HANDLER.getId(type).toString();
    }
}
