/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package userthreads.lodestarsuite.utils.misc;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.function.Supplier;

public class Pool<T> {
    private final Queue<T> items = new ArrayDeque<>();
    private final Supplier<T> producer;

    public Pool(Producer<T> producer) {
        this.producer = producer::create;
    }

    public synchronized T get() {
        if (!items.isEmpty()) return items.poll();
        return producer.get();
    }

    public synchronized void free(T obj) {
        items.offer(obj);
    }
}
