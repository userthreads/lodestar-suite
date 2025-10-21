/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package userthreads.lodestarsuite.utils.misc.text;

import userthreads.lodestarsuite.utils.render.color.Color;

/**
 * Encapsulates a string and the color it should have. See {@link TextUtils}
 */
public record ColoredText(String text, Color color) {
}
