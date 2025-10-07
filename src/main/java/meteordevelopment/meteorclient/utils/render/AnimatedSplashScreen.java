/*
 * This file is part of the Lodestar Client distribution (https://github.com/waythread/lodestar-client).
 * Copyright (c) waythread.
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
    private static final float STAR_SPEED = 2.0f;
    private static final float PULSE_SPEED = 3.0f;
    private static final float MIN_ALPHA = 0.0f;
    private static final float MAX_ALPHA = 1.0f;
    private static final float STAR_LIFETIME = 3.0f; // Stars live for 3 seconds
    private static final float EXPLOSION_INTERVAL = 0.5f; // New explosion every 0.5 seconds
    private static final int STARS_PER_EXPLOSION = 15;
    
    private static final List<Star> stars = new ArrayList<>();
    private static final Random random = new Random();
    private static boolean initialized = false;
    private static float lastExplosionTime = 0.0f;
    
    private static class Star {
        public float x, y;
        public float velocityX, velocityY;
        public float size;
        public float pulsePhase;
        public int color;
        public float lifetime; // How long this star has been alive
        public float maxLifetime; // Maximum lifetime for this star
        
        public Star() {
            reset();
        }
        
        public void reset() {
            // Start from left side of screen
            this.x = -10;
            this.y = random.nextFloat() * mc.getWindow().getScaledHeight();
            
            // Explosion pattern: stars spread out in different directions
            float angle = (random.nextFloat() - 0.5f) * 1.0f; // Spread angle
            float speed = STAR_SPEED + random.nextFloat() * STAR_SPEED;
            
            this.velocityX = speed * MathHelper.cos(angle);
            this.velocityY = speed * MathHelper.sin(angle) * 0.3f; // Less vertical movement
            
            this.size = 0.5f + random.nextFloat() * 2.0f;
            this.pulsePhase = random.nextFloat() * MathHelper.TAU;
            this.color = 0xFFFFFFFF; // White stars
            this.lifetime = 0.0f;
            this.maxLifetime = STAR_LIFETIME + random.nextFloat() * 1.0f; // Random lifetime
        }
        
        public void update(float deltaTime) {
            // Move star
            x += velocityX * deltaTime;
            y += velocityY * deltaTime;
            
            // Update lifetime
            lifetime += deltaTime;
            
            // Update pulse phase
            pulsePhase += PULSE_SPEED * deltaTime;
            
            // Remove star if it goes off screen or lifetime expires
            if (x > mc.getWindow().getScaledWidth() + 10 || 
                y < -10 || y > mc.getWindow().getScaledHeight() + 10 ||
                lifetime > maxLifetime) {
                // Mark for removal instead of resetting
                lifetime = maxLifetime + 1; // Mark as expired
            }
        }
        
        public float getAlpha() {
            // Fade in at start, fade out at end
            float lifeProgress = lifetime / maxLifetime;
            float fadeIn = Math.min(1.0f, lifeProgress * 5.0f); // Quick fade in
            float fadeOut = Math.max(0.0f, 1.0f - (lifeProgress - 0.7f) * 3.0f); // Fade out in last 30%
            
            float baseAlpha = MIN_ALPHA + (MAX_ALPHA - MIN_ALPHA) * (0.5f + 0.5f * MathHelper.sin(pulsePhase));
            return baseAlpha * fadeIn * fadeOut;
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
        lastExplosionTime = 0.0f;
        lastFrameTime = 0.0f;
        
        // Create initial explosion
        createExplosion();
        
        initialized = true;
    }
    
    private static void createExplosion() {
        // Create a burst of stars from the left side
        for (int i = 0; i < STARS_PER_EXPLOSION; i++) {
            Star star = new Star();
            stars.add(star);
        }
    }
    
    private static float lastFrameTime = 0.0f;
    private static final float TARGET_FPS = 30.0f;
    private static final float FRAME_TIME = 1.0f / TARGET_FPS;
    
    public static void render(DrawContext context) {
        if (!Config.get().titleScreenCredits.get()) return;
        
        if (!initialized) init();
        
        // FPS limiting
        float currentTime = System.currentTimeMillis() / 1000.0f;
        if (currentTime - lastFrameTime < FRAME_TIME) {
            return; // Skip this frame to limit FPS
        }
        lastFrameTime = currentTime;
        
        float deltaTime = FRAME_TIME; // Use fixed delta time for consistent animation
        
        // Create new explosions periodically
        if (currentTime - lastExplosionTime >= EXPLOSION_INTERVAL) {
            createExplosion();
            lastExplosionTime = currentTime;
        }
        
        // Update stars and remove expired ones
        stars.removeIf(star -> {
            star.update(deltaTime);
            return star.lifetime > star.maxLifetime;
        });
        
        // Render more visible black background overlay
        context.fill(0, 0, mc.getWindow().getScaledWidth(), mc.getWindow().getScaledHeight(), 0x60000000);
        
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
            
            // Add a more visible glow effect for brighter stars
            if (star.getAlpha() > 0.6f) {
                int glowColor = (int) ((star.getAlpha() * 0.4f * 255)) << 24 | 0xFFFFFF;
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

