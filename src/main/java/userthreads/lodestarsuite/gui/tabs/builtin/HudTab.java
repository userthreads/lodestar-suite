/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package userthreads.lodestarsuite.gui.tabs.builtin;

import userthreads.lodestarsuite.gui.GuiTheme;
import userthreads.lodestarsuite.gui.renderer.GuiRenderer;
import userthreads.lodestarsuite.gui.tabs.Tab;
import userthreads.lodestarsuite.gui.tabs.TabScreen;
import userthreads.lodestarsuite.gui.tabs.WindowTabScreen;
import userthreads.lodestarsuite.gui.widgets.containers.WHorizontalList;
import userthreads.lodestarsuite.gui.widgets.pressable.WButton;
import userthreads.lodestarsuite.gui.widgets.pressable.WCheckbox;
import userthreads.lodestarsuite.systems.hud.Hud;
import userthreads.lodestarsuite.systems.hud.screens.HudEditorScreen;
import userthreads.lodestarsuite.utils.misc.NbtUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;

import static userthreads.lodestarsuite.LodestarSuite.mc;

public class HudTab extends Tab {
    public HudTab() {
        super("HUD");
    }

    @Override
    public TabScreen createScreen(GuiTheme theme) {
        return new HudScreen(theme, this);
    }

    @Override
    public boolean isScreen(Screen screen) {
        return screen instanceof HudScreen;
    }

    public static class HudScreen extends WindowTabScreen {
        private final Hud hud;

        public HudScreen(GuiTheme theme, Tab tab) {
            super(theme, tab);

            hud = Hud.get();
            hud.settings.onActivated();
        }

        @Override
        public void initWidgets() {
            add(theme.settings(hud.settings)).expandX();

            add(theme.horizontalSeparator()).expandX();

            WButton openEditor = add(theme.button("Edit")).expandX().widget();
            openEditor.action = () -> mc.setScreen(new HudEditorScreen(theme));

            WHorizontalList buttons = add(theme.horizontalList()).expandX().widget();
            buttons.add(theme.button("Clear")).expandX().widget().action = hud::clear;
            buttons.add(theme.button("Reset to default elements")).expandX().widget().action = hud::resetToDefaultElements;

            add(theme.horizontalSeparator()).expandX();

            WHorizontalList bottom = add(theme.horizontalList()).expandX().widget();

            bottom.add(theme.label("Active: "));
            WCheckbox active = bottom.add(theme.checkbox(hud.active)).expandCellX().widget();
            active.action = () -> hud.active = active.checked;

            WButton resetSettings = bottom.add(theme.button(GuiRenderer.RESET)).widget();
            resetSettings.action = hud.settings::reset;
            resetSettings.tooltip = "Reset";
        }

        @Override
        protected void onRenderBefore(DrawContext drawContext, float delta) {
            HudEditorScreen.renderElements(drawContext);
        }

        @Override
        public boolean toClipboard() {
            return NbtUtils.toClipboard(hud);
        }

        @Override
        public boolean fromClipboard() {
            return NbtUtils.fromClipboard(hud);
        }
    }
}
