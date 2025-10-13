/*
 * This file is part of the Lodestar Suite distribution (https://github.com/waythread/lodestar-suite).
 * Copyright (c) waythread.
 */

package meteordevelopment.meteorclient.systems.hud.elements;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WVerticalList;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.TickRate;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtFloat;
import net.minecraft.nbt.NbtList;

import java.util.ArrayList;
import java.util.List;

public class TPSGraphHud extends HudElement {
    public static final HudElementInfo<TPSGraphHud> INFO = new HudElementInfo<>(Hud.GROUP, "tps-graph", "Displays a graph of server TPS over time.", TPSGraphHud::new);

    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgGraph = settings.createGroup("Graph");
    private final SettingGroup sgUpdate = settings.createGroup("Update Rate");
    private final SettingGroup sgColors = settings.createGroup("Colors");

    // General
    private final Setting<Boolean> showCurrentTPS = sgGeneral.add(new BoolSetting.Builder()
        .name("show-current-tps")
        .description("Show current TPS value above the graph.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> showAverageTPS = sgGeneral.add(new BoolSetting.Builder()
        .name("show-average-tps")
        .description("Show average TPS value above the graph.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> showMinMaxTPS = sgGeneral.add(new BoolSetting.Builder()
        .name("show-min-max-tps")
        .description("Show minimum and maximum TPS values.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> showMSPT = sgGeneral.add(new BoolSetting.Builder()
        .name("show-mspt")
        .description("Show estimated MSPT (Milliseconds Per Tick) information.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> showMSPTGraph = sgGeneral.add(new BoolSetting.Builder()
        .name("show-mspt-graph")
        .description("Show MSPT graph alongside TPS graph.")
        .defaultValue(false)
        .build()
    );

    // Graph
    private final Setting<Integer> graphWidth = sgGraph.add(new IntSetting.Builder()
        .name("graph-width")
        .description("Width of the TPS graph.")
        .defaultValue(300)
        .min(50)
        .sliderRange(50, 500)
        .build()
    );

    private final Setting<Integer> graphHeight = sgGraph.add(new IntSetting.Builder()
        .name("graph-height")
        .description("Height of the TPS graph.")
        .defaultValue(60)
        .min(20)
        .sliderRange(20, 200)
        .build()
    );

    private final Setting<Integer> maxDataPoints = sgGraph.add(new IntSetting.Builder()
        .name("max-data-points")
        .description("Maximum number of TPS data points to store.")
        .defaultValue(100)
        .min(20)
        .sliderRange(20, 500)
        .build()
    );


    private final Setting<Boolean> showGrid = sgGraph.add(new BoolSetting.Builder()
        .name("show-grid")
        .description("Show grid lines on the graph.")
        .defaultValue(true)
        .build()
    );

    private final Setting<SettingColor> gridColor = sgGraph.add(new ColorSetting.Builder()
        .name("grid-color")
        .description("Color of the grid lines.")
        .defaultValue(new SettingColor(100, 100, 100, 100))
        .visible(showGrid::get)
        .build()
    );

    // Update Rate Settings
    private final Setting<Double> updateRate = sgUpdate.add(new DoubleSetting.Builder()
        .name("update-rate")
        .description("Update rate for TPS data collection in seconds.")
        .defaultValue(1.0)
        .min(0.1)
        .max(10.0)
        .sliderRange(0.1, 10.0)
        .build()
    );

    private final Setting<Boolean> adaptiveUpdateRate = sgUpdate.add(new BoolSetting.Builder()
        .name("adaptive-update-rate")
        .description("Automatically adjust update rate based on TPS stability.")
        .defaultValue(false)
        .build()
    );

    // Color Settings
    private final Setting<SettingColor> excellentTPSColor = sgColors.add(new ColorSetting.Builder()
        .name("excellent-tps-color")
        .description("Color for excellent TPS (20.0+).")
        .defaultValue(new SettingColor(0, 255, 0, 255))
        .build()
    );

    private final Setting<SettingColor> goodTPSColor = sgColors.add(new ColorSetting.Builder()
        .name("good-tps-color")
        .description("Color for good TPS (16.0-19.9).")
        .defaultValue(new SettingColor(100, 255, 100, 255))
        .build()
    );

    private final Setting<SettingColor> moderateTPSColor = sgColors.add(new ColorSetting.Builder()
        .name("moderate-tps-color")
        .description("Color for moderate TPS (10.0-15.9).")
        .defaultValue(new SettingColor(255, 255, 0, 255))
        .build()
    );

    private final Setting<SettingColor> poorTPSColor = sgColors.add(new ColorSetting.Builder()
        .name("poor-tps-color")
        .description("Color for poor TPS (5.0-9.9).")
        .defaultValue(new SettingColor(255, 165, 0, 255))
        .build()
    );

    private final Setting<SettingColor> criticalTPSColor = sgColors.add(new ColorSetting.Builder()
        .name("critical-tps-color")
        .description("Color for critical TPS (0.0-4.9).")
        .defaultValue(new SettingColor(255, 0, 0, 255))
        .build()
    );

    private final Setting<Boolean> smoothColorTransitions = sgColors.add(new BoolSetting.Builder()
        .name("smooth-color-transitions")
        .description("Enable smooth color transitions between TPS ranges.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> showTPSRangeLabels = sgColors.add(new BoolSetting.Builder()
        .name("show-tps-range-labels")
        .description("Show TPS range labels on the graph (Excellent, Good, Moderate, Poor, Critical).")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> colorCodeByModule = sgColors.add(new BoolSetting.Builder()
        .name("color-code-by-module")
        .description("Use different color schemes for different TPS ranges (module-based coloring).")
        .defaultValue(true)
        .build()
    );


    // TPS data storage
    private final List<Float> tpsHistory = new ArrayList<>();
    private final List<Float> msptHistory = new ArrayList<>();
    private final List<Long> timeHistory = new ArrayList<>();
    private long lastUpdate = 0;
    private double currentUpdateInterval = 1000.0; // Current update interval in milliseconds
    
    // Statistics
    private float minTPS = 20.0f;
    private float maxTPS = 0.0f;
    private float totalTPS = 0.0f;
    private int dataPointCount = 0;
    
    // MSPT statistics
    private float minMSPT = 50.0f; // 20 TPS = 50ms per tick
    private float maxMSPT = 0.0f;
    private float totalMSPT = 0.0f;

    public TPSGraphHud() {
        super(INFO);
    }

    @Override
    public void tick(HudRenderer renderer) {
        long currentTime = System.currentTimeMillis();
        
        // Calculate current update interval
        updateCurrentInterval();
        
        // Update TPS data based on configured rate
        if (currentTime - lastUpdate >= currentUpdateInterval) {
            float currentTPS = TickRate.INSTANCE.getTickRate();
            float currentMSPT = calculateMSPT(currentTPS);
            
            // Add new data point (ensure all lists stay synchronized)
            tpsHistory.add(currentTPS);
            msptHistory.add(currentMSPT);
            timeHistory.add(currentTime);
            
            // Safety check: ensure all lists have the same size
            if (tpsHistory.size() != msptHistory.size() || tpsHistory.size() != timeHistory.size()) {
                MeteorClient.LOG.warn("TPS Graph HUD: List synchronization issue detected, resetting data");
                tpsHistory.clear();
                msptHistory.clear();
                timeHistory.clear();
                resetStatistics();
                return; // Skip this update to prevent further issues
            }
            
            // Update statistics
            updateStatistics(currentTPS, currentMSPT);
            
            // Remove old data points if we exceed max
            while (tpsHistory.size() > maxDataPoints.get()) {
                // Ensure all lists have data before removing
                if (tpsHistory.isEmpty() || msptHistory.isEmpty() || timeHistory.isEmpty()) {
                    break; // Safety check to prevent IndexOutOfBoundsException
                }
                
                float removedTPS = tpsHistory.remove(0);
                float removedMSPT = msptHistory.remove(0);
                timeHistory.remove(0);
                
                // Update statistics for removed point
                totalTPS -= removedTPS;
                totalMSPT -= removedMSPT;
                dataPointCount--;
                
                // Recalculate min/max if needed
                if (removedTPS == minTPS || removedTPS == maxTPS) {
                    recalculateMinMax();
                }
                if (removedMSPT == minMSPT || removedMSPT == maxMSPT) {
                    recalculateMSPTMinMax();
                }
            }
            
            lastUpdate = currentTime;
        }
    }
    
    private void updateStatistics(float tps, float mspt) {
        totalTPS += tps;
        totalMSPT += mspt;
        dataPointCount++;
        
        if (tps < minTPS) minTPS = tps;
        if (tps > maxTPS) maxTPS = tps;
        
        if (mspt < minMSPT) minMSPT = mspt;
        if (mspt > maxMSPT) maxMSPT = mspt;
    }
    
    /**
     * Calculate MSPT (Milliseconds Per Tick) from TPS
     */
    private float calculateMSPT(float tps) {
        if (tps <= 0) return 1000.0f; // Default to 1000ms if TPS is 0 or negative
        return 1000.0f / tps; // MSPT = 1000ms / TPS
    }
    
    private void recalculateMinMax() {
        if (tpsHistory.isEmpty()) {
            minTPS = 20.0f;
            maxTPS = 0.0f;
            return;
        }
        
        minTPS = Float.MAX_VALUE;
        maxTPS = Float.MIN_VALUE;
        
        for (float tps : tpsHistory) {
            if (tps < minTPS) minTPS = tps;
            if (tps > maxTPS) maxTPS = tps;
        }
    }
    
    private void recalculateMSPTMinMax() {
        if (msptHistory.isEmpty()) {
            minMSPT = 50.0f;
            maxMSPT = 0.0f;
            return;
        }
        
        minMSPT = Float.MAX_VALUE;
        maxMSPT = Float.MIN_VALUE;
        
        for (float mspt : msptHistory) {
            if (mspt < minMSPT) minMSPT = mspt;
            if (mspt > maxMSPT) maxMSPT = mspt;
        }
    }

    /**
     * Calculate current update interval based on settings
     */
    private void updateCurrentInterval() {
        if (adaptiveUpdateRate.get()) {
            // Adaptive update rate based on TPS stability
            if (tpsHistory.size() >= 5) {
                float recentTPS = tpsHistory.get(tpsHistory.size() - 1);
                float avgTPS = totalTPS / dataPointCount;
                float variance = Math.abs(recentTPS - avgTPS);
                
                // More frequent updates when TPS is unstable
                if (variance > 2.0f) {
                    currentUpdateInterval = updateRate.get() * 500.0; // 0.5x rate for unstable TPS
                } else if (variance > 1.0f) {
                    currentUpdateInterval = updateRate.get() * 750.0; // 0.75x rate for slightly unstable TPS
                } else {
                    currentUpdateInterval = updateRate.get() * 1000.0; // Normal rate for stable TPS
                }
            } else {
                currentUpdateInterval = updateRate.get() * 1000.0;
            }
        } else {
            currentUpdateInterval = updateRate.get() * 1000.0;
        }
    }

    @Override
    public void render(HudRenderer renderer) {
        // Calculate graph dimensions
        int graphW = graphWidth.get();
        int graphH = graphHeight.get();
        
        // Calculate text height based on what's actually being displayed
        int textHeight = 0;
        int textLines = 0;
        
        if (showCurrentTPS.get() || showAverageTPS.get() || showMinMaxTPS.get()) {
            textLines++; // TPS line
        }
        if (showMSPT.get()) {
            textLines++; // MSPT line
        }
        
        textHeight = textLines * 14 + (textLines > 0 ? 20 : 0); // 14px per line + 20px padding (8px top + 12px bottom for better spacing)
        
        // Set element size to include both graph and text
        setSize(graphW, graphH + textHeight);
        
        // Draw background for the entire element (graph + text area)
        renderer.quad(x, y, graphW, graphH + textHeight, new Color(0, 0, 0, 128));
        
        // Draw grid if enabled
        if (showGrid.get()) {
            drawGrid(renderer, graphW, graphH);
        }

        // Draw TPS range labels if enabled
        if (showTPSRangeLabels.get()) {
            drawTPSRangeLabels(renderer, graphW, graphH);
        }
        
        // Draw TPS graph
        if (!tpsHistory.isEmpty()) {
            if (showMSPTGraph.get()) {
                // Draw both TPS and MSPT graphs
                drawTPSGraph(renderer, graphW, graphH / 2);
                drawMSPTGraph(renderer, graphW, graphH / 2, graphH / 2);
            } else {
                // Draw only TPS graph
                drawTPSGraph(renderer, graphW, graphH);
            }
        }
        
        // Draw separator line between graph and text (if text is shown)
        if (textHeight > 0) {
            renderer.quad(x, y + graphH, graphW, 1, new Color(100, 100, 100, 100));
        }
        
        // Draw TPS text
        if (showCurrentTPS.get() || showAverageTPS.get() || showMinMaxTPS.get()) {
            drawTPSText(renderer, graphW);
        }
        
        // Draw MSPT text
        if (showMSPT.get()) {
            drawMSPTText(renderer, graphW);
        }
        
        // Show placeholder in editor when no data
        if (tpsHistory.isEmpty() && isInEditor()) {
            renderer.text("TPS Graph - No data yet", x + graphW / 2, y + graphH / 2, Color.GRAY, false, 1.0);
        }
    }

    private void drawGrid(HudRenderer renderer, int width, int height) {
        Color grid = gridColor.get();
        
        // Horizontal lines (TPS levels: 0, 5, 10, 15, 20)
        for (int i = 0; i <= 4; i++) {
            int y = this.y + (height * i / 4);
            renderer.line(this.x, y, this.x + width, y, grid);
        }
        
        // Vertical lines (time divisions)
        for (int i = 0; i <= 10; i++) {
            int x = this.x + (width * i / 10);
            renderer.line(x, this.y, x, this.y + height, grid);
        }
    }

    private void drawTPSRangeLabels(HudRenderer renderer, int width, int height) {
        // Draw TPS range labels on the right side of the graph
        int labelX = x + width + 8; // 8px spacing from graph
        
        // Excellent (20+ TPS) - Top
        int excellentY = y + (int)(height * 0.1);
        renderer.text("Excellent (20+)", labelX, excellentY, excellentTPSColor.get(), false, 0.8);
        
        // Good (16-19.9 TPS)
        int goodY = y + (int)(height * 0.3);
        renderer.text("Good (16-19.9)", labelX, goodY, goodTPSColor.get(), false, 0.8);
        
        // Moderate (10-15.9 TPS)
        int moderateY = y + (int)(height * 0.5);
        renderer.text("Moderate (10-15.9)", labelX, moderateY, moderateTPSColor.get(), false, 0.8);
        
        // Poor (5-9.9 TPS)
        int poorY = y + (int)(height * 0.7);
        renderer.text("Poor (5-9.9)", labelX, poorY, poorTPSColor.get(), false, 0.8);
        
        // Critical (0-4.9 TPS) - Bottom
        int criticalY = y + (int)(height * 0.9);
        renderer.text("Critical (0-4.9)", labelX, criticalY, criticalTPSColor.get(), false, 0.8);
    }

    private void drawTPSGraph(HudRenderer renderer, int width, int height) {
        if (tpsHistory.size() < 2) return;
        
        // Draw TPS line segments
        for (int i = 1; i < tpsHistory.size(); i++) {
            float tps1 = tpsHistory.get(i - 1);
            float tps2 = tpsHistory.get(i);
            
            // Calculate positions
            double x1 = this.x + (width * (i - 1.0) / (tpsHistory.size() - 1));
            double x2 = this.x + (width * i / (tpsHistory.size() - 1));
            
            // Normalize TPS to graph height (0-20 TPS maps to 0-height)
            double y1 = this.y + height - ((tps1 / 20.0) * height);
            double y2 = this.y + height - ((tps2 / 20.0) * height);
            
            // Get color based on TPS value (use average for smooth transitions)
            Color lineColor = getTPSColor((tps1 + tps2) / 2.0f);
            
            // Draw line using HudRenderer's line method
            renderer.line(x1, y1, x2, y2, lineColor);
            
            // Draw module-based coloring if enabled
            if (colorCodeByModule.get()) {
                drawModuleColorIndicator(renderer, x1, y1, x2, y2, (tps1 + tps2) / 2.0f);
            }
        }
    }

    private Color getTPSColor(float tps) {
        if (smoothColorTransitions.get()) {
            return getSmoothTPSColor(tps);
        } else {
            return getDiscreteTPSColor(tps);
        }
    }

    private Color getDiscreteTPSColor(float tps) {
        if (tps >= 20.0f) return excellentTPSColor.get();
        else if (tps >= 16.0f) return goodTPSColor.get();
        else if (tps >= 10.0f) return moderateTPSColor.get();
        else if (tps >= 5.0f) return poorTPSColor.get();
        else return criticalTPSColor.get();
    }

    private Color getSmoothTPSColor(float tps) {
        Color startColor, endColor;
        float progress;
        
        if (tps >= 20.0f) {
            return excellentTPSColor.get();
        } else if (tps >= 16.0f) {
            startColor = goodTPSColor.get();
            endColor = excellentTPSColor.get();
            progress = (tps - 16.0f) / 4.0f;
        } else if (tps >= 10.0f) {
            startColor = moderateTPSColor.get();
            endColor = goodTPSColor.get();
            progress = (tps - 10.0f) / 6.0f;
        } else if (tps >= 5.0f) {
            startColor = poorTPSColor.get();
            endColor = moderateTPSColor.get();
            progress = (tps - 5.0f) / 5.0f;
        } else {
            startColor = criticalTPSColor.get();
            endColor = poorTPSColor.get();
            progress = tps / 5.0f;
        }
        
        // Interpolate between colors
        return new Color(
            (int) (startColor.r + (endColor.r - startColor.r) * progress),
            (int) (startColor.g + (endColor.g - startColor.g) * progress),
            (int) (startColor.b + (endColor.b - startColor.b) * progress),
            (int) (startColor.a + (endColor.a - startColor.a) * progress)
        );
    }

    private void drawModuleColorIndicator(HudRenderer renderer, double x1, double y1, double x2, double y2, float tps) {
        // Draw a small colored dot at each data point to indicate TPS range
        Color indicatorColor = getTPSColor(tps);
        
        // Draw small circle at the end point
        double centerX = x2;
        double centerY = y2;
        double radius = 2.0;
        
        // Draw filled circle using multiple quads
        for (int i = 0; i < 8; i++) {
            double angle1 = (i * Math.PI * 2) / 8;
            double angle2 = ((i + 1) * Math.PI * 2) / 8;
            
            double x1_circle = centerX + Math.cos(angle1) * radius;
            double y1_circle = centerY + Math.sin(angle1) * radius;
            double x2_circle = centerX + Math.cos(angle2) * radius;
            double y2_circle = centerY + Math.sin(angle2) * radius;
            
            // Draw triangle from center to two points on circle
            renderer.triangle(centerX, centerY, x1_circle, y1_circle, x2_circle, y2_circle, indicatorColor);
        }
    }

    private void drawTPSText(HudRenderer renderer, int width) {
        if (!showCurrentTPS.get() && !showAverageTPS.get() && !showMinMaxTPS.get()) return;
        
        int textY = y + graphHeight.get() + 10; // 8px separator + 2px padding
        int currentX = x + 4; // 4px left padding
        
        // Build TPS text line with proper spacing
        StringBuilder tpsLine = new StringBuilder();
        boolean hasContent = false;
        
        if (showCurrentTPS.get() && !tpsHistory.isEmpty()) {
            float currentTPS = tpsHistory.get(tpsHistory.size() - 1);
            tpsLine.append(String.format("TPS: %.1f", currentTPS));
            hasContent = true;
        }
        
        if (showAverageTPS.get() && dataPointCount > 0) {
            if (hasContent) tpsLine.append(" | ");
            float avgTPS = totalTPS / dataPointCount;
            tpsLine.append(String.format("Avg: %.1f", avgTPS));
            hasContent = true;
        }
        
        if (showMinMaxTPS.get() && dataPointCount > 0) {
            if (hasContent) tpsLine.append(" | ");
            tpsLine.append(String.format("Min: %.1f Max: %.1f", minTPS, maxTPS));
            hasContent = true;
        }
        
        if (hasContent) {
            // Draw TPS line with appropriate color (use current TPS color if available)
            Color tpsColor = Color.WHITE;
            if (showCurrentTPS.get() && !tpsHistory.isEmpty()) {
                tpsColor = getTPSColor(tpsHistory.get(tpsHistory.size() - 1));
            }
            renderer.text(tpsLine.toString(), currentX, textY, tpsColor, false, 1.0);
        }
    }
    
    private void drawMSPTText(HudRenderer renderer, int width) {
        if (!showMSPT.get() || tpsHistory.isEmpty() || msptHistory.isEmpty()) return;
        
        float currentTPS = tpsHistory.get(tpsHistory.size() - 1);
        float currentMSPT = calculateMSPT(currentTPS);
        float averageMSPT = dataPointCount > 0 ? totalMSPT / dataPointCount : 0.0f;
        
        // Position MSPT text on a separate line below TPS text
        int textY = y + graphHeight.get() + 28; // 8px separator + 2px padding + 18px line height (more spacing)
        int currentX = x + 4; // 4px left padding
        
        // Build MSPT text line with proper spacing
        StringBuilder msptLine = new StringBuilder();
        boolean hasContent = false;
        
        // Current MSPT
        msptLine.append(String.format("MSPT: %.1fms", currentMSPT));
        hasContent = true;
        
        // Average MSPT
        if (showAverageTPS.get()) {
            if (hasContent) msptLine.append(" | ");
            msptLine.append(String.format("Avg: %.1fms", averageMSPT));
            hasContent = true;
        }
        
        // Min/Max MSPT
        if (showMinMaxTPS.get()) {
            if (hasContent) msptLine.append(" | ");
            msptLine.append(String.format("Min: %.1fms Max: %.1fms", minMSPT, maxMSPT));
            hasContent = true;
        }
        
        if (hasContent) {
            // Draw MSPT line with appropriate color
            Color msptColor = getMSPTColor(currentMSPT);
            renderer.text(msptLine.toString(), currentX, textY, msptColor, false, 1.0);
        }
    }
    
    /**
     * Get color for MSPT value (green = good, yellow = okay, red = bad)
     */
    private Color getMSPTColor(float mspt) {
        if (mspt <= 50.0f) return Color.GREEN;      // 20+ TPS (good)
        if (mspt <= 100.0f) return Color.YELLOW;    // 10-20 TPS (okay)
        return Color.RED;                           // <10 TPS (bad)
    }
    
    /**
     * Draw MSPT graph
     */
    private void drawMSPTGraph(HudRenderer renderer, int width, int height, int offsetY) {
        if (msptHistory.isEmpty() || tpsHistory.isEmpty()) return;
        
        // Calculate MSPT range for scaling
        float minMSPT = Float.MAX_VALUE;
        float maxMSPT = Float.MIN_VALUE;
        
        for (float mspt : msptHistory) {
            if (mspt < minMSPT) minMSPT = mspt;
            if (mspt > maxMSPT) maxMSPT = mspt;
        }
        
        // Ensure minimum range for visibility
        if (maxMSPT - minMSPT < 10.0f) {
            float center = (minMSPT + maxMSPT) / 2.0f;
            minMSPT = center - 5.0f;
            maxMSPT = center + 5.0f;
        }
        
        float msptRange = maxMSPT - minMSPT;
        
        // Draw MSPT line graph
        for (int i = 1; i < msptHistory.size(); i++) {
            float mspt1 = msptHistory.get(i - 1);
            float mspt2 = msptHistory.get(i);
            
            // Calculate positions
            int x1 = x + (i - 1) * width / Math.max(1, msptHistory.size() - 1);
            int x2 = x + i * width / Math.max(1, msptHistory.size() - 1);
            
            float normalizedMSPT1 = (mspt1 - minMSPT) / msptRange;
            float normalizedMSPT2 = (mspt2 - minMSPT) / msptRange;
            
            int y1 = y + offsetY + height - (int)(normalizedMSPT1 * height);
            int y2 = y + offsetY + height - (int)(normalizedMSPT2 * height);
            
            // Get color based on MSPT value
            Color lineColor = getMSPTColor(mspt2);
            
            // Draw line
            renderer.line(x1, y1, x2, y2, lineColor);
        }
        
        // Draw MSPT labels
        renderer.text("MSPT", x, y + offsetY, Color.WHITE, false, 0.8);
        renderer.text(String.format("%.0fms", minMSPT), x, y + offsetY + height - 8, Color.GRAY, false, 0.7);
        renderer.text(String.format("%.0fms", maxMSPT), x, y + offsetY + 8, Color.GRAY, false, 0.7);
    }

    @Override
    public WWidget getWidget(GuiTheme theme) {
        WVerticalList list = theme.verticalList();
        
        WButton clearData = list.add(theme.button("Clear Graph Data")).expandX().widget();
        clearData.action = () -> {
            tpsHistory.clear();
            msptHistory.clear();
            timeHistory.clear();
            resetStatistics();
        };
        
        WButton resetStats = list.add(theme.button("Reset Statistics")).expandX().widget();
        resetStats.action = () -> {
            resetStatistics();
        };
        
        return list;
    }
    
    private void resetStatistics() {
        minTPS = 20.0f;
        maxTPS = 0.0f;
        totalTPS = 0.0f;
        dataPointCount = 0;
        
        // Reset MSPT statistics
        minMSPT = 50.0f;
        maxMSPT = 0.0f;
        totalMSPT = 0.0f;
    }
    
    // Data persistence
    @Override
    public NbtCompound toTag() {
        NbtCompound tag = super.toTag();
        
        // Save TPS history
        NbtList tpsList = new NbtList();
        for (float tps : tpsHistory) {
            tpsList.add(NbtFloat.of(tps));
        }
        tag.put("tpsHistory", tpsList);
        
        // Save MSPT history
        NbtList msptList = new NbtList();
        for (float mspt : msptHistory) {
            msptList.add(NbtFloat.of(mspt));
        }
        tag.put("msptHistory", msptList);
        
        // Save statistics
        tag.putFloat("minTPS", minTPS);
        tag.putFloat("maxTPS", maxTPS);
        tag.putFloat("totalTPS", totalTPS);
        tag.putInt("dataPointCount", dataPointCount);
        
        // Save MSPT statistics
        tag.putFloat("minMSPT", minMSPT);
        tag.putFloat("maxMSPT", maxMSPT);
        tag.putFloat("totalMSPT", totalMSPT);
        
        return tag;
    }
    
    @Override
    public HudElement fromTag(NbtCompound tag) {
        super.fromTag(tag);
        
        // Load TPS history
        tpsHistory.clear();
        NbtList tpsList = tag.getListOrEmpty("tpsHistory");
        for (NbtElement element : tpsList) {
            if (element instanceof NbtFloat nbtFloat) {
                tpsHistory.add(nbtFloat.floatValue());
            }
        }
        
        // Load MSPT history
        msptHistory.clear();
        NbtList msptList = tag.getListOrEmpty("msptHistory");
        for (NbtElement element : msptList) {
            if (element instanceof NbtFloat nbtFloat) {
                msptHistory.add(nbtFloat.floatValue());
            }
        }
        
        // Load statistics
        if (tag.contains("minTPS")) minTPS = tag.getFloat("minTPS").orElse(20.0f);
        if (tag.contains("maxTPS")) maxTPS = tag.getFloat("maxTPS").orElse(0.0f);
        if (tag.contains("totalTPS")) totalTPS = tag.getFloat("totalTPS").orElse(0.0f);
        if (tag.contains("dataPointCount")) dataPointCount = tag.getInt("dataPointCount").orElse(0);
        
        // Load MSPT statistics
        if (tag.contains("minMSPT")) minMSPT = tag.getFloat("minMSPT").orElse(50.0f);
        if (tag.contains("maxMSPT")) maxMSPT = tag.getFloat("maxMSPT").orElse(0.0f);
        if (tag.contains("totalMSPT")) totalMSPT = tag.getFloat("totalMSPT").orElse(0.0f);
        
        return this;
    }
}
