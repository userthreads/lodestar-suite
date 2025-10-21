/*
 * This file is part of the Lodestar Suite distribution (https://github.com/userthreads/lodestar-suite).
 * Copyright (c) userthreads.
 */

package userthreads.lodestarsuite.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemConvertible;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Block.class)
public abstract class BlockMixin extends AbstractBlock implements ItemConvertible {
    public BlockMixin(Settings settings) {
        super(settings);
    }

    @ModifyReturnValue(method = "shouldDrawSide", at = @At("RETURN"))
    private static boolean onShouldDrawSide(boolean original, BlockState state, BlockState otherState, Direction side) {
        // Xray module removed - no draw side modifications
        return original;
    }

    @ModifyReturnValue(method = "getSlipperiness", at = @At("RETURN"))
    public float getSlipperiness(float original) {
        // Slippy and NoSlow modules removed - no slipperiness modifications
        return original;
    }
}
