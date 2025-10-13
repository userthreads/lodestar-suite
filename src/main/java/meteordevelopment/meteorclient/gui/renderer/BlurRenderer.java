/*
 * This file is part of the Lodestar Suite distribution (https://github.com/waythread/lodestar-suite).
 * Copyright (c) waythread.
 */

package meteordevelopment.meteorclient.gui.renderer;

import meteordevelopment.meteorclient.renderer.Renderer2D;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.client.gui.DrawContext;

public class BlurRenderer {
    private static final BlurRenderer INSTANCE = new BlurRenderer();
    
    private static boolean initialized = false;
    
    public static BlurRenderer getInstance() {
        if (!initialized) {
            initialize();
            initialized = true;
        }
        return INSTANCE;
    }
    
    private static void initialize() {
        // Initialize the blur renderer
        // No specific initialization needed for the simple implementation
    }
    
    /**
     * Renders a blurred backdrop for UI elements
     * @param drawContext The draw context
     * @param x X position of the blur area
     * @param y Y position of the blur area
     * @param width Width of the blur area
     * @param height Height of the blur area
     * @param intensity Blur intensity (0.0 - 5.0)
     * @param tintColor Color to tint the blur effect
     */
    public void renderBlurBackdrop(DrawContext drawContext, double x, double y, double width, double height, float intensity, Color tintColor) {
        // For now, use the simple blur method
        renderSimpleBlurBackdrop(x, y, width, height, intensity, tintColor);
    }
    
    /**
     * Renders a simple blur backdrop without capturing screen content
     * This creates a semi-transparent backdrop that simulates blur
     */
    public void renderSimpleBlurBackdrop(double x, double y, double width, double height, float intensity, Color tintColor) {
        if (!initialized) {
            initialize();
            initialized = true;
        }
        
        // Create a semi-transparent colored quad with blur-like appearance
        // Adjust alpha based on intensity for a more blur-like effect
        int alpha = (int) (tintColor.a * (0.2f + intensity * 0.15f));
        Color blurColor = new Color(tintColor.r, tintColor.g, tintColor.b, Math.min(255, alpha));
        
        // Use the existing Renderer2D system for proper UI rendering
        Renderer2D.COLOR.begin();
        Renderer2D.COLOR.quad(x, y, width, height, blurColor);
        Renderer2D.COLOR.render();
    }
    
    /**
     * Renders a gradient blur backdrop for a more sophisticated effect
     */
    public void renderGradientBlurBackdrop(double x, double y, double width, double height, float intensity, Color tintColor) {
        if (!initialized) {
            initialize();
            initialized = true;
        }
        
        // Create a gradient effect from center to edges
        int centerAlpha = (int) (tintColor.a * (0.3f + intensity * 0.2f));
        int edgeAlpha = (int) (tintColor.a * (0.1f + intensity * 0.1f));
        
        Color centerColor = new Color(tintColor.r, tintColor.g, tintColor.b, Math.min(255, centerAlpha));
        Color edgeColor = new Color(tintColor.r, tintColor.g, tintColor.b, Math.min(255, edgeAlpha));
        
        // Use the existing Renderer2D system for proper UI rendering
        Renderer2D.COLOR.begin();
        
        // Create gradient quad
        Renderer2D.COLOR.quad(x, y, width, height, edgeColor);
        
        // Add center overlay for more blur effect
        double centerX = x + width * 0.25;
        double centerY = y + height * 0.25;
        double centerWidth = width * 0.5;
        double centerHeight = height * 0.5;
        
        Renderer2D.COLOR.quad(centerX, centerY, centerWidth, centerHeight, centerColor);
        
        Renderer2D.COLOR.render();
    }
    
    public void cleanup() {
        initialized = false;
    }
}
