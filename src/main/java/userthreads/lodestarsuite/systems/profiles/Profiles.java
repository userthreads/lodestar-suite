/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package userthreads.lodestarsuite.systems.profiles;

import userthreads.lodestarsuite.LodestarSuite;
import userthreads.lodestarsuite.events.game.GameJoinedEvent;
import userthreads.lodestarsuite.systems.System;
import userthreads.lodestarsuite.systems.Systems;
import userthreads.lodestarsuite.utils.Utils;
import userthreads.lodestarsuite.utils.misc.NbtUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Profiles extends System<Profiles> implements Iterable<Profile> {
    public static final File FOLDER = new File(LodestarSuite.FOLDER, "profiles");

    private List<Profile> profiles = new ArrayList<>();

    public Profiles() {
        super("profiles");
    }

    public static Profiles get() {
        return Systems.get(Profiles.class);
    }

    public void add(Profile profile) {
        if (!profiles.contains(profile)) profiles.add(profile);
        profile.save();
        save();
    }

    public void remove(Profile profile) {
        if (profiles.remove(profile)) profile.delete();
        save();
    }

    public Profile get(String name) {
        for (Profile profile : this) {
            if (profile.name.get().equalsIgnoreCase(name)) {
                return profile;
            }
        }

        return null;
    }

    public List<Profile> getAll() {
        return profiles;
    }

    @Override
    public File getFile() {
        return new File(FOLDER, "profiles.nbt");
    }

    @EventHandler
    private void onGameJoined(GameJoinedEvent event) {
        for (Profile profile : this) {
            if (profile.loadOnJoin.get().contains(Utils.getWorldName())) {
                profile.load();
            }
        }
    }

    public boolean isEmpty() {
        return profiles.isEmpty();
    }

    @Override
    public @NotNull Iterator<Profile> iterator() {
        return profiles.iterator();
    }

    @Override
    public NbtCompound toTag() {
        NbtCompound tag = new NbtCompound();
        tag.put("profiles", NbtUtils.listToTag(profiles));
        return tag;
    }

    @Override
    public Profiles fromTag(NbtCompound tag) {
        profiles = NbtUtils.listFromTag(tag.getListOrEmpty("profiles"), Profile::new);
        return this;
    }
}
