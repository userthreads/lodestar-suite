/*
 * This file is part of the Lodestar Client distribution (https://github.com/copiuum/lodestar-client).
 * Copyright (c) copiuum.
 */

package meteordevelopment.meteorclient.utils.render;

import meteordevelopment.meteorclient.systems.config.Config;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class AnimatedSplashScreen {
    private static final int STAR_COUNT = 200;
    private static final float STAR_SPEED = 0.3f;
    private static final float PULSE_SPEED = 1.5f;
    private static final float MIN_ALPHA = 0.05f;
    private static final float MAX_ALPHA = 0.8f;
    
    private static final List<Star> stars = new ArrayList<>();
    private static final Random random = new Random();
    private static boolean initialized = false;
    
    private static class Star {
        public float x, y;
        public float velocityX, velocityY;
        public float size;
        public float pulsePhase;
        public int color;
        
        public Star() {
            reset();
        }
        
        public void reset() {
            // Start from left side of screen
            this.x = -10;
            this.y = random.nextFloat() * mc.getWindow().getScaledHeight();
            // Move primarily left to right with slight vertical variation
            this.velocityX = STAR_SPEED + random.nextFloat() * STAR_SPEED;
            this.velocityY = (random.nextFloat() - 0.5f) * STAR_SPEED * 0.3f;
            this.size = 0.3f + random.nextFloat() * 1.2f;
            this.pulsePhase = random.nextFloat() * MathHelper.TAU;
            this.color = 0xFFFFFFFF; // White stars
        }
        
        public void update(float deltaTime) {
            // Move star
            x += velocityX * deltaTime;
            y += velocityY * deltaTime;
            
            // Update pulse phase
            pulsePhase += PULSE_SPEED * deltaTime;
            
            // Reset if star goes off screen (only check right side since they start from left)
            if (x > mc.getWindow().getScaledWidth() + 10 || 
                y < -10 || y > mc.getWindow().getScaledHeight() + 10) {
                reset();
            }
        }
        
        public float getAlpha() {
            return MIN_ALPHA + (MAX_ALPHA - MIN_ALPHA) * (0.5f + 0.5f * MathHelper.sin(pulsePhase));
        }
        
        public int getColor() {
            float alpha = getAlpha();
            int alphaInt = (int) (alpha * 255);
            return (alphaInt << 24) | (color & 0xFFFFFF);
        }
    }
    
    public static void init() {
        if (initialized) return;
        
        stars.clear();
        for (int i = 0; i < STAR_COUNT; i++) {
            stars.add(new Star());
        }
        
        initialized = true;
    }
    
    public static void render(DrawContext context) {
        if (!Config.get().titleScreenCredits.get()) return;
        
        if (!initialized) init();
        
        float deltaTime = 1.0f / 60.0f; // Approximate 60 FPS
        
        // Update stars
        for (Star star : stars) {
            star.update(deltaTime);
        }
        
        // Render subtle black background overlay (more transparent)
        context.fill(0, 0, mc.getWindow().getScaledWidth(), mc.getWindow().getScaledHeight(), 0x20000000);
        
        // Render stars
        for (Star star : stars) {
            int color = star.getColor();
            int size = (int) star.size;
            
            // Render star as a small filled rectangle
            context.fill(
                (int) (star.x - size/2), 
                (int) (star.y - size/2), 
                (int) (star.x + size/2), 
                (int) (star.y + size/2), 
                color
            );
            
            // Add a subtle glow effect for brighter stars
            if (star.getAlpha() > 0.5f) {
                int glowColor = (int) ((star.getAlpha() * 0.2f * 255)) << 24 | 0xFFFFFF;
                context.fill(
                    (int) (star.x - size), 
                    (int) (star.y - size), 
                    (int) (star.x + size), 
                    (int) (star.y + size), 
                    glowColor
                );
            }
        }
    }
    
    public static void reset() {
        initialized = false;
        stars.clear();
    }
}
