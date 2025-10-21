/*
 * This file is part of the Lodestar Suite distribution (https://github.com/userthreads/lodestar-suite).
 * Copyright (c) userthreads.
 */

package userthreads.lodestarsuite.systems.timezone;

import userthreads.lodestarsuite.LodestarSuite;
import userthreads.lodestarsuite.settings.BoolSetting;
import userthreads.lodestarsuite.settings.Setting;
import userthreads.lodestarsuite.settings.SettingGroup;
import userthreads.lodestarsuite.settings.Settings;
import userthreads.lodestarsuite.settings.StringSetting;
import userthreads.lodestarsuite.systems.System;
import meteordevelopment.orbit.EventHandler;
import userthreads.lodestarsuite.events.world.TickEvent;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.zone.ZoneRules;
import java.util.*;

public class TimezoneManager extends System<TimezoneManager> {
    public static final TimezoneManager INSTANCE = new TimezoneManager();
    
    // Settings
    public final Settings settings = new Settings();
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    
    public final Setting<String> timezone = sgGeneral.add(new StringSetting.Builder()
        .name("timezone")
        .description("Your local timezone for seasonal themes and time display.")
        .defaultValue("UTC")
        .build()
    );
    
    public final Setting<Boolean> autoDetectTimezone = sgGeneral.add(new BoolSetting.Builder()
        .name("auto-detect-timezone")
        .description("Automatically detect your system timezone.")
        .defaultValue(true)
        .onChanged(this::onAutoDetectChanged)
        .build()
    );
    
    public final Setting<Boolean> showTimezoneInfo = sgGeneral.add(new BoolSetting.Builder()
        .name("show-timezone-info")
        .description("Show timezone information in debug logs.")
        .defaultValue(false)
        .build()
    );
    
    public final Setting<Boolean> useInternetTime = sgGeneral.add(new BoolSetting.Builder()
        .name("use-internet-time")
        .description("Use internet time synchronization for accurate seasonal themes.")
        .defaultValue(true)
        .build()
    );
    
    public final Setting<Boolean> useLocationDetection = sgGeneral.add(new BoolSetting.Builder()
        .name("use-location-detection")
        .description("Use location detection for automatic timezone selection.")
        .defaultValue(true)
        .build()
    );
    
    private ZoneId currentZone;
    private boolean isDSTActive = false;
    private long lastTimezoneCheck = 0;
    private static final long TIMEZONE_CHECK_INTERVAL = 60 * 60 * 1000L; // 1 hour
    
    public TimezoneManager() {
        super("timezone");
    }
    
    public static TimezoneManager get() {
        return INSTANCE;
    }
    
    @Override
    public void init() {
        super.init();
        updateTimezone();
    }
    
    @EventHandler
    private void onTick(TickEvent.Post event) {
        // Check timezone changes every hour (for DST transitions)
        long currentTime = java.lang.System.currentTimeMillis();
        if (currentTime - lastTimezoneCheck >= TIMEZONE_CHECK_INTERVAL) {
            updateTimezone();
            lastTimezoneCheck = currentTime;
        }
    }
    
    private void onAutoDetectChanged(boolean enabled) {
        if (enabled) {
            autoDetectSystemTimezone();
        }
    }
    
    private void updateTimezone() {
        try {
            String timezoneId = timezone.get();
            currentZone = ZoneId.of(timezoneId);
            
            // Check if DST is currently active
            ZoneRules rules = currentZone.getRules();
            Instant now = Instant.now();
            isDSTActive = rules.isDaylightSavings(now);
            
            if (showTimezoneInfo.get()) {
                ZonedDateTime zonedNow = ZonedDateTime.now(currentZone);
                String dstStatus = isDSTActive ? "DST Active" : "Standard Time";
                
                LodestarSuite.LOG.info("Timezone: {} | Local Time: {} | UTC Time: {} | {}", 
                    currentZone.getId(),
                    zonedNow.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    Instant.now().atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    dstStatus);
            }
            
        } catch (Exception e) {
            LodestarSuite.LOG.error("Invalid timezone: {}, falling back to UTC", timezone.get(), e);
            currentZone = ZoneOffset.UTC;
            isDSTActive = false;
        }
    }
    
