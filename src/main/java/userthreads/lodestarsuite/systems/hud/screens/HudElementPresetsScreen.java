/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package userthreads.lodestarsuite.systems.hud.screens;

import userthreads.lodestarsuite.gui.GuiTheme;
import userthreads.lodestarsuite.gui.WindowScreen;
import userthreads.lodestarsuite.gui.widgets.containers.WHorizontalList;
import userthreads.lodestarsuite.gui.widgets.input.WTextBox;
import userthreads.lodestarsuite.gui.widgets.pressable.WPlus;
import userthreads.lodestarsuite.systems.hud.Hud;
import userthreads.lodestarsuite.systems.hud.HudElementInfo;
import userthreads.lodestarsuite.utils.Utils;
import net.minecraft.client.gui.DrawContext;
import org.jetbrains.annotations.Nullable;

public class HudElementPresetsScreen extends WindowScreen {
    private final HudElementInfo<?> info;
    private final int x, y;

    private final WTextBox searchBar;
    @Nullable
    private HudElementInfo<?>.Preset firstPreset;

    public HudElementPresetsScreen(GuiTheme theme, HudElementInfo<?> info, int x, int y) {
        super(theme, "Select preset for " + info.title);

        this.info = info;
        this.x = x + 9;
        this.y = y;

        searchBar = theme.textBox("");
        searchBar.action = () -> {
            clear();
            initWidgets();
        };

        enterAction = () -> {
            if (firstPreset == null) return;
            Hud.get().add(firstPreset, x, y);
            close();
        };
    }

    @Override
    public void initWidgets() {
        firstPreset = null;

        // Search bar
        add(searchBar).expandX();
        searchBar.setFocused(true);

        // Presets
        for (HudElementInfo<?>.Preset preset : info.presets) {
            if (!Utils.searchTextDefault(preset.title, searchBar.get(), false)) continue;

            WHorizontalList l = add(theme.horizontalList()).expandX().widget();

            l.add(theme.label(preset.title));

            WPlus add = l.add(theme.plus()).expandCellX().right().widget();
            add.action = () -> {
                Hud.get().add(preset, x, y);
                close();
            };

            if (firstPreset == null) firstPreset = preset;
        }
    }

    @Override
    protected void onRenderBefore(DrawContext drawContext, float delta) {
        HudEditorScreen.renderElements(drawContext);
    }
}
