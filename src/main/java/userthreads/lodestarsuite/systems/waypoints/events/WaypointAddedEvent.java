/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package userthreads.lodestarsuite.systems.waypoints.events;

import userthreads.lodestarsuite.systems.waypoints.Waypoint;

public class WaypointAddedEvent {

    public final Waypoint waypoint;

    public WaypointAddedEvent(Waypoint waypoint) {
        this.waypoint = waypoint;
    }
}
