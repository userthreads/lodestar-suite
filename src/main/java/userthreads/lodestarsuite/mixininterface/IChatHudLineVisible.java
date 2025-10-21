/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package userthreads.lodestarsuite.mixininterface;

public interface IChatHudLineVisible extends IChatHudLine {
    boolean meteor$isStartOfEntry();
    void meteor$setStartOfEntry(boolean start);
}
