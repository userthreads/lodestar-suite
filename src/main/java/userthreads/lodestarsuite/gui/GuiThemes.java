/*
 * This file is part of the Lodestar Suite distribution (https://github.com/userthreads/lodestar-suite).
 * Copyright (c) userthreads.
 */

package userthreads.lodestarsuite.gui;

import userthreads.lodestarsuite.LodestarSuite;
import userthreads.lodestarsuite.events.game.GameLeftEvent;
import userthreads.lodestarsuite.gui.themes.meteor.ChristmasGuiTheme;
import userthreads.lodestarsuite.gui.themes.meteor.HalloweenGuiTheme;
import userthreads.lodestarsuite.gui.themes.meteor.LodestarGuiTheme;
import userthreads.lodestarsuite.systems.christmas.ChristmasMode;
import userthreads.lodestarsuite.systems.halloween.HalloweenMode;
import userthreads.lodestarsuite.utils.PostInit;
import userthreads.lodestarsuite.utils.PreInit;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GuiThemes {
    private static final File FOLDER = new File(LodestarSuite.FOLDER, "gui");
    private static final File THEMES_FOLDER = new File(FOLDER, "themes");
    private static final File FILE = new File(FOLDER, "gui.nbt");

    private static final List<GuiTheme> themes = new ArrayList<>();
    private static GuiTheme theme;

    private GuiThemes() {
    }

    @PreInit
    public static void init() {
        add(new LodestarGuiTheme());
        add(new HalloweenGuiTheme());
        add(new ChristmasGuiTheme());
    }

    @PostInit
    public static void postInit() {
        if (FILE.exists()) {
            try {
                NbtCompound tag = NbtIo.read(FILE.toPath());

                if (tag != null) select(tag.getString("currentTheme", ""));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (theme == null) select("Lodestar");
        
        // Check for seasonal themes and auto-switch if appropriate
        checkSeasonalThemes();
        
        // Subscribe to game events for theme saving
        LodestarSuite.EVENT_BUS.subscribe(GuiThemes.class);
    }
    
    public static void checkSeasonalThemes() {
        // Auto-switch to seasonal themes based on date
        if (HalloweenMode.get().isActive() && !(get() instanceof HalloweenGuiTheme)) {
            select("Halloween");
            LodestarSuite.LOG.info("Auto-switched to Halloween theme for Halloween season (October 1 - November 1)!");
        } else if (ChristmasMode.get().isActive() && !(get() instanceof ChristmasGuiTheme)) {
            select("Christmas");
            LodestarSuite.LOG.info("Auto-switched to Christmas theme for Christmas season (December 1 - December 30)!");
        } else if (!HalloweenMode.get().isActive() && !ChristmasMode.get().isActive() && 
                   (get() instanceof HalloweenGuiTheme || get() instanceof ChristmasGuiTheme)) {
            // Switch back to default theme when not in seasonal periods
            select("Lodestar");
            LodestarSuite.LOG.info("Auto-switched back to default theme - seasonal period ended");
        }
    }

    public static void add(GuiTheme theme) {
        for (Iterator<GuiTheme> it = themes.iterator(); it.hasNext();) {
            if (it.next().name.equals(theme.name)) {
                it.remove();

                LodestarSuite.LOG.error("Theme with the name '{}' has already been added.", theme.name);
                break;
            }
        }

        themes.add(theme);
    }

    public static void select(String name) {
        // Find theme with the provided name
        GuiTheme theme = null;

        for (GuiTheme t : themes) {
            if (t.name.equals(name)) {
                theme = t;
                break;
            }
        }

        if (theme != null) {
            // Save current theme
            saveTheme();

            // Select new theme
            GuiThemes.theme = theme;
            
            // Log theme switch for debugging
            LodestarSuite.LOG.info("Switched to theme: {}", theme.name);

            // Load new theme
            try {
                File file = new File(THEMES_FOLDER, get().name + ".nbt");

                if (file.exists()) {
                    NbtCompound tag = NbtIo.read(file.toPath());
                    if (tag != null) get().fromTag(tag);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Save global gui settings with the new theme
            saveGlobal();
        } else {
            LodestarSuite.LOG.error("Theme '{}' not found!", name);
        }
    }

    public static GuiTheme get() {
        return theme;
    }

    public static String[] getNames() {
        String[] names = new String[themes.size()];

        for (int i = 0; i < themes.size(); i++) {
            names[i] = themes.get(i).name;
        }

        return names;
    }

    // Saving

    private static void saveTheme() {
        if (get() != null) {
            try {
                NbtCompound tag = get().toTag();

                THEMES_FOLDER.mkdirs();
                NbtIo.write(tag, new File(THEMES_FOLDER, get().name + ".nbt").toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void saveGlobal() {
        try {
            NbtCompound tag = new NbtCompound();
            tag.putString("currentTheme", get().name);

            FOLDER.mkdirs();
            NbtIo.write(tag, FILE.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        saveTheme();
        saveGlobal();
    }
    
    @EventHandler
    private static void onGameLeft(GameLeftEvent event) {
        // Save themes when leaving game session
        save();
    }
}
