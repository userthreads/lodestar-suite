/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package userthreads.lodestarsuite.gui.widgets;

import userthreads.lodestarsuite.gui.widgets.containers.WHorizontalList;
import userthreads.lodestarsuite.utils.misc.Names;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.Iterator;

import static userthreads.lodestarsuite.LodestarSuite.mc;

public class WItemWithLabel extends WHorizontalList {
    private ItemStack itemStack;
    private String name;

    private WItem item;
    private WLabel label;

    public WItemWithLabel(ItemStack itemStack, String name) {
        this.itemStack = itemStack;
        this.name = name;
    }

    @Override
    public void init() {
        item = add(theme.item(itemStack)).widget();
        label = add(theme.label(name + getStringToAppend())).widget();
    }

    private String getStringToAppend() {
        String str = "";

        if (itemStack.getItem() == Items.POTION) {
            Iterator<StatusEffectInstance> effects = itemStack.getItem().getComponents().get(DataComponentTypes.POTION_CONTENTS).getEffects().iterator();
            if (!effects.hasNext()) return str;

            str += " ";

            StatusEffectInstance effect = effects.next();
            if (effect.getAmplifier() > 0) str += "%d ".formatted(effect.getAmplifier() + 1);

            str += "(%s)".formatted(StatusEffectUtil.getDurationText(effect, 1, mc.world != null ? mc.world.getTickManager().getTickRate() : 20.0F).getString());
        }

        return str;
    }

    public void set(ItemStack itemStack) {
        this.itemStack = itemStack;
        item.itemStack = itemStack;

        name = Names.get(itemStack);
        label.set(name + getStringToAppend());
    }

    public String getLabelText() {
        return label == null ? name : label.get();
    }
}
