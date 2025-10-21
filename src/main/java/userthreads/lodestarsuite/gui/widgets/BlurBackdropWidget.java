/*
 * This file is part of the Lodestar Suite distribution (https://github.com/userthreads/lodestar-suite).
 * Copyright (c) userthreads.
 */

package userthreads.lodestarsuite.gui.widgets;

import userthreads.lodestarsuite.gui.renderer.GuiRenderer;
import userthreads.lodestarsuite.gui.renderer.BlurRenderer;
import userthreads.lodestarsuite.utils.render.color.SettingColor;
import userthreads.lodestarsuite.utils.render.color.Color;

public class BlurBackdropWidget extends WWidget {
    private float blurIntensity = 2.0f;
    private SettingColor tintColor = new SettingColor(0, 0, 0, 100);
    private boolean useAdvancedBlur = true;
    
    public BlurBackdropWidget() {
        super();
    }
    
    public BlurBackdropWidget(float blurIntensity, SettingColor tintColor) {
        super();
        this.blurIntensity = blurIntensity;
        this.tintColor = tintColor;
    }
    
    @Override
    protected void onCalculateSize() {
        // Size is set by parent or manually
    }
    
    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        // Check if blur is enabled in the theme
        if (parent != null && parent.theme instanceof userthreads.lodestarsuite.gui.themes.meteor.LodestarGuiTheme) {
            userthreads.lodestarsuite.gui.themes.meteor.LodestarGuiTheme theme = 
                (userthreads.lodestarsuite.gui.themes.meteor.LodestarGuiTheme) parent.theme;
            
            if (!theme.blurEnabled.get()) {
                return; // Don't render if blur is disabled
            }
            
            // Update settings from theme
            blurIntensity = theme.blurIntensity.get().floatValue();
            tintColor = theme.blurTintColor.get();
            useAdvancedBlur = theme.blurAdvancedMode.get();
        }
        
        if (useAdvancedBlur) {
            // Use advanced blur with screen capture
            BlurRenderer.getInstance().renderBlurBackdrop(
                null, // DrawContext not needed for simple implementation
                x, y, width, height,
                blurIntensity,
                tintColor
            );
        } else {
            // Use simple blur effect
            BlurRenderer.getInstance().renderSimpleBlurBackdrop(
                x, y, width, height,
                blurIntensity,
                tintColor
            );
        }
    }
    
    // Getters and setters
    public float getBlurIntensity() {
        return blurIntensity;
    }
    
    public void setBlurIntensity(float blurIntensity) {
        this.blurIntensity = Math.max(0.0f, Math.min(5.0f, blurIntensity));
    }
    
    public SettingColor getTintColor() {
        return tintColor;
    }
    
    public void setTintColor(SettingColor tintColor) {
        this.tintColor = tintColor;
    }
    
    public boolean isUseAdvancedBlur() {
        return useAdvancedBlur;
    }
    
    public void setUseAdvancedBlur(boolean useAdvancedBlur) {
        this.useAdvancedBlur = useAdvancedBlur;
    }
    
    /**
     * Renders the blur backdrop directly in HUD context (outside of GUI)
     * This method can be used by HUD modules to render blur backdrops
     */
    public void renderHudBlur(double x, double y, double width, double height, 
                             float blurIntensity, SettingColor tintColor, boolean useAdvancedBlur) {
        // Convert SettingColor to Color
        Color blurColor = new Color(
            tintColor.r,
            tintColor.g,
            tintColor.b,
            tintColor.a
        );
        
        if (useAdvancedBlur) {
            // Use advanced blur with screen capture
            BlurRenderer.getInstance().renderBlurBackdrop(
                null, // DrawContext not needed for simple implementation
                x, y, width, height,
                blurIntensity,
                blurColor
            );
        } else {
            // Use simple blur effect
            BlurRenderer.getInstance().renderSimpleBlurBackdrop(
                x, y, width, height,
                blurIntensity,
                blurColor
            );
        }
    }
}
