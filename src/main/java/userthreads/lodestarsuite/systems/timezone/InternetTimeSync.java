/*
 * This file is part of the Lodestar Suite distribution (https://github.com/userthreads/lodestar-suite).
 * Copyright (c) userthreads.
 */

package userthreads.lodestarsuite.systems.timezone;

import userthreads.lodestarsuite.LodestarSuite;
import userthreads.lodestarsuite.settings.BoolSetting;
import userthreads.lodestarsuite.settings.IntSetting;
import userthreads.lodestarsuite.settings.Setting;
import userthreads.lodestarsuite.settings.SettingGroup;
import userthreads.lodestarsuite.settings.Settings;
import userthreads.lodestarsuite.systems.System;
import meteordevelopment.orbit.EventHandler;
import userthreads.lodestarsuite.events.world.TickEvent;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;

public class InternetTimeSync extends System<InternetTimeSync> {
    public static final InternetTimeSync INSTANCE = new InternetTimeSync();
    
    // Settings
    public final Settings settings = new Settings();
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    
    public final Setting<Boolean> enabled = sgGeneral.add(new BoolSetting.Builder()
        .name("enabled")
        .description("Enable internet time synchronization for accurate seasonal themes.")
        .defaultValue(true)
        .build()
    );
    
    public final Setting<Boolean> autoDetectLocation = sgGeneral.add(new BoolSetting.Builder()
        .name("auto-detect-location")
        .description("Automatically detect your location for timezone detection.")
        .defaultValue(true)
        .build()
    );
    
    public final Setting<Integer> syncInterval = sgGeneral.add(new IntSetting.Builder()
        .name("sync-interval")
        .description("Internet time sync interval in hours.")
        .defaultValue(6)
        .min(1)
        .max(24)
        .build()
    );
    
    public final Setting<Boolean> showSyncInfo = sgGeneral.add(new BoolSetting.Builder()
        .name("show-sync-info")
        .description("Show internet time sync information in debug logs.")
        .defaultValue(false)
        .build()
    );
    
    private HttpClient httpClient;
    private long lastSyncTime = 0;
    private long lastLocationCheck = 0;
    private Instant internetTime = null;
    private String detectedLocation = null;
    private String detectedTimezone = null;
    private static final long LOCATION_CHECK_INTERVAL = 24 * 60 * 60 * 1000L; // 24 hours
    
    public InternetTimeSync() {
        super("internet-time-sync");
    }
    
    public static InternetTimeSync get() {
        return INSTANCE;
    }
    
    @Override
    public void init() {
        super.init();
        httpClient = HttpClient.newBuilder()
            .connectTimeout(java.time.Duration.ofSeconds(10))
            .build();
        
        if (enabled.get()) {
            syncInternetTime();
            if (autoDetectLocation.get()) {
                detectLocation();
            }
        }
    }
    
    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (!enabled.get()) return;
        
        long currentTime = java.lang.System.currentTimeMillis();
        
        // Sync internet time periodically
        if (currentTime - lastSyncTime >= syncInterval.get() * 60 * 60 * 1000L) {
            syncInternetTime();
            lastSyncTime = currentTime;
        }
        
