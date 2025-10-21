/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package userthreads.lodestarsuite.mixin;

import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.model.json.Transformation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemRenderState.LayerRenderState.class)
public interface LayerRenderStateAccessor {
    @Accessor("transform")
    Transformation meteor$getTransform();
}