    public void autoDetectSystemTimezone() {
        try {
            // Try location detection first if enabled
            if (useLocationDetection.get() && InternetTimeSync.get() != null && InternetTimeSync.get().isLocationDetected()) {
                String detectedTimezone = InternetTimeSync.get().getDetectedTimezone();
                if (detectedTimezone != null) {
                    try {
                        ZoneId.of(detectedTimezone); // Validate timezone
                        timezone.set(detectedTimezone);
                        LodestarSuite.LOG.info("Auto-detected timezone from location: {} ({})", 
                            detectedTimezone, InternetTimeSync.get().getDetectedLocation());
                        return;
                    } catch (Exception e) {
                        LodestarSuite.LOG.warn("Invalid detected timezone: {}, falling back to system timezone", detectedTimezone);
                    }
                }
            }
            
            // Fallback to system timezone
            ZoneId systemZone = ZoneId.systemDefault();
            timezone.set(systemZone.getId());
            LodestarSuite.LOG.info("Auto-detected system timezone: {}", systemZone.getId());
        } catch (Exception e) {
            LodestarSuite.LOG.error("Failed to auto-detect timezone, using UTC", e);
            timezone.set("UTC");
        }
    }
    
    /**
     * Get the current local date in the user's timezone
     * Uses internet time if available for accuracy
     */
    public LocalDate getLocalDate() {
        if (currentZone == null) {
            return LocalDate.now(ZoneOffset.UTC);
        }
        
        // Use internet time if available and enabled
        if (useInternetTime.get() && InternetTimeSync.get() != null && InternetTimeSync.get().isInternetTimeAvailable()) {
            Instant internetTime = InternetTimeSync.get().getInternetTime();
            return internetTime.atZone(currentZone).toLocalDate();
        }
        
        return LocalDate.now(currentZone);
    }
    
    /**
     * Get the current local time in the user's timezone
     * Uses internet time if available for accuracy
     */
    public LocalTime getLocalTime() {
        if (currentZone == null) {
            return LocalTime.now(ZoneOffset.UTC);
        }
        
        // Use internet time if available and enabled
        if (useInternetTime.get() && InternetTimeSync.get() != null && InternetTimeSync.get().isInternetTimeAvailable()) {
            Instant internetTime = InternetTimeSync.get().getInternetTime();
            return internetTime.atZone(currentZone).toLocalTime();
        }
        
        return LocalTime.now(currentZone);
    }
    
    /**
     * Get the current zoned date time in the user's timezone
     * Uses internet time if available for accuracy
     */
    public ZonedDateTime getLocalDateTime() {
        if (currentZone == null) {
            return ZonedDateTime.now(ZoneOffset.UTC);
        }
        
        // Use internet time if available and enabled
        if (useInternetTime.get() && InternetTimeSync.get() != null && InternetTimeSync.get().isInternetTimeAvailable()) {
            Instant internetTime = InternetTimeSync.get().getInternetTime();
            return internetTime.atZone(currentZone);
        }
        
        return ZonedDateTime.now(currentZone);
    }
    
    /**
     * Check if Daylight Saving Time is currently active
     */
    public boolean isDSTActive() {
        return isDSTActive;
    }
    
    /**
     * Get the current timezone
     */
    public ZoneId getCurrentZone() {
        return currentZone != null ? currentZone : ZoneOffset.UTC;
    }
    
    /**
     * Get timezone offset from UTC
     */
    public ZoneOffset getOffset() {
        if (currentZone == null) {
            return ZoneOffset.UTC;
        }
        return currentZone.getRules().getOffset(Instant.now());
    }
    
