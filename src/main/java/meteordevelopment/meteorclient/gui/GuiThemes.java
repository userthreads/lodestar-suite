/*
 * This file is part of the Lodestar Client distribution (https://github.com/waythread/lodestar-client).
 * Copyright (c) waythread.
 */

package meteordevelopment.meteorclient.gui;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.gui.themes.meteor.ChristmasGuiTheme;
import meteordevelopment.meteorclient.gui.themes.meteor.HalloweenGuiTheme;
import meteordevelopment.meteorclient.gui.themes.meteor.MeteorGuiTheme;
import meteordevelopment.meteorclient.systems.christmas.ChristmasMode;
import meteordevelopment.meteorclient.systems.halloween.HalloweenMode;
import meteordevelopment.meteorclient.utils.PostInit;
import meteordevelopment.meteorclient.utils.PreInit;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GuiThemes {
    private static final File FOLDER = new File(MeteorClient.FOLDER, "gui");
    private static final File THEMES_FOLDER = new File(FOLDER, "themes");
    private static final File FILE = new File(FOLDER, "gui.nbt");

    private static final List<GuiTheme> themes = new ArrayList<>();
    private static GuiTheme theme;

    private GuiThemes() {
    }

    @PreInit
    public static void init() {
        add(new MeteorGuiTheme());
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
        MeteorClient.EVENT_BUS.subscribe(GuiThemes.class);
    }
    
    public static void checkSeasonalThemes() {
        // Auto-switch to seasonal themes based on date
        if (HalloweenMode.get().isActive() && !(get() instanceof HalloweenGuiTheme)) {
            select("Halloween");
            MeteorClient.LOG.info("Auto-switched to Halloween theme for Halloween week!");
        } else if (ChristmasMode.get().isActive() && !(get() instanceof ChristmasGuiTheme)) {
            select("Christmas");
            MeteorClient.LOG.info("Auto-switched to Christmas theme for Christmas season!");
        } else if (!HalloweenMode.get().isActive() && !ChristmasMode.get().isActive() && 
                   (get() instanceof HalloweenGuiTheme || get() instanceof ChristmasGuiTheme)) {
            // Switch back to default theme when not in seasonal periods
            select("Lodestar");
            MeteorClient.LOG.info("Auto-switched back to default theme - seasonal period ended");
        }
    }

    public static void add(GuiTheme theme) {
        for (Iterator<GuiTheme> it = themes.iterator(); it.hasNext();) {
            if (it.next().name.equals(theme.name)) {
                it.remove();

                MeteorClient.LOG.error("Theme with the name '{}' has already been added.", theme.name);
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
