/*
 * This file is part of the Lodestar Client distribution (https://github.com/waythread/lodestar-client).
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

import java.time.LocalDate;
import java.time.Month;

public class ChristmasMode extends System<ChristmasMode> {
    public static final ChristmasMode INSTANCE = new ChristmasMode();

    private boolean isChristmasSeason = false;
    
    // Settings
    public final Settings settings = new Settings();
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    
    public final Setting<Boolean> enabled = sgGeneral.add(new BoolSetting.Builder()
        .name("enabled")
        .description("Enable Christmas mode animations regardless of date.")
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
        checkChristmasSeason();
        
        // Note: Animations are now handled by mixins for better performance
        // This method is kept for potential future use or debugging
    }


    private void checkChristmasSeason() {
        LocalDate now = LocalDate.now();
        
        // Check if we're in Christmas season (December 20 - January 6)
        boolean isChristmasSeason = (now.getMonth() == Month.DECEMBER && now.getDayOfMonth() >= 20) ||
                                   (now.getMonth() == Month.JANUARY && now.getDayOfMonth() <= 6);
        
        this.isChristmasSeason = isChristmasSeason;
    }


    public boolean isChristmasSeason() {
        return isChristmasSeason;
    }
    
    public boolean isActive() {
        boolean active = enabled.get() || isChristmasSeason;
        MeteorClient.LOG.info("ChristmasMode.isActive() - enabled: {}, isChristmasSeason: {}, active: {}", 
            enabled.get(), isChristmasSeason, active);
        return active;
    }


}