        // Check location periodically
        if (autoDetectLocation.get() && currentTime - lastLocationCheck >= LOCATION_CHECK_INTERVAL) {
            detectLocation();
            lastLocationCheck = currentTime;
        }
    }
    
    /**
     * Synchronize time with internet time servers
     */
    public void syncInternetTime() {
        if (!enabled.get()) return;
        
        CompletableFuture.supplyAsync(() -> {
            try {
                // Try multiple time servers for reliability
                String[] timeServers = {
                    "http://worldtimeapi.org/api/timezone/Etc/UTC",
                    "http://worldtimeapi.org/api/ip",
                    "https://timeapi.io/api/Time/current/zone?timeZone=UTC"
                };
                
                for (String server : timeServers) {
                    try {
                        HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create(server))
                            .timeout(java.time.Duration.ofSeconds(10))
                            .build();
                        
                        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                        
                        if (response.statusCode() == 200) {
                            String responseBody = response.body();
                            Instant serverTime = parseTimeFromResponse(responseBody, server);
                            
                            if (serverTime != null) {
                                internetTime = serverTime;
                                
                                if (showSyncInfo.get()) {
                                    LodestarSuite.LOG.info("Internet time sync successful from {}: {}", 
                                        server, serverTime.atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
                                }
                                
                                return true;
                            }
                        }
                    } catch (Exception e) {
                        if (showSyncInfo.get()) {
                            LodestarSuite.LOG.warn("Failed to sync from {}: {}", server, e.getMessage());
                        }
                    }
                }
                
                return false;
            } catch (Exception e) {
                LodestarSuite.LOG.error("Internet time sync failed", e);
                return false;
            }
        }).thenAccept(success -> {
            if (!success && showSyncInfo.get()) {
                LodestarSuite.LOG.warn("All internet time servers failed, using system time");
            }
        });
    }
    
    /**
     * Detect user location for timezone detection
     */
    public void detectLocation() {
        if (!enabled.get() || !autoDetectLocation.get()) return;
        
        CompletableFuture.supplyAsync(() -> {
            try {
                // Try multiple location services
                String[] locationServices = {
                    "http://ip-api.com/json/",
                    "https://ipapi.co/json/",
                    "http://worldtimeapi.org/api/ip"
                };
                
                for (String service : locationServices) {
                    try {
                        HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create(service))
                            .timeout(java.time.Duration.ofSeconds(10))
                            .build();
                        
                        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                        
                        if (response.statusCode() == 200) {
                            String responseBody = response.body();
                            LocationInfo locationInfo = parseLocationFromResponse(responseBody, service);
                            
                            if (locationInfo != null) {
                                detectedLocation = locationInfo.country + ", " + locationInfo.city;
                                detectedTimezone = locationInfo.timezone;
                                
                                if (showSyncInfo.get()) {
                                    LodestarSuite.LOG.info("Location detected: {} | Timezone: {}", 
                                        detectedLocation, detectedTimezone);
                                }
                                
                                // Update TimezoneManager if available
                                if (TimezoneManager.get() != null && detectedTimezone != null) {
                                    try {
                                        ZoneId.of(detectedTimezone); // Validate timezone
                                        TimezoneManager.get().timezone.set(detectedTimezone);
                                        LodestarSuite.LOG.info("Auto-updated timezone to: {}", detectedTimezone);
                                    } catch (Exception e) {
                                        LodestarSuite.LOG.warn("Invalid detected timezone: {}", detectedTimezone);
                                    }
                                }
                                
                                return true;
                            }
                        }
                    } catch (Exception e) {
                        if (showSyncInfo.get()) {
                            LodestarSuite.LOG.warn("Failed to detect location from {}: {}", service, e.getMessage());
                        }
                    }
                }
                
                return false;
            } catch (Exception e) {
                LodestarSuite.LOG.error("Location detection failed", e);
                return false;
            }
        });
    }
    
    /**
     * Parse time from API response
     */
    private Instant parseTimeFromResponse(String response, String server) {
        try {
            if (server.contains("worldtimeapi.org")) {
                // Parse worldtimeapi.org response
                if (response.contains("\"datetime\":")) {
                    String datetime = response.split("\"datetime\":\"")[1].split("\"")[0];
                    return Instant.parse(datetime);
                }
            } else if (server.contains("timeapi.io")) {
                // Parse timeapi.io response
                if (response.contains("\"dateTime\":")) {
                    String datetime = response.split("\"dateTime\":\"")[1].split("\"")[0];
                    return Instant.parse(datetime);
                }
            }
        } catch (Exception e) {
            LodestarSuite.LOG.warn("Failed to parse time from response: {}", e.getMessage());
        }
        return null;
    }
    
    /**
     * Parse location from API response
     */
    private LocationInfo parseLocationFromResponse(String response, String service) {
        try {
            if (service.contains("ip-api.com")) {
                // Parse ip-api.com response
                if (response.contains("\"status\":\"success\"")) {
                    String country = extractJsonValue(response, "country");
                    String city = extractJsonValue(response, "city");
                    String timezone = extractJsonValue(response, "timezone");
                    
                    if (country != null && timezone != null) {
                        return new LocationInfo(country, city, timezone);
                    }
                }
            } else if (service.contains("ipapi.co")) {
                // Parse ipapi.co response
                String country = extractJsonValue(response, "country_name");
                String city = extractJsonValue(response, "city");
                String timezone = extractJsonValue(response, "timezone");
                
                if (country != null && timezone != null) {
                    return new LocationInfo(country, city, timezone);
                }
            } else if (service.contains("worldtimeapi.org")) {
                // Parse worldtimeapi.org location response
                String timezone = extractJsonValue(response, "timezone");
                if (timezone != null) {
                    return new LocationInfo("Unknown", "Unknown", timezone);
                }
            }
        } catch (Exception e) {
            LodestarSuite.LOG.warn("Failed to parse location from response: {}", e.getMessage());
        }
        return null;
    }
    
    /**
     * Extract JSON value from response
     */
    private String extractJsonValue(String json, String key) {
        try {
            String pattern = "\"" + key + "\":\"";
            if (json.contains(pattern)) {
                return json.split(pattern)[1].split("\"")[0];
            }
        } catch (Exception e) {
            // Ignore parsing errors
        }
        return null;
    }
    
    /**
     * Get synchronized internet time
     */
    public Instant getInternetTime() {
        return internetTime != null ? internetTime : Instant.now();
    }
    
    /**
     * Get detected location
     */
    public String getDetectedLocation() {
        return detectedLocation;
    }
    
    /**
     * Get detected timezone
     */
    public String getDetectedTimezone() {
        return detectedTimezone;
    }
    
    /**
     * Check if internet time sync is available
     */
    public boolean isInternetTimeAvailable() {
        return internetTime != null;
    }
    
    /**
     * Check if location detection is available
     */
    public boolean isLocationDetected() {
        return detectedLocation != null && detectedTimezone != null;
    }
    
    /**
     * Get time sync status information
     */
    public String getSyncStatus() {
        StringBuilder status = new StringBuilder();
        
        if (isInternetTimeAvailable()) {
            status.append("Internet Time: ✅ Synced | ");
        } else {
            status.append("Internet Time: ❌ Using System Time | ");
        }
        
        if (isLocationDetected()) {
            status.append("Location: ✅ ").append(detectedLocation).append(" | ");
            status.append("Timezone: ✅ ").append(detectedTimezone);
        } else {
            status.append("Location: ❌ Not Detected | ");
            status.append("Timezone: ❌ Using System Default");
        }
        
        return status.toString();
    }
    
    /**
     * Location information container
     */
    private static class LocationInfo {
        final String country;
        final String city;
        final String timezone;
        
        LocationInfo(String country, String city, String timezone) {
            this.country = country;
            this.city = city;
            this.timezone = timezone;
        }
    }
}
