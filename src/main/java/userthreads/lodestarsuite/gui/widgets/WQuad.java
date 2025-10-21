/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package userthreads.lodestarsuite.gui.widgets;

import userthreads.lodestarsuite.utils.render.color.Color;

public abstract class WQuad extends WWidget {
    public Color color;

    public WQuad(Color color) {
        this.color = color;
    }

    @Override
    protected void onCalculateSize() {
        double s = theme.scale(32);

        width = s;
        height = s;
    }
}
