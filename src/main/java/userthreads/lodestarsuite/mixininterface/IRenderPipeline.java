/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package userthreads.lodestarsuite.mixininterface;

public interface IRenderPipeline {
    void meteor$setLineSmooth(boolean lineSmooth);

    boolean meteor$getLineSmooth();
}
