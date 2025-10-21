/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package userthreads.lodestarsuite.systems.waypoints;

import userthreads.lodestarsuite.LodestarSuite;
import userthreads.lodestarsuite.events.game.GameJoinedEvent;
import userthreads.lodestarsuite.events.game.GameLeftEvent;
import userthreads.lodestarsuite.systems.System;
import userthreads.lodestarsuite.systems.Systems;
import userthreads.lodestarsuite.systems.waypoints.events.WaypointAddedEvent;
import userthreads.lodestarsuite.systems.waypoints.events.WaypointRemovedEvent;
import userthreads.lodestarsuite.utils.Utils;
import userthreads.lodestarsuite.utils.files.StreamUtils;
import userthreads.lodestarsuite.utils.misc.NbtUtils;
import userthreads.lodestarsuite.utils.player.PlayerUtils;
import userthreads.lodestarsuite.utils.world.Dimension;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.orbit.EventPriority;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Waypoints extends System<Waypoints> implements Iterable<Waypoint> {
    public static final String[] BUILTIN_ICONS = {"square", "circle", "triangle", "star", "diamond", "skull"};

    public final Map<String, AbstractTexture> icons = new ConcurrentHashMap<>();

    private final List<Waypoint> waypoints = Collections.synchronizedList(new ArrayList<>());

    public Waypoints() {
        super(null);
    }

    public static Waypoints get() {
        return Systems.get(Waypoints.class);
    }

    @Override
    public void init() {
        File iconsFolder = new File(new File(LodestarSuite.FOLDER, "waypoints"), "icons");
        iconsFolder.mkdirs();

        for (String builtinIcon : BUILTIN_ICONS) {
            File iconFile = new File(iconsFolder, builtinIcon + ".png");
            if (!iconFile.exists()) copyIcon(iconFile);
        }

        File[] files = iconsFolder.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.getName().endsWith(".png")) {
                try {
                    String name = file.getName().replace(".png", "");
                    AbstractTexture texture = new NativeImageBackedTexture(null, NativeImage.read(new FileInputStream(file)));
                    icons.put(name, texture);
                }
                catch (IOException e) {
                    LodestarSuite.LOG.error("Failed to read a waypoint icon", e);
                }
            }
        }
    }

    /**
     * Adds a waypoint or saves it if it already exists
     * @return {@code true} if waypoint already exists
     */
    public boolean add(Waypoint waypoint) {
        if (waypoints.contains(waypoint)) {
            save();
            return true;
        }

        waypoints.add(waypoint);
        save();

        LodestarSuite.EVENT_BUS.post(new WaypointAddedEvent(waypoint));

        return false;
    }

    public boolean remove(Waypoint waypoint) {
        boolean removed = waypoints.remove(waypoint);
        if (removed) {
            save();
            LodestarSuite.EVENT_BUS.post(new WaypointRemovedEvent(waypoint));
        }

        return removed;
    }

    public Waypoint get(String name) {
        for (Waypoint waypoint : waypoints) {
            if (waypoint.name.get().equalsIgnoreCase(name)) return waypoint;
        }

        return null;
    }

    @EventHandler
    private void onGameJoined(GameJoinedEvent event) {
        load();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onGameDisconnected(GameLeftEvent event) {
        waypoints.clear();
    }

    public static boolean checkDimension(Waypoint waypoint) {
        Dimension playerDim = PlayerUtils.getDimension();
        Dimension waypointDim = waypoint.dimension.get();

        if (playerDim == waypointDim) return true;
        if (!waypoint.opposite.get()) return false;

        boolean playerOpp = playerDim == Dimension.Overworld || playerDim == Dimension.Nether;
        boolean waypointOpp = waypointDim == Dimension.Overworld || waypointDim == Dimension.Nether;

        return playerOpp && waypointOpp;
    }

    @Override
    public File getFile() {
        if (!Utils.canUpdate()) return null;
        return new File(new File(LodestarSuite.FOLDER, "waypoints"), Utils.getFileWorldName() + ".nbt");
    }

    public boolean isEmpty() {
        return waypoints.isEmpty();
    }

    @Override
    public @NotNull Iterator<Waypoint> iterator() {
        return new WaypointIterator();
    }

    private void copyIcon(File file) {
        String path = "/assets/" + LodestarSuite.MOD_ID + "/textures/icons/waypoints/" + file.getName();
        InputStream in = Waypoints.class.getResourceAsStream(path);

        if (in == null) {
            LodestarSuite.LOG.error("Failed to read a resource: {}", path);
            return;
        }

        StreamUtils.copy(in, file);
    }

    @Override
    public NbtCompound toTag() {
        NbtCompound tag = new NbtCompound();
        tag.put("waypoints", NbtUtils.listToTag(waypoints));
        return tag;
    }

    @Override
    public Waypoints fromTag(NbtCompound tag) {
        waypoints.clear();

        for (NbtElement waypointTag : tag.getListOrEmpty("waypoints")) {
            waypoints.add(new Waypoint(waypointTag));
        }

        return this;
    }

    private final class WaypointIterator implements Iterator<Waypoint> {
        private final Iterator<Waypoint> it = waypoints.iterator();

        @Override
        public boolean hasNext() {
            return it.hasNext();
        }

        @Override
        public Waypoint next() {
            return it.next();
        }

        @Override
        public void remove() {
            it.remove();
            save();
        }
    }
}