    /**
     * Get common timezone suggestions organized by region based on UTC offsets
     * Data sourced from https://www.timeanddate.com/worldclock/
     */
    public static String[] getCommonTimezones() {
        return new String[]{
            // UTC and GMT (Baseline)
            "UTC",                   // UTC+00:00 - Coordinated Universal Time
            "GMT",                   // UTC+00:00 - Greenwich Mean Time
            
            // UTC-12 to UTC-8 (Pacific/Americas)
            "Pacific/Baker_Island",  // UTC-12:00 - Baker Island Time
            "Pacific/Honolulu",      // UTC-10:00 - Hawaii Standard Time (no DST)
            "America/Anchorage",     // UTC-09:00 - Alaska Standard Time (with DST)
            "America/Los_Angeles",   // UTC-08:00 - Pacific Standard Time (with DST)
            "America/Phoenix",       // UTC-07:00 - Mountain Standard Time (no DST)
            
            // UTC-6 to UTC-3 (Americas)
            "America/Denver",        // UTC-07:00 - Mountain Standard Time (with DST)
            "America/Chicago",       // UTC-06:00 - Central Standard Time (with DST)
            "America/New_York",      // UTC-05:00 - Eastern Standard Time (with DST)
            "America/Caracas",       // UTC-04:00 - Venezuela Time (no DST)
            "America/Sao_Paulo",     // UTC-03:00 - Brazil Time (no DST)
            "America/Buenos_Aires",  // UTC-03:00 - Argentina Time (no DST)
            
            // UTC-2 to UTC+0 (Atlantic/Africa)
            "Atlantic/South_Georgia", // UTC-02:00 - South Georgia Time
            "Atlantic/Cape_Verde",   // UTC-01:00 - Cape Verde Time
            "Africa/Accra",          // UTC+00:00 - Ghana Time (no DST)
            "Africa/Casablanca",     // UTC+01:00 - Morocco Time (with DST)
            
            // UTC+1 to UTC+3 (Europe/Africa)
            "Europe/London",         // UTC+00:00 - Greenwich Mean Time (with DST)
            "Europe/Paris",          // UTC+01:00 - Central European Time (with DST)
            "Europe/Berlin",         // UTC+01:00 - Central European Time (with DST)
            "Europe/Rome",           // UTC+01:00 - Central European Time (with DST)
            "Europe/Madrid",         // UTC+01:00 - Central European Time (with DST)
            "Europe/Amsterdam",      // UTC+01:00 - Central European Time (with DST)
            "Europe/Stockholm",      // UTC+01:00 - Central European Time (with DST)
            "Europe/Riga",           // UTC+02:00 - Eastern European Time (with DST)
            "Europe/Moscow",         // UTC+03:00 - Moscow Standard Time (no DST)
            "Africa/Cairo",          // UTC+02:00 - Eastern European Time (with DST)
            "Africa/Johannesburg",   // UTC+02:00 - South Africa Standard Time (no DST)
            "Africa/Lagos",          // UTC+01:00 - West Africa Time (no DST)
            
            // UTC+3 to UTC+6 (Middle East/Asia)
            "Asia/Dubai",            // UTC+04:00 - Gulf Standard Time (no DST)
            "Asia/Tehran",           // UTC+03:30 - Iran Standard Time (no DST)
            "Asia/Karachi",          // UTC+05:00 - Pakistan Standard Time (no DST)
            "Asia/Kolkata",          // UTC+05:30 - India Standard Time (no DST)
            "Asia/Dhaka",            // UTC+06:00 - Bangladesh Standard Time (no DST)
            
            // UTC+6 to UTC+9 (Asia)
            "Asia/Yangon",           // UTC+06:30 - Myanmar Time (no DST)
            "Asia/Bangkok",          // UTC+07:00 - Indochina Time (no DST)
            "Asia/Jakarta",          // UTC+07:00 - Western Indonesia Time (no DST)
            "Asia/Shanghai",         // UTC+08:00 - China Standard Time (no DST)
            "Asia/Hong_Kong",        // UTC+08:00 - Hong Kong Time (no DST)
            "Asia/Singapore",        // UTC+08:00 - Singapore Time (no DST)
            "Asia/Tokyo",            // UTC+09:00 - Japan Standard Time (no DST)
            "Asia/Seoul",            // UTC+09:00 - Korea Standard Time (no DST)
            
            // UTC+9 to UTC+12 (Asia/Pacific)
            "Australia/Adelaide",    // UTC+09:30 - Australian Central Standard Time (with DST)
            "Australia/Darwin",      // UTC+09:30 - Australian Central Standard Time (no DST)
            "Australia/Brisbane",    // UTC+10:00 - Australian Eastern Standard Time (no DST)
            "Australia/Sydney",      // UTC+10:00 - Australian Eastern Standard Time (with DST)
            "Australia/Melbourne",   // UTC+10:00 - Australian Eastern Standard Time (with DST)
            "Australia/Perth",       // UTC+08:00 - Australian Western Standard Time (no DST)
            "Pacific/Auckland",      // UTC+12:00 - New Zealand Standard Time (with DST)
            "Pacific/Fiji",          // UTC+12:00 - Fiji Time (no DST)
            
            // Special cases and islands
            "Pacific/Kiritimati",    // UTC+14:00 - Line Islands Time (no DST)
            "Atlantic/Azores",       // UTC-01:00 - Azores Time (with DST)
            "Indian/Mauritius",      // UTC+04:00 - Mauritius Time (no DST)
            "Antarctica/McMurdo"     // UTC+12:00 - New Zealand Standard Time (with DST)
        };
    }
    
