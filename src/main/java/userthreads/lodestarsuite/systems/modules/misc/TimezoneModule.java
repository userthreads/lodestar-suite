/*
 * This file is part of the Lodestar Suite distribution (https://github.com/userthreads/lodestar-suite).
 * Copyright (c) userthreads.
 */

package userthreads.lodestarsuite.systems.modules.misc;

import userthreads.lodestarsuite.settings.BoolSetting;
import userthreads.lodestarsuite.settings.Setting;
import userthreads.lodestarsuite.settings.SettingGroup;
import userthreads.lodestarsuite.systems.modules.Categories;
import userthreads.lodestarsuite.systems.modules.Module;
import userthreads.lodestarsuite.systems.timezone.TimezoneManager;

public class TimezoneModule extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    
    public final Setting<Boolean> showTimezoneInfo = sgGeneral.add(new BoolSetting.Builder()
        .name("show-timezone-info")
        .description("Show timezone information in chat.")
        .defaultValue(false)
        .build()
    );
    
    public final Setting<Boolean> autoDetectTimezone = sgGeneral.add(new BoolSetting.Builder()
        .name("auto-detect-timezone")
        .description("Automatically detect your system timezone.")
        .defaultValue(true)
        .onChanged(this::onAutoDetectChanged)
        .build()
    );
    
    public final Setting<Boolean> showDSTInfo = sgGeneral.add(new BoolSetting.Builder()
        .name("show-dst-info")
        .description("Show DST information when toggling module.")
        .defaultValue(true)
        .build()
    );
    
    public final Setting<Boolean> showSeasonalInfo = sgGeneral.add(new BoolSetting.Builder()
        .name("show-seasonal-info")
        .description("Show seasonal theme status when toggling module.")
        .defaultValue(true)
        .build()
    );
    
    public final Setting<Boolean> showInternetSyncInfo = sgGeneral.add(new BoolSetting.Builder()
        .name("show-internet-sync-info")
        .description("Show internet time sync and location detection status.")
        .defaultValue(true)
        .build()
    );

    public TimezoneModule() {
        super(Categories.Misc, "timezone", "Configure timezone settings for seasonal themes and time display.");
    }
    
    private void onAutoDetectChanged(boolean enabled) {
        if (enabled && TimezoneManager.get() != null) {
            TimezoneManager.get().autoDetectSystemTimezone();
        }
    }
    
    @Override
    public void onActivate() {
        if (TimezoneManager.get() == null) {
            error("TimezoneManager not available!");
            return;
        }
        
        // Show timezone information
        if (showTimezoneInfo.get()) {
            String timeInfo = TimezoneManager.get().formatTimeWithTimezone();
            info("Current Timezone: " + timeInfo);
            
            // Show detailed timezone info based on timeanddate.com data
            String detailedInfo = TimezoneManager.getDetailedTimezoneInfo(TimezoneManager.get().getCurrentZone().getId());
            info("Detailed Info: " + detailedInfo);
        }
        
        // Show DST information
        if (showDSTInfo.get()) {
            boolean isDST = TimezoneManager.get().isDSTActive();
            String dstStatus = isDST ? "Daylight Saving Time is active" : "Standard Time is active";
            info("DST Status: " + dstStatus);
            
            String offset = TimezoneManager.get().getOffset().toString();
            info("UTC Offset: " + offset);
            
            // Show UTC reference time
            String utcTime = java.time.Instant.now().atOffset(java.time.ZoneOffset.UTC)
                .format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
            info("UTC Time: " + utcTime);
        }
        
        // Show seasonal theme information
        if (showSeasonalInfo.get()) {
            String halloweenStatus = userthreads.lodestarsuite.systems.halloween.HalloweenMode.get().isActive() ? "Active" : "Inactive";
            String christmasStatus = userthreads.lodestarsuite.systems.christmas.ChristmasMode.get().isActive() ? "Active" : "Inactive";
            
            info("Halloween Theme: " + halloweenStatus);
            info("Christmas Theme: " + christmasStatus);
        }
        
        // Show internet time sync and location detection info
        if (showInternetSyncInfo.get() && userthreads.lodestarsuite.systems.timezone.InternetTimeSync.get() != null) {
            String syncStatus = userthreads.lodestarsuite.systems.timezone.InternetTimeSync.get().getSyncStatus();
            info("Internet Sync Status: " + syncStatus);
        }
        
        // Show timezone statistics
        if (showTimezoneInfo.get()) {
            int totalTimezones = TimezoneManager.getAllTimezones().size();
            int dstTimezones = TimezoneManager.getDSTTimezones().size();
            int nonDSTTimezones = TimezoneManager.getNonDSTTimezones().size();
            
            info("Timezone Statistics: " + totalTimezones + " total, " + dstTimezones + " in DST, " + nonDSTTimezones + " standard");
        }
    }
    
    @Override
    public void onDeactivate() {
        if (showTimezoneInfo.get()) {
            info("Timezone module deactivated. Seasonal themes will continue to work based on your configured timezone.");
        }
    }
}
