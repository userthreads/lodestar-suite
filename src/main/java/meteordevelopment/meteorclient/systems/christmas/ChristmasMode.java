/*
 * This file is part of the Lodestar Suite distribution (https://github.com/waythread/lodestar-suite).
 * Copyright (c) waythread.
 */

package meteordevelopment.meteorclient.systems.christmas;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.Settings;
import meteordevelopment.meteorclient.systems.System;
import meteordevelopment.orbit.EventHandler;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneOffset;

public class ChristmasMode extends System<ChristmasMode> {
    public static final ChristmasMode INSTANCE = new ChristmasMode();

    private boolean isChristmasSeason = false;
    private long lastCheckTime = 0;
    private long lastLogTime = 0;
    private static final long CHECK_INTERVAL = 12 * 60 * 60 * 1000L; // 12 hours in milliseconds
    private static final long LOG_INTERVAL = 5 * 60 * 1000L; // 5 minutes for debug logs
    
    // Settings
    public final Settings settings = new Settings();
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    
    public final Setting<Boolean> enabled = sgGeneral.add(new BoolSetting.Builder()
        .name("enabled")
        .description("Enable Christmas theme regardless of date.")
        .defaultValue(false)
        .build()
    );

    public ChristmasMode() {
        super("christmas");
    }

    public static ChristmasMode get() {
        return INSTANCE;
    }

    @Override
    public void init() {
        super.init();
        checkChristmasSeason();
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        // Only check every 12 hours to reduce unnecessary computations
        long currentTime = Instant.now().toEpochMilli();
        if (currentTime - lastCheckTime >= CHECK_INTERVAL) {
            checkChristmasSeason();
            lastCheckTime = currentTime;
        }
    }


    private void checkChristmasSeason() {
        // Use UTC time for consistent checking across time zones
        LocalDate now = LocalDate.now(ZoneOffset.UTC);
        
        // Check if we're in Christmas season (December 1 - December 30)
        boolean isChristmasSeason = (now.getMonth() == Month.DECEMBER && now.getDayOfMonth() >= 1 && now.getDayOfMonth() <= 30);
        
        this.isChristmasSeason = isChristmasSeason;
    }


    public boolean isChristmasSeason() {
        return isChristmasSeason;
    }
    
    public boolean isActive() {
        boolean active = enabled.get() || isChristmasSeason;
        
        // Rate limit debug logs to every 5 minutes to reduce console spam
        long currentTime = Instant.now().toEpochMilli();
        if (currentTime - lastLogTime >= LOG_INTERVAL) {
            MeteorClient.LOG.info("ChristmasMode.isActive() - enabled: {}, isChristmasSeason: {}, active: {} (UTC: {})", 
                enabled.get(), isChristmasSeason, active, LocalDate.now(ZoneOffset.UTC));
            lastLogTime = currentTime;
        }
        
        return active;
    }


}
