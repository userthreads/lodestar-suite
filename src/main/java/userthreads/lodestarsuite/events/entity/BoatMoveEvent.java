/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package userthreads.lodestarsuite.events.entity;

import net.minecraft.entity.vehicle.AbstractBoatEntity;

public class BoatMoveEvent {
    private static final BoatMoveEvent INSTANCE = new BoatMoveEvent();

    public AbstractBoatEntity boat;

    public static BoatMoveEvent get(AbstractBoatEntity entity) {
        INSTANCE.boat = entity;
        return INSTANCE;
    }
}
