/*
 * This file is part of the Lodestar Client distribution (https://github.com/waythread/lodestar-client).
 * Copyright (c) waythread.
 */

package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.renderer.Renderer2D;
import meteordevelopment.meteorclient.systems.christmas.ChristmasMode;
import meteordevelopment.meteorclient.systems.config.Config;
import meteordevelopment.meteorclient.systems.halloween.HalloweenMode;
import meteordevelopment.meteorclient.utils.render.AnimatedSplashScreen;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {
    private static final Random random = new Random();
    private static final List<Bat> bats = new ArrayList<>();
    private static final List<Snowflake> snowflakes = new ArrayList<>();
    private static long lastRenderLogTime = 0;
    private static final long RENDER_LOG_INTERVAL = 30 * 1000L; // 30 seconds for render logs

    public TitleScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (Config.get().titleScreenCredits.get()) AnimatedSplashScreen.render(context);
        
        // Seasonal animations
        // Rate limit render logs to every 30 seconds
        long currentTime = java.time.Instant.now().toEpochMilli();
        if (currentTime - lastRenderLogTime >= RENDER_LOG_INTERVAL) {
            MeteorClient.LOG.info("TitleScreenMixin.onRender() - TitleScreen render called");
            lastRenderLogTime = currentTime;
        }
        
        // Update and render Halloween bats
        if (HalloweenMode.get().isActive()) {
            updateBats();
            renderBats(context);
        } else {
            bats.clear();
        }
        
        // Update and render Christmas snowflakes
        if (ChristmasMode.get().isActive()) {
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
        float baseSpawnRate = 0.0001f; // Base rate per pixel
        float spawnRate = Math.min(0.08f, baseSpawnRate * screenArea / 100000f); // Cap at 8%
        
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
        float y = -30; // Start higher above screen
        float vx = (random.nextFloat() - 0.5f) * 3; // Slower horizontal movement
        float vy = random.nextFloat() * 1.5f + 0.8f; // Slower downward movement
        
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
        float baseSpawnRate = 0.00008f; // Base rate per pixel (slightly less than bats)
        float spawnRate = Math.min(0.06f, baseSpawnRate * screenArea / 100000f); // Cap at 6%
        
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
        float y = -20; // Start higher above screen
        float vx = (random.nextFloat() - 0.5f) * 0.8f; // Slightly more horizontal drift
        float vy = random.nextFloat() * 0.8f + 0.3f; // Slower downward fall
        
        snowflakes.add(new Snowflake(x, y, vx, vy));
    }

    private void renderSnowflakes(DrawContext context) {
        Renderer2D.COLOR.begin();
        
        for (Snowflake snowflake : snowflakes) {
            snowflake.render();
        }
        
        Renderer2D.COLOR.render();
    }

    // Bat class for Halloween animations
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
            this.color = new Color(255, 165, 0, 200); // Orange bat
        }

        public boolean update() {
            x += vx;
            y += vy;
            life -= 0.002f; // Even slower fade for longer life
            
            // Add some random movement
            vx += (random.nextFloat() - 0.5f) * 0.03f; // Less random movement
            vy += (random.nextFloat() - 0.5f) * 0.03f;
            
            // Clamp velocity
            vx = Math.max(-2.5f, Math.min(2.5f, vx)); // Slower max velocity
            vy = Math.max(-2.5f, Math.min(2.5f, vy));
            
            // Only remove if life is over AND it's fallen far below screen
            int screenHeight = MeteorClient.mc.getWindow().getScaledHeight();
            return life > 0 && y < screenHeight + 100; // Allow falling 100px below screen
        }

        public void render() {
            if (life <= 0) return;

            // Simple bat shape - two triangles
            float size = 8 * life;
            float alpha = life * 200;
            
            // Main body (triangle)
            Renderer2D.COLOR.quad(
                x - size/2, y - size/2, size, size,
                new Color((int)(color.r), (int)(color.g), (int)(color.b), (int)alpha)
            );
        }
    }

    // Snowflake class for Christmas animations
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
            this.color = new Color(255, 255, 255, 200); // White snowflake
        }

        public boolean update() {
            x += vx;
            y += vy;
            life -= 0.0008f; // Even slower fade for longer life
            
            // Gentle swaying motion
            vx += (random.nextFloat() - 0.5f) * 0.008f; // Less swaying
            
            // Clamp velocity
            vx = Math.max(-1.2f, Math.min(1.2f, vx)); // Slower max velocity
            vy = Math.max(0.3f, Math.min(1.2f, vy));
            
            // Only remove if life is over AND it's fallen far below screen
            int screenHeight = MeteorClient.mc.getWindow().getScaledHeight();
            return life > 0 && y < screenHeight + 150; // Allow falling 150px below screen
        }

        public void render() {
            if (life <= 0) return;

            // Simple snowflake shape - small white dot
            float size = 4 * life;
            float alpha = life * 200;
            
            Renderer2D.COLOR.quad(
                x - size/2, y - size/2, size, size,
                new Color((int)(color.r), (int)(color.g), (int)(color.b), (int)alpha)
            );
        }
    }
}