    /**
     * Get timezones organized by UTC offset regions
     * Data sourced from https://www.timeanddate.com/worldclock/
     */
    public static Map<String, List<String>> getTimezonesByRegion() {
        Map<String, List<String>> regions = new LinkedHashMap<>();
        
        // UTC-12 to UTC-8 (Pacific/Americas West)
        regions.put("UTC-12 to UTC-8 (Pacific/Americas West)", Arrays.asList(
            "Pacific/Baker_Island",  // UTC-12:00
            "Pacific/Honolulu",      // UTC-10:00 (no DST)
            "America/Anchorage",     // UTC-09:00 (with DST)
            "America/Los_Angeles",   // UTC-08:00 (with DST)
            "America/Phoenix"        // UTC-07:00 (no DST)
        ));
        
        // UTC-7 to UTC-3 (Americas)
        regions.put("UTC-7 to UTC-3 (Americas)", Arrays.asList(
            "America/Denver",        // UTC-07:00 (with DST)
            "America/Chicago",       // UTC-06:00 (with DST)
            "America/New_York",      // UTC-05:00 (with DST)
            "America/Caracas",       // UTC-04:00 (no DST)
            "America/Sao_Paulo",     // UTC-03:00 (no DST)
            "America/Buenos_Aires"   // UTC-03:00 (no DST)
        ));
        
        // UTC-2 to UTC+0 (Atlantic/Africa West)
        regions.put("UTC-2 to UTC+0 (Atlantic/Africa West)", Arrays.asList(
            "Atlantic/South_Georgia", // UTC-02:00
            "Atlantic/Cape_Verde",   // UTC-01:00
            "UTC",                   // UTC+00:00
            "GMT",                   // UTC+00:00
            "Africa/Accra",          // UTC+00:00 (no DST)
            "Europe/London"          // UTC+00:00 (with DST)
        ));
        
        // UTC+1 to UTC+3 (Europe/Africa)
        regions.put("UTC+1 to UTC+3 (Europe/Africa)", Arrays.asList(
            "Europe/Paris",          // UTC+01:00 (with DST)
            "Europe/Berlin",         // UTC+01:00 (with DST)
            "Europe/Rome",           // UTC+01:00 (with DST)
            "Europe/Madrid",         // UTC+01:00 (with DST)
            "Europe/Amsterdam",      // UTC+01:00 (with DST)
            "Europe/Stockholm",      // UTC+01:00 (with DST)
            "Europe/Riga",           // UTC+02:00 (with DST)
            "Africa/Casablanca",     // UTC+01:00 (with DST)
            "Africa/Lagos",          // UTC+01:00 (no DST)
            "Africa/Johannesburg",   // UTC+02:00 (no DST)
            "Europe/Moscow"          // UTC+03:00 (no DST)
        ));
        
        // UTC+3 to UTC+6 (Middle East/Asia West)
        regions.put("UTC+3 to UTC+6 (Middle East/Asia West)", Arrays.asList(
            "Asia/Dubai",            // UTC+04:00 (no DST)
            "Asia/Tehran",           // UTC+03:30 (no DST)
            "Asia/Karachi",          // UTC+05:00 (no DST)
            "Asia/Kolkata",          // UTC+05:30 (no DST)
            "Asia/Dhaka"             // UTC+06:00 (no DST)
        ));
        
        // UTC+6 to UTC+9 (Asia)
        regions.put("UTC+6 to UTC+9 (Asia)", Arrays.asList(
            "Asia/Yangon",           // UTC+06:30 (no DST)
            "Asia/Bangkok",          // UTC+07:00 (no DST)
            "Asia/Jakarta",          // UTC+07:00 (no DST)
            "Asia/Shanghai",         // UTC+08:00 (no DST)
            "Asia/Hong_Kong",        // UTC+08:00 (no DST)
            "Asia/Singapore",        // UTC+08:00 (no DST)
            "Asia/Tokyo",            // UTC+09:00 (no DST)
            "Asia/Seoul"             // UTC+09:00 (no DST)
        ));
        
        // UTC+9 to UTC+12 (Asia/Pacific)
        regions.put("UTC+9 to UTC+12 (Asia/Pacific)", Arrays.asList(
            "Australia/Adelaide",    // UTC+09:30 (with DST)
            "Australia/Darwin",      // UTC+09:30 (no DST)
            "Australia/Brisbane",    // UTC+10:00 (no DST)
            "Australia/Sydney",      // UTC+10:00 (with DST)
            "Australia/Melbourne",   // UTC+10:00 (with DST)
            "Australia/Perth",       // UTC+08:00 (no DST)
            "Pacific/Auckland",      // UTC+12:00 (with DST)
            "Pacific/Fiji"           // UTC+12:00 (no DST)
        ));
        
        // UTC+12 to UTC+14 (Pacific/Islands)
        regions.put("UTC+12 to UTC+14 (Pacific/Islands)", Arrays.asList(
            "Pacific/Kiritimati",    // UTC+14:00 (no DST)
            "Atlantic/Azores",       // UTC-01:00 (with DST)
            "Indian/Mauritius",      // UTC+04:00 (no DST)
            "Antarctica/McMurdo"     // UTC+12:00 (with DST)
        ));
        
        return regions;
    }
    
