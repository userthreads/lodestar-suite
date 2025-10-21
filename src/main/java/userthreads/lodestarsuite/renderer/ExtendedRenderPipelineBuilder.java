/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package userthreads.lodestarsuite.renderer;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import userthreads.lodestarsuite.mixininterface.IRenderPipeline;

public class ExtendedRenderPipelineBuilder extends RenderPipeline.Builder {
    private boolean lineSmooth;

    public ExtendedRenderPipelineBuilder(RenderPipeline.Snippet... snippets) {
        for (RenderPipeline.Snippet snippet : snippets) {
            withSnippet(snippet);
        }
    }

    public ExtendedRenderPipelineBuilder withLineSmooth() {
        lineSmooth = true;
        return this;
    }

    @Override
    public RenderPipeline build() {
        RenderPipeline pipeline = super.build();
        ((IRenderPipeline) pipeline).meteor$setLineSmooth(lineSmooth);

        return pipeline;
    }
}
