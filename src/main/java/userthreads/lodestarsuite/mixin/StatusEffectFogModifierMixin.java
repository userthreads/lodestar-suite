/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package userthreads.lodestarsuite.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import userthreads.lodestarsuite.systems.modules.Modules;
import userthreads.lodestarsuite.systems.modules.render.NoRender;
import net.minecraft.client.render.fog.StatusEffectFogModifier;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(StatusEffectFogModifier.class)
public abstract class StatusEffectFogModifierMixin {
    @Shadow
    public abstract RegistryEntry<StatusEffect> getStatusEffect();

    @ModifyReturnValue(method = "shouldApply", at = @At("RETURN"))
    private boolean modifyShouldApply(boolean original) {
        NoRender noRender = Modules.get().get(NoRender.class);
        if (getStatusEffect() == StatusEffects.BLINDNESS) return original && !noRender.noBlindness();
        if (getStatusEffect() == StatusEffects.DARKNESS) return original && !noRender.noDarkness();
        return original;
    }
}
