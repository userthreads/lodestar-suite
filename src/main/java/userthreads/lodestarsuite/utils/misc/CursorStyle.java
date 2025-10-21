/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package userthreads.lodestarsuite.utils.misc;

import org.lwjgl.glfw.GLFW;

public enum CursorStyle {
    Default,
    Click,
    Type;

    private boolean created;
    private long cursor;

    public long getGlfwCursor() {
        if (!created) {
            switch (this) {
                case Click -> cursor = GLFW.glfwCreateStandardCursor(GLFW.GLFW_HAND_CURSOR);
                case Type -> cursor = GLFW.glfwCreateStandardCursor(GLFW.GLFW_IBEAM_CURSOR);
                case Default -> cursor = GLFW.glfwCreateStandardCursor(GLFW.GLFW_ARROW_CURSOR);
            }

            created = true;
        }

        return cursor;
    }
}
