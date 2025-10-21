/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package userthreads.lodestarsuite.events.meteor;

import userthreads.lodestarsuite.events.Cancellable;
import userthreads.lodestarsuite.utils.misc.input.KeyAction;

public class KeyEvent extends Cancellable {
    private static final KeyEvent INSTANCE = new KeyEvent();

    public int key, modifiers;
    public KeyAction action;

    public static KeyEvent get(int key, int modifiers, KeyAction action) {
        INSTANCE.setCancelled(false);
        INSTANCE.key = key;
        INSTANCE.modifiers = modifiers;
        INSTANCE.action = action;
        return INSTANCE;
    }
}
