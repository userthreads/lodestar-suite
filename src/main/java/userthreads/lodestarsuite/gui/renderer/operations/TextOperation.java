/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package userthreads.lodestarsuite.gui.renderer.operations;

import userthreads.lodestarsuite.gui.renderer.GuiRenderOperation;
import userthreads.lodestarsuite.renderer.text.TextRenderer;

public class TextOperation extends GuiRenderOperation<TextOperation> {
    private String text;
    private TextRenderer renderer;

    public boolean title;

    public TextOperation set(String text, TextRenderer renderer, boolean title) {
        this.text = text;
        this.renderer = renderer;
        this.title = title;

        return this;
    }

    @Override
    protected void onRun() {
        renderer.render(text, x, y, color);
    }
}
