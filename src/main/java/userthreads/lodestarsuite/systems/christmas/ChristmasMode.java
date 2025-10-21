/*
 * This file is part of the Lodestar Suite distribution (https://github.com/userthreads/lodestar-suite).
 * Copyright (c) userthreads.
 */

package userthreads.lodestarsuite.systems.christmas;

import userthreads.lodestarsuite.LodestarSuite;
import userthreads.lodestarsuite.events.world.TickEvent;
import userthreads.lodestarsuite.settings.BoolSetting;
import userthreads.lodestarsuite.settings.Setting;
import userthreads.lodestarsuite.settings.SettingGroup;
import userthreads.lodestarsuite.settings.Settings;
import userthreads.lodestarsuite.systems.System;
import userthreads.lodestarsuite.systems.timezone.TimezoneManager;
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
    
    public final Setting<Boolean> useLocalTimezone = sgGeneral.add(new BoolSetting.Builder()
        .name("use-local-timezone")
        .description("Use your local timezone instead of UTC for date checking.")
        .defaultValue(true)
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
        // Use timezone-aware date checking
        LocalDate now;
        if (useLocalTimezone.get() && TimezoneManager.get() != null) {
            now = TimezoneManager.get().getLocalDate();
        } else {
            // Fallback to UTC for consistent checking across time zones
            now = LocalDate.now(ZoneOffset.UTC);
        }
        
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
            String timezoneInfo = useLocalTimezone.get() && TimezoneManager.get() != null ? 
                TimezoneManager.get().formatTimeWithTimezone() : 
                "UTC: " + LocalDate.now(ZoneOffset.UTC);
            
            LodestarSuite.LOG.info("ChristmasMode.isActive() - enabled: {}, isChristmasSeason: {}, active: {} ({})", 
                enabled.get(), isChristmasSeason, active, timezoneInfo);
            lastLogTime = currentTime;
        }
        
        return active;
    }


}
