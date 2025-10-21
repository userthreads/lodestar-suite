/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package userthreads.lodestarsuite.gui.renderer;

import userthreads.lodestarsuite.utils.misc.Pool;
import userthreads.lodestarsuite.utils.render.color.Color;

public abstract class GuiRenderOperation<T extends GuiRenderOperation<T>> {
    protected double x, y;
    protected Color color;

    public void set(double x, double y, Color color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }

    @SuppressWarnings("unchecked")
    public void run(Pool<T> pool) {
        onRun();
        pool.free((T) this);
    }

    protected abstract void onRun();
}
