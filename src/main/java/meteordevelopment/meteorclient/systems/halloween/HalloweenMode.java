/*
 * This file is part of the Lodestar Client distribution (https://github.com/waythread/lodestar-client).
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

import java.time.LocalDate;
import java.time.Month;

public class HalloweenMode extends System<HalloweenMode> {
    public static final HalloweenMode INSTANCE = new HalloweenMode();

    private boolean isHalloweenWeek = false;
    
    // Settings
    public final Settings settings = new Settings();
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    
    public final Setting<Boolean> enabled = sgGeneral.add(new BoolSetting.Builder()
        .name("enabled")
        .description("Enable Halloween mode animations regardless of date.")
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
        checkHalloweenWeek();
        
        // Note: Animations are now handled by mixins for better performance
        // This method is kept for potential future use or debugging
    }


    private void checkHalloweenWeek() {
        LocalDate now = LocalDate.now();
        
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
        MeteorClient.LOG.info("HalloweenMode.isActive() - enabled: {}, isHalloweenWeek: {}, active: {}", 
            enabled.get(), isHalloweenWeek, active);
        return active;
    }

}