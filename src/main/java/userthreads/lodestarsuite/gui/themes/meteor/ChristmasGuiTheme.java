/*
 * This file is part of the Lodestar Suite distribution (https://github.com/userthreads/lodestar-suite).
 * Copyright (c) userthreads.
 */

package userthreads.lodestarsuite.gui.themes.meteor;

import userthreads.lodestarsuite.utils.render.color.SettingColor;

public class ChristmasGuiTheme extends LodestarGuiTheme {
    public ChristmasGuiTheme() {
        super("Christmas");
        
        // Christmas color scheme - red, green, and white theme
        accentColor.set(new SettingColor(220, 20, 20, 255)); // Christmas red
        textColor.set(new SettingColor(255, 255, 255, 255)); // White text
        textSecondaryColor.set(new SettingColor(200, 255, 200, 255)); // Light green
        placeholderColor.set(new SettingColor(150, 200, 150, 255)); // Muted green placeholder
        
        // Module colors
        moduleBackground.set(new SettingColor(20, 40, 20, 255)); // Dark green module background
        
        // Slider colors
        sliderLeft.set(new SettingColor(220, 20, 20, 255)); // Christmas red slider
        sliderRight.set(new SettingColor(40, 80, 40, 255)); // Dark green slider background
    }
}
