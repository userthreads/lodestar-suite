/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package userthreads.lodestarsuite.gui.widgets;

import userthreads.lodestarsuite.gui.renderer.GuiRenderer;
import userthreads.lodestarsuite.renderer.Texture;

public class WTexture extends WWidget {
    private final double width, height;
    private final double rotation;
    private final Texture texture;

    public WTexture(double width, double height, double rotation, Texture texture) {
        this.width = width;
        this.height = height;
        this.rotation = rotation;
        this.texture = texture;
    }

    @Override
    protected void onCalculateSize() {
        super.width = theme.scale(width);
        super.height = theme.scale(height);
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        renderer.texture(x, y, super.width, super.height, rotation, texture);
    }
}
