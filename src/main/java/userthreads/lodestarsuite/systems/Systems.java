/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package userthreads.lodestarsuite.systems;

import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import userthreads.lodestarsuite.LodestarSuite;
import userthreads.lodestarsuite.events.game.GameLeftEvent;
// Account system removed for security reasons
import userthreads.lodestarsuite.systems.christmas.ChristmasMode;
import userthreads.lodestarsuite.systems.config.Config;
import userthreads.lodestarsuite.systems.friends.Friends;
import userthreads.lodestarsuite.systems.halloween.HalloweenMode;
import userthreads.lodestarsuite.systems.hud.Hud;
import userthreads.lodestarsuite.systems.macros.Macros;
import userthreads.lodestarsuite.systems.modules.Modules;
// Proxy system removed for security reasons
import userthreads.lodestarsuite.systems.timezone.InternetTimeSync;
import userthreads.lodestarsuite.systems.timezone.TimezoneManager;
import userthreads.lodestarsuite.systems.waypoints.Waypoints;
import meteordevelopment.orbit.EventHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Systems {
    @SuppressWarnings("rawtypes")
    private static final Map<Class<? extends System>, System<?>> systems = new Reference2ReferenceOpenHashMap<>();
    private static final List<Runnable> preLoadTasks = new ArrayList<>(1);

    public static void addPreLoadTask(Runnable task) {
        preLoadTasks.add(task);
    }

    public static void init() {
        // Has to be loaded first so the hidden modules list in config tab can load modules
        add(new Modules());

        Config config = new Config();
        System<?> configSystem = add(config);
        configSystem.init();
        configSystem.load();

        // Registers the colors from config tab. This allows rainbow colours to work for friends.
        config.settings.registerColorSettings(null);

        add(new Macros());
        add(new Friends());
        add(new Waypoints());
        add(new Hud());
        add(new InternetTimeSync());
        add(new TimezoneManager());
        add(new HalloweenMode());
        add(new ChristmasMode());

        LodestarSuite.EVENT_BUS.subscribe(Systems.class);
    }

    public static System<?> add(System<?> system) {
        systems.put(system.getClass(), system);
        LodestarSuite.EVENT_BUS.subscribe(system);
        system.init();

        return system;
    }

    // save/load

    @EventHandler
    private static void onGameLeft(GameLeftEvent event) {
        save();
    }

    public static void save(File folder) {
        long start = java.lang.System.currentTimeMillis();
        LodestarSuite.LOG.info("Saving");

        for (System<?> system : systems.values()) system.save(folder);

        LodestarSuite.LOG.info("Saved in {} milliseconds.", java.lang.System.currentTimeMillis() - start);
    }

    public static void save() {
        save(null);
    }

    public static void load(File folder) {
        long start = java.lang.System.currentTimeMillis();
        LodestarSuite.LOG.info("Loading");

        for (Runnable task : preLoadTasks) task.run();
        for (System<?> system : systems.values()) system.load(folder);

        LodestarSuite.LOG.info("Loaded in {} milliseconds", java.lang.System.currentTimeMillis() - start);
    }

    public static void load() {
        load(null);
    }

    @SuppressWarnings("unchecked")
    public static <T extends System<?>> T get(Class<T> klass) {
        return (T) systems.get(klass);
    }
}
