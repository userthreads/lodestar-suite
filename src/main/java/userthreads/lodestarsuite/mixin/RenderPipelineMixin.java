/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package userthreads.lodestarsuite.mixin;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import userthreads.lodestarsuite.mixininterface.IRenderPipeline;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(RenderPipeline.class)
public abstract class RenderPipelineMixin implements IRenderPipeline {
    @Unique
    private boolean lineSmooth;

    @Override
    public void meteor$setLineSmooth(boolean lineSmooth) {
        this.lineSmooth = lineSmooth;
    }

    @Override
    public boolean meteor$getLineSmooth() {
        return lineSmooth;
    }
}