    /**
     * Get all available timezones
     */
    public static List<String> getAllTimezones() {
        Set<String> zoneIds = ZoneId.getAvailableZoneIds();
        List<String> sortedZones = new ArrayList<>(zoneIds);
        sortedZones.sort(String::compareTo);
        return sortedZones;
    }
    
    /**
     * Format time with timezone information
     */
    public String formatTimeWithTimezone() {
        if (currentZone == null) {
            return "UTC";
        }
        
        ZonedDateTime now = ZonedDateTime.now(currentZone);
        String dstStatus = isDSTActive ? "DST" : "STD";
        
        return String.format("%s (%s %s)", 
            now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
            currentZone.getId(),
            dstStatus);
    }
    
    /**
     * Validate if a timezone ID is valid
     */
    public static boolean isValidTimezone(String timezoneId) {
        try {
            ZoneId.of(timezoneId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Get timezone information for a specific timezone
     */
    public static String getTimezoneInfo(String timezoneId) {
        try {
            ZoneId zone = ZoneId.of(timezoneId);
            ZonedDateTime now = ZonedDateTime.now(zone);
            ZoneRules rules = zone.getRules();
            boolean isDST = rules.isDaylightSavings(now.toInstant());
            
            return String.format("%s | %s | %s", 
                timezoneId,
                now.getOffset().toString(),
                isDST ? "DST" : "STD");
        } catch (Exception e) {
            return timezoneId + " | ERROR: " + e.getMessage();
        }
    }
    
    /**
     * Get all timezones that support DST
     */
    public static List<String> getDSTTimezones() {
        List<String> dstTimezones = new ArrayList<>();
        
        for (String timezoneId : getAllTimezones()) {
            try {
                ZoneId zone = ZoneId.of(timezoneId);
                ZoneRules rules = zone.getRules();
                ZonedDateTime now = ZonedDateTime.now(zone);
                
                if (rules.isDaylightSavings(now.toInstant())) {
                    dstTimezones.add(timezoneId);
                }
            } catch (Exception e) {
                // Skip invalid timezones
            }
        }
        
        return dstTimezones;
    }
    
    /**
     * Get all timezones that don't support DST
     */
    public static List<String> getNonDSTTimezones() {
        List<String> nonDSTTimezones = new ArrayList<>();
        
        for (String timezoneId : getAllTimezones()) {
            try {
                ZoneId zone = ZoneId.of(timezoneId);
                ZoneRules rules = zone.getRules();
                ZonedDateTime now = ZonedDateTime.now(zone);
                
                if (!rules.isDaylightSavings(now.toInstant())) {
                    nonDSTTimezones.add(timezoneId);
                }
            } catch (Exception e) {
                // Skip invalid timezones
            }
        }
        
        return nonDSTTimezones;
    }
    
    /**
     * Get timezone offset from UTC for a specific timezone
     */
    public static String getTimezoneOffset(String timezoneId) {
        try {
            ZoneId zone = ZoneId.of(timezoneId);
            ZonedDateTime now = ZonedDateTime.now(zone);
            return now.getOffset().toString();
        } catch (Exception e) {
            return "ERROR";
        }
    }
    
    /**
     * Check if a timezone is currently in DST
     */
    public static boolean isTimezoneInDST(String timezoneId) {
        try {
            ZoneId zone = ZoneId.of(timezoneId);
            ZoneRules rules = zone.getRules();
            ZonedDateTime now = ZonedDateTime.now(zone);
            return rules.isDaylightSavings(now.toInstant());
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Get timezones by UTC offset range
     * Data sourced from https://www.timeanddate.com/worldclock/
     */
    public static Map<String, List<String>> getTimezonesByUTCOffset() {
        Map<String, List<String>> offsetGroups = new LinkedHashMap<>();
        
        // UTC-12 to UTC-8 (Pacific/Americas West)
        offsetGroups.put("UTC-12 to UTC-8", Arrays.asList(
            "Pacific/Baker_Island", "Pacific/Honolulu", "America/Anchorage", 
            "America/Los_Angeles", "America/Phoenix"
        ));
        
        // UTC-7 to UTC-3 (Americas)
        offsetGroups.put("UTC-7 to UTC-3", Arrays.asList(
            "America/Denver", "America/Chicago", "America/New_York", 
            "America/Caracas", "America/Sao_Paulo", "America/Buenos_Aires"
        ));
        
        // UTC-2 to UTC+0 (Atlantic/Africa West)
        offsetGroups.put("UTC-2 to UTC+0", Arrays.asList(
            "Atlantic/South_Georgia", "Atlantic/Cape_Verde", "UTC", "GMT", 
            "Africa/Accra", "Europe/London"
        ));
        
        // UTC+1 to UTC+3 (Europe/Africa)
        offsetGroups.put("UTC+1 to UTC+3", Arrays.asList(
            "Europe/Paris", "Europe/Berlin", "Europe/Rome", "Europe/Madrid",
            "Europe/Amsterdam", "Europe/Stockholm", "Europe/Riga", "Africa/Casablanca", 
            "Africa/Lagos", "Africa/Johannesburg", "Europe/Moscow"
        ));
        
        // UTC+3 to UTC+6 (Middle East/Asia West)
        offsetGroups.put("UTC+3 to UTC+6", Arrays.asList(
            "Asia/Dubai", "Asia/Tehran", "Asia/Karachi", "Asia/Kolkata", "Asia/Dhaka"
        ));
        
        // UTC+6 to UTC+9 (Asia)
        offsetGroups.put("UTC+6 to UTC+9", Arrays.asList(
            "Asia/Yangon", "Asia/Bangkok", "Asia/Jakarta", "Asia/Shanghai",
            "Asia/Hong_Kong", "Asia/Singapore", "Asia/Tokyo", "Asia/Seoul"
        ));
        
        // UTC+9 to UTC+12 (Asia/Pacific)
        offsetGroups.put("UTC+9 to UTC+12", Arrays.asList(
            "Australia/Adelaide", "Australia/Darwin", "Australia/Brisbane",
            "Australia/Sydney", "Australia/Melbourne", "Australia/Perth",
            "Pacific/Auckland", "Pacific/Fiji"
        ));
        
        // UTC+12 to UTC+14 (Pacific/Islands)
        offsetGroups.put("UTC+12 to UTC+14", Arrays.asList(
            "Pacific/Kiritimati", "Atlantic/Azores", "Indian/Mauritius", "Antarctica/McMurdo"
        ));
        
        return offsetGroups;
    }
    
    /**
     * Get comprehensive timezone information with UTC offset
     * Data sourced from https://www.timeanddate.com/worldclock/
     */
    public static String getDetailedTimezoneInfo(String timezoneId) {
        try {
            ZoneId zone = ZoneId.of(timezoneId);
            ZonedDateTime now = ZonedDateTime.now(zone);
            ZoneRules rules = zone.getRules();
            boolean isDST = rules.isDaylightSavings(now.toInstant());
            ZoneOffset offset = now.getOffset();
            
            String dstStatus = isDST ? "DST" : "STD";
            String offsetStr = offset.toString();
            
            // Get timezone name from timeanddate.com data
            String timezoneName = getTimezoneDisplayName(timezoneId);
            
            return String.format("%s | %s | %s | %s", 
                timezoneName,
                offsetStr,
                dstStatus,
                now.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        } catch (Exception e) {
            return timezoneId + " | ERROR: " + e.getMessage();
        }
    }
    
    /**
     * Get display name for timezone based on timeanddate.com data
     */
    private static String getTimezoneDisplayName(String timezoneId) {
        // Map of timezone IDs to display names from timeanddate.com
        Map<String, String> displayNames = new HashMap<>();
        
        // UTC and GMT
        displayNames.put("UTC", "UTC (Coordinated Universal Time)");
        displayNames.put("GMT", "GMT (Greenwich Mean Time)");
        
        // North America
        displayNames.put("America/New_York", "New York (EST/EDT)");
        displayNames.put("America/Chicago", "Chicago (CST/CDT)");
        displayNames.put("America/Denver", "Denver (MST/MDT)");
        displayNames.put("America/Los_Angeles", "Los Angeles (PST/PDT)");
        displayNames.put("America/Phoenix", "Phoenix (MST)");
        displayNames.put("America/Anchorage", "Anchorage (AKST/AKDT)");
        displayNames.put("Pacific/Honolulu", "Honolulu (HST)");
        
        // South America
        displayNames.put("America/Sao_Paulo", "São Paulo (BRT)");
        displayNames.put("America/Buenos_Aires", "Buenos Aires (ART)");
        displayNames.put("America/Caracas", "Caracas (VET)");
        
        // Europe
        displayNames.put("Europe/London", "London (GMT/BST)");
        displayNames.put("Europe/Paris", "Paris (CET/CEST)");
        displayNames.put("Europe/Berlin", "Berlin (CET/CEST)");
        displayNames.put("Europe/Rome", "Rome (CET/CEST)");
        displayNames.put("Europe/Madrid", "Madrid (CET/CEST)");
        displayNames.put("Europe/Amsterdam", "Amsterdam (CET/CEST)");
        displayNames.put("Europe/Stockholm", "Stockholm (CET/CEST)");
        displayNames.put("Europe/Riga", "Riga (EET/EEST)");
        displayNames.put("Europe/Moscow", "Moscow (MSK)");
        
        // Africa
        displayNames.put("Africa/Cairo", "Cairo (EET/EEST)");
        displayNames.put("Africa/Johannesburg", "Johannesburg (SAST)");
        displayNames.put("Africa/Lagos", "Lagos (WAT)");
        displayNames.put("Africa/Casablanca", "Casablanca (WET/WEST)");
        displayNames.put("Africa/Accra", "Accra (GMT)");
        
        // Asia
        displayNames.put("Asia/Tokyo", "Tokyo (JST)");
        displayNames.put("Asia/Shanghai", "Shanghai (CST)");
        displayNames.put("Asia/Seoul", "Seoul (KST)");
        displayNames.put("Asia/Hong_Kong", "Hong Kong (HKT)");
        displayNames.put("Asia/Singapore", "Singapore (SGT)");
        displayNames.put("Asia/Kolkata", "Kolkata (IST)");
        displayNames.put("Asia/Dubai", "Dubai (GST)");
        displayNames.put("Asia/Tehran", "Tehran (IRST)");
        displayNames.put("Asia/Karachi", "Karachi (PKT)");
        displayNames.put("Asia/Dhaka", "Dhaka (BST)");
        displayNames.put("Asia/Bangkok", "Bangkok (ICT)");
        displayNames.put("Asia/Jakarta", "Jakarta (WIB)");
        displayNames.put("Asia/Yangon", "Yangon (MMT)");
        
        // Australia & Oceania
        displayNames.put("Australia/Sydney", "Sydney (AEST/AEDT)");
        displayNames.put("Australia/Melbourne", "Melbourne (AEST/AEDT)");
        displayNames.put("Australia/Brisbane", "Brisbane (AEST)");
        displayNames.put("Australia/Perth", "Perth (AWST)");
        displayNames.put("Australia/Adelaide", "Adelaide (ACST/ACDT)");
        displayNames.put("Australia/Darwin", "Darwin (ACST)");
        displayNames.put("Pacific/Auckland", "Auckland (NZST/NZDT)");
        displayNames.put("Pacific/Fiji", "Fiji (FJT)");
        
        // Other regions
        displayNames.put("Atlantic/Azores", "Azores (AZOT/AZOST)");
        displayNames.put("Indian/Mauritius", "Mauritius (MUT)");
        displayNames.put("Antarctica/McMurdo", "McMurdo (NZST/NZDT)");
        displayNames.put("Pacific/Kiritimati", "Kiritimati (LINT)");
        displayNames.put("Atlantic/South_Georgia", "South Georgia (GST)");
        displayNames.put("Atlantic/Cape_Verde", "Cape Verde (CVT)");
        
        return displayNames.getOrDefault(timezoneId, timezoneId);
    }
}
