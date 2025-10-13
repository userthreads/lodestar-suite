/*
 * This file is part of the Lodestar Suite distribution (https://github.com/waythread/lodestar-suite).
 * Copyright (c) waythread.
 */

package meteordevelopment.meteorclient.systems.halloween;

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

public class HalloweenMode extends System<HalloweenMode> {
    public static final HalloweenMode INSTANCE = new HalloweenMode();

    private boolean isHalloweenWeek = false;
    private long lastCheckTime = 0;
    private long lastLogTime = 0;
    private static final long CHECK_INTERVAL = 12 * 60 * 60 * 1000L; // 12 hours in milliseconds
    private static final long LOG_INTERVAL = 5 * 60 * 1000L; // 5 minutes for debug logs
    
    // Settings
    public final Settings settings = new Settings();
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    
    public final Setting<Boolean> enabled = sgGeneral.add(new BoolSetting.Builder()
        .name("enabled")
        .description("Enable Halloween theme regardless of date.")
        .defaultValue(false)
        .build()
    );

    public HalloweenMode() {
        super("halloween");
    }

    public static HalloweenMode get() {
        return INSTANCE;
    }

    @Override
    public void init() {
        super.init();
        checkHalloweenWeek();
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        // Only check every 12 hours to reduce unnecessary computations
        long currentTime = Instant.now().toEpochMilli();
        if (currentTime - lastCheckTime >= CHECK_INTERVAL) {
            checkHalloweenWeek();
            lastCheckTime = currentTime;
        }
    }


    private void checkHalloweenWeek() {
        // Use UTC time for consistent checking across time zones
        LocalDate now = LocalDate.now(ZoneOffset.UTC);
        
        // Check if we're in Halloween week (October 27 - November 2)
        boolean isHalloweenWeek = (now.getMonth() == Month.OCTOBER && now.getDayOfMonth() >= 27) ||
                                 (now.getMonth() == Month.NOVEMBER && now.getDayOfMonth() <= 2);
        
        this.isHalloweenWeek = isHalloweenWeek;
    }


    public boolean isHalloweenWeek() {
        return isHalloweenWeek;
    }
    
    public boolean isActive() {
        boolean active = enabled.get() || isHalloweenWeek;
        
        // Rate limit debug logs to every 5 minutes to reduce console spam
        long currentTime = Instant.now().toEpochMilli();
        if (currentTime - lastLogTime >= LOG_INTERVAL) {
            MeteorClient.LOG.info("HalloweenMode.isActive() - enabled: {}, isHalloweenWeek: {}, active: {} (UTC: {})", 
                enabled.get(), isHalloweenWeek, active, LocalDate.now(ZoneOffset.UTC));
            lastLogTime = currentTime;
        }
        
        return active;
    }

}
