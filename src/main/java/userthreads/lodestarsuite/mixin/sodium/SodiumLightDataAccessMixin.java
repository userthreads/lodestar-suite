/*
 * This file is part of the Lodestar Suite distribution (https://github.com/userthreads/lodestar-suite).
 * Copyright (c) userthreads.
 */

package userthreads.lodestarsuite.mixin.sodium;

import userthreads.lodestarsuite.systems.modules.Modules;
import userthreads.lodestarsuite.systems.modules.render.Fullbright;
import net.caffeinemc.mods.sodium.client.model.light.data.LightDataAccess;
import net.minecraft.world.LightType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = LightDataAccess.class, remap = false)
public abstract class SodiumLightDataAccessMixin {
    @Unique
    private Fullbright fb;

    @ModifyVariable(method = "compute", at = @At(value = "STORE"), name = "sl")
    private int compute_assignSL(int sl) {
        if (fb == null) fb = Modules.get().get(Fullbright.class);
        return Math.max(fb.getLuminance(LightType.SKY), sl);
    }

    @ModifyVariable(method = "compute", at = @At(value = "STORE"), name = "bl")
    private int compute_assignBL(int bl) {
        if (fb == null) fb = Modules.get().get(Fullbright.class);
        return Math.max(fb.getLuminance(LightType.BLOCK), bl);
    }
}
