/*
 * This file is part of the Lodestar Suite distribution (https://github.com/userthreads/lodestar-suite).
 * Copyright (c) userthreads.
 */

package userthreads.lodestarsuite.gui.themes.meteor;

import userthreads.lodestarsuite.utils.render.color.SettingColor;

public class HalloweenGuiTheme extends LodestarGuiTheme {
    public HalloweenGuiTheme() {
        super("Halloween");
        
        // Halloween color scheme - simple orange and dark theme
        accentColor.set(new SettingColor(255, 165, 0, 255)); // Orange
        textColor.set(new SettingColor(255, 200, 100, 255)); // Light orange text
        textSecondaryColor.set(new SettingColor(200, 150, 50, 255)); // Darker orange
        placeholderColor.set(new SettingColor(150, 100, 30, 255)); // Dark orange placeholder
        
        // Module colors
        moduleBackground.set(new SettingColor(30, 30, 30, 255)); // Dark module background
        
        // Slider colors
        sliderLeft.set(new SettingColor(255, 165, 0, 255)); // Orange slider
        sliderRight.set(new SettingColor(100, 60, 0, 255)); // Dark orange slider background
    }
}
