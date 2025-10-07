/*
 * This file is part of the Lodestar Client distribution (https://github.com/waythread/lodestar-client).
 * Copyright (c) waythread.
 */

package meteordevelopment.meteorclient.mixin;

import net.minecraft.client.gui.screen.ingame.AbstractFurnaceScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookProvider;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.AbstractFurnaceScreenHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AbstractFurnaceScreen.class)
public abstract class AbstractFurnaceScreenMixin<T extends AbstractFurnaceScreenHandler> extends HandledScreen<T> implements RecipeBookProvider {
    public AbstractFurnaceScreenMixin(T container, PlayerInventory playerInventory, Text name) {
        super(container, playerInventory, name);
    }

    @Override
    public void handledScreenTick() {
        super.handledScreenTick();

        // AutoSmelter module removed - no furnace automation
    }
}
