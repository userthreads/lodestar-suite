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
import meteordevelopment.meteorclient.systems.timezone.TimezoneManager;
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
    
    public final Setting<Boolean> useLocalTimezone = sgGeneral.add(new BoolSetting.Builder()
        .name("use-local-timezone")
        .description("Use your local timezone instead of UTC for date checking.")
        .defaultValue(true)
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
        // Use timezone-aware date checking
        LocalDate now;
        if (useLocalTimezone.get() && TimezoneManager.get() != null) {
            now = TimezoneManager.get().getLocalDate();
        } else {
            // Fallback to UTC for consistent checking across time zones
            now = LocalDate.now(ZoneOffset.UTC);
        }
        
        // Check if we're in Halloween season (October 1 - November 1)
        boolean isHalloweenWeek = (now.getMonth() == Month.OCTOBER) ||
                                 (now.getMonth() == Month.NOVEMBER && now.getDayOfMonth() <= 1);
        
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
            String timezoneInfo = useLocalTimezone.get() && TimezoneManager.get() != null ? 
                TimezoneManager.get().formatTimeWithTimezone() : 
                "UTC: " + LocalDate.now(ZoneOffset.UTC);
            
            MeteorClient.LOG.info("HalloweenMode.isActive() - enabled: {}, isHalloweenWeek: {}, active: {} ({})", 
                enabled.get(), isHalloweenWeek, active, timezoneInfo);
            lastLogTime = currentTime;
        }
        
        return active;
    }

}
