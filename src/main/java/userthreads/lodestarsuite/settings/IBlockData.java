/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package userthreads.lodestarsuite.settings;

import userthreads.lodestarsuite.gui.GuiTheme;
import userthreads.lodestarsuite.gui.WidgetScreen;
import userthreads.lodestarsuite.utils.misc.IChangeable;
import userthreads.lodestarsuite.utils.misc.ICopyable;
import userthreads.lodestarsuite.utils.misc.ISerializable;
import net.minecraft.block.Block;

public interface IBlockData<T extends ICopyable<T> & ISerializable<T> & IChangeable & IBlockData<T>> {
    WidgetScreen createScreen(GuiTheme theme, Block block, BlockDataSetting<T> setting);
}
