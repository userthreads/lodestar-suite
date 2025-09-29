/*
 * This file is part of the Lodestar Client distribution (https://github.com/copiuum/lodestar-client).
 * Copyright (c) copiuum.
 */

package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.systems.christmas.ChristmasMode;
import meteordevelopment.meteorclient.systems.halloween.HalloweenMode;
import meteordevelopment.meteorclient.renderer.Renderer2D;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.gui.WidgetScreen;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Mixin(WidgetScreen.class)
public abstract class InGameAnimationsMixin extends Screen {
    private static final Random random = new Random();
    private static final List<Bat> bats = new ArrayList<>();
    private static final List<Snowflake> snowflakes = new ArrayList<>();

    public InGameAnimationsMixin(Text title) {
        super(title);
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        MeteorClient.LOG.info("InGameAnimationsMixin.onRender() - WidgetScreen render called");
        
        // Update and render Halloween bats
        if (HalloweenMode.get().isActive()) {
            MeteorClient.LOG.info("Halloween mode active in-game, updating bats");
            updateBats();
            renderBats(context);
        } else {
            bats.clear();
        }
        
        // Update and render Christmas snowflakes
        if (ChristmasMode.get().isActive()) {
            MeteorClient.LOG.info("Christmas mode active in-game, updating snowflakes");
            updateSnowflakes();
            renderSnowflakes(context);
        } else {
            snowflakes.clear();
        }
    }

    private void updateBats() {
        // Dynamic spawn rate based on screen size
        int screenWidth = MeteorClient.mc.getWindow().getScaledWidth();
        int screenHeight = MeteorClient.mc.getWindow().getScaledHeight();
        float screenArea = screenWidth * screenHeight;
        float baseSpawnRate = 0.00006f; // Base rate per pixel (less than panorama)
        float spawnRate = Math.min(0.05f, baseSpawnRate * screenArea / 100000f); // Cap at 5%
        
        // Spawn new bats with dynamic rate
        if (random.nextFloat() < spawnRate) {
            spawnBat();
        }
        
        // Update existing bats
        bats.removeIf(bat -> !bat.update());
    }

    private void spawnBat() {
        int screenWidth = MeteorClient.mc.getWindow().getScaledWidth();
        
        // Always spawn from top of screen
        float x = random.nextFloat() * screenWidth;
        float y = -25; // Start higher above screen
        float vx = (random.nextFloat() - 0.5f) * 2; // Slower horizontal movement
        float vy = random.nextFloat() * 1.2f + 0.6f; // Slower downward movement
        
        bats.add(new Bat(x, y, vx, vy));
    }

    private void renderBats(DrawContext context) {
        Renderer2D.COLOR.begin();
        
        for (Bat bat : bats) {
            bat.render();
        }
        
        Renderer2D.COLOR.render();
    }

    private void updateSnowflakes() {
        // Dynamic spawn rate based on screen size
        int screenWidth = MeteorClient.mc.getWindow().getScaledWidth();
        int screenHeight = MeteorClient.mc.getWindow().getScaledHeight();
        float screenArea = screenWidth * screenHeight;
        float baseSpawnRate = 0.00004f; // Base rate per pixel (less than panorama)
        float spawnRate = Math.min(0.04f, baseSpawnRate * screenArea / 100000f); // Cap at 4%
        
        // Spawn new snowflakes with dynamic rate
        if (random.nextFloat() < spawnRate) {
            spawnSnowflake();
        }
        
        // Update existing snowflakes
        snowflakes.removeIf(snowflake -> !snowflake.update());
    }

    private void spawnSnowflake() {
        int screenWidth = MeteorClient.mc.getWindow().getScaledWidth();
        
        // Always spawn from top of screen
        float x = random.nextFloat() * screenWidth;
        float y = -15; // Start higher above screen
        float vx = (random.nextFloat() - 0.5f) * 0.5f; // Slightly more horizontal drift
        float vy = random.nextFloat() * 0.5f + 0.1f; // Slower downward fall
        
        snowflakes.add(new Snowflake(x, y, vx, vy));
    }

    private void renderSnowflakes(DrawContext context) {
        Renderer2D.COLOR.begin();
        
        for (Snowflake snowflake : snowflakes) {
            snowflake.render();
        }
        
        Renderer2D.COLOR.render();
    }

    // Inner classes for animations
    private static class Bat {
        private float x, y, vx, vy;
        private float life = 1.0f;
        private final Color color;
        private final Random random = new Random();

        public Bat(float x, float y, float vx, float vy) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
            this.color = new Color(255, 165, 0, 150); // Orange bat (more transparent in-game)
        }

        public boolean update() {
            x += vx;
            y += vy;
            life -= 0.003f; // Even slower fade for longer life
            
            // Add some random movement
            vx += (random.nextFloat() - 0.5f) * 0.02f; // Less random movement
            vy += (random.nextFloat() - 0.5f) * 0.02f;
            
            // Clamp velocity
            vx = Math.max(-2.2f, Math.min(2.2f, vx)); // Slower max velocity
            vy = Math.max(-2.2f, Math.min(2.2f, vy));
            
            // Only remove if life is over AND it's fallen far below screen
            int screenHeight = MeteorClient.mc.getWindow().getScaledHeight();
            return life > 0 && y < screenHeight + 80; // Allow falling 80px below screen
        }

        public void render() {
            if (life <= 0) return;

            // Simple bat shape - two triangles
            float size = 6 * life;
            float alpha = life * 150;
            
            // Body
            Renderer2D.COLOR.quad(
                x - size/2, y - size/2, size, size,
                new Color((int)(color.r), (int)(color.g), (int)(color.b), (int)alpha)
            );
        }
    }

    private static class Snowflake {
        private float x, y, vx, vy;
        private float life = 1.0f;
        private final Color color;
        private final Random random = new Random();

        public Snowflake(float x, float y, float vx, float vy) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
            this.color = new Color(255, 255, 255, 150); // White snowflake (more transparent in-game)
        }

        public boolean update() {
            x += vx;
            y += vy;
            life -= 0.0015f; // Even slower fade for longer life
            
            // Gentle swaying motion
            vx += (random.nextFloat() - 0.5f) * 0.004f; // Less swaying
            
            // Clamp velocity
            vx = Math.max(-0.7f, Math.min(0.7f, vx)); // Slower max velocity
            vy = Math.max(0.2f, Math.min(0.7f, vy));
            
            // Only remove if life is over AND it's fallen far below screen
            int screenHeight = MeteorClient.mc.getWindow().getScaledHeight();
            return life > 0 && y < screenHeight + 100; // Allow falling 100px below screen
        }

        public void render() {
            if (life <= 0) return;

            // Simple snowflake shape - small white dot
            float size = 2 * life;
            float alpha = life * 150;
            
            Renderer2D.COLOR.quad(
                x - size/2, y - size/2, size, size,
                new Color((int)(color.r), (int)(color.g), (int)(color.b), (int)alpha)
            );
        }
    }
}
