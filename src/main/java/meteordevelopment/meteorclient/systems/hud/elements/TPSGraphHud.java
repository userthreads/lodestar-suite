/*
 * This file is part of the Lodestar Suite distribution (https://github.com/waythread/lodestar-suite).
 * Copyright (c) waythread.
 */

package meteordevelopment.meteorclient.systems.hud.elements;

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

    // Graph
    private final Setting<Integer> graphWidth = sgGraph.add(new IntSetting.Builder()
        .name("graph-width")
        .description("Width of the TPS graph.")
        .defaultValue(200)
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


    // TPS data storage
    private final List<Float> tpsHistory = new ArrayList<>();
    private final List<Long> timeHistory = new ArrayList<>();
    private long lastUpdate = 0;
    private static final long UPDATE_INTERVAL = 1000; // Update every second
    
    // Statistics
    private float minTPS = 20.0f;
    private float maxTPS = 0.0f;
    private float totalTPS = 0.0f;
    private int dataPointCount = 0;

    public TPSGraphHud() {
        super(INFO);
    }

    @Override
    public void tick(HudRenderer renderer) {
        long currentTime = System.currentTimeMillis();
        
        // Update TPS data every second
        if (currentTime - lastUpdate >= UPDATE_INTERVAL) {
            float currentTPS = TickRate.INSTANCE.getTickRate();
            
            // Add new data point
            tpsHistory.add(currentTPS);
            timeHistory.add(currentTime);
            
            // Update statistics
            updateStatistics(currentTPS);
            
            // Remove old data points if we exceed max
            while (tpsHistory.size() > maxDataPoints.get()) {
                float removedTPS = tpsHistory.remove(0);
                timeHistory.remove(0);
                
                // Update statistics for removed point
                totalTPS -= removedTPS;
                dataPointCount--;
                
                // Recalculate min/max if needed
                if (removedTPS == minTPS || removedTPS == maxTPS) {
                    recalculateMinMax();
                }
            }
            
            lastUpdate = currentTime;
        }
    }
    
    private void updateStatistics(float tps) {
        totalTPS += tps;
        dataPointCount++;
        
        if (tps < minTPS) minTPS = tps;
        if (tps > maxTPS) maxTPS = tps;
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

    @Override
    public void render(HudRenderer renderer) {
        // Calculate graph dimensions
        int graphW = graphWidth.get();
        int graphH = graphHeight.get();
        
        // Calculate text height
        int textHeight = 0;
        if (showCurrentTPS.get() || showAverageTPS.get() || showMinMaxTPS.get()) {
            textHeight = 20;
        }
        
        // Set element size
        setSize(graphW, graphH + textHeight);
        
        // Draw background
        renderer.quad(x, y, graphW, graphH, new Color(0, 0, 0, 128));
        
        // Draw grid if enabled
        if (showGrid.get()) {
            drawGrid(renderer, graphW, graphH);
        }
        
        // Draw TPS graph
        if (!tpsHistory.isEmpty()) {
            drawTPSGraph(renderer, graphW, graphH);
        }
        
        // Draw TPS text
        if (showCurrentTPS.get() || showAverageTPS.get() || showMinMaxTPS.get()) {
            drawTPSText(renderer, graphW);
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
        }
    }

    private Color getTPSColor(float tps) {
        if (tps >= 20.0f) return new Color(0, 255, 0);      // Green
        else if (tps >= 16.0f) return new Color(255, 165, 0); // Orange
        else if (tps >= 10.0f) return new Color(255, 191, 0); // Amber
        else if (tps >= 1.0f) return new Color(255, 0, 0);    // Red
        else return new Color(0, 0, 0);                       // Black
    }


    private void drawTPSText(HudRenderer renderer, int width) {
        int textY = y + graphHeight.get() + 5;
        int currentX = x;
        
        if (showCurrentTPS.get() && !tpsHistory.isEmpty()) {
            float currentTPS = tpsHistory.get(tpsHistory.size() - 1);
            Color tpsColor = getTPSColor(currentTPS);
            String tpsText = String.format("TPS: %.1f", currentTPS);
            
            renderer.text(tpsText, currentX, textY, tpsColor, false, 1.0);
            currentX += renderer.textWidth(tpsText, false, 1.0) + 10;
        }
        
        if (showAverageTPS.get() && dataPointCount > 0) {
            float avgTPS = totalTPS / dataPointCount;
            Color avgColor = getTPSColor(avgTPS);
            String avgText = String.format("Avg: %.1f", avgTPS);
            
            renderer.text(avgText, currentX, textY, avgColor, false, 1.0);
            currentX += renderer.textWidth(avgText, false, 1.0) + 10;
        }
        
        if (showMinMaxTPS.get() && dataPointCount > 0) {
            String minMaxText = String.format("Min: %.1f Max: %.1f", minTPS, maxTPS);
            Color minMaxColor = getTPSColor((minTPS + maxTPS) / 2.0f);
            
            double textWidth = renderer.textWidth(minMaxText, false, 1.0);
            renderer.text(minMaxText, x + width - textWidth, textY, minMaxColor, false, 1.0);
        }
    }

    @Override
    public WWidget getWidget(GuiTheme theme) {
        WVerticalList list = theme.verticalList();
        
        WButton clearData = list.add(theme.button("Clear Graph Data")).expandX().widget();
        clearData.action = () -> {
            tpsHistory.clear();
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
        
        // Save statistics
        tag.putFloat("minTPS", minTPS);
        tag.putFloat("maxTPS", maxTPS);
        tag.putFloat("totalTPS", totalTPS);
        tag.putInt("dataPointCount", dataPointCount);
        
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
        
        // Load statistics
        if (tag.contains("minTPS")) minTPS = tag.getFloat("minTPS").orElse(20.0f);
        if (tag.contains("maxTPS")) maxTPS = tag.getFloat("maxTPS").orElse(0.0f);
        if (tag.contains("totalTPS")) totalTPS = tag.getFloat("totalTPS").orElse(0.0f);
        if (tag.contains("dataPointCount")) dataPointCount = tag.getInt("dataPointCount").orElse(0);
        
        return this;
    }
}
