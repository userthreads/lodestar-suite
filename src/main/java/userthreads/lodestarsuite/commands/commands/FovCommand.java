/*
 * This file is part of the Lodestar Suite distribution (https://github.com/userthreads/lodestar-suite).
 * Copyright (c) userthreads.
 */

package userthreads.lodestarsuite.commands.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import userthreads.lodestarsuite.commands.Command;
import userthreads.lodestarsuite.mixininterface.ISimpleOption;
import net.minecraft.command.CommandSource;

import userthreads.lodestarsuite.LodestarSuite;

public class FovCommand extends Command {
    public FovCommand() {
        super("fov", "Changes your fov.");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("fov", IntegerArgumentType.integer(0, 180)).executes(context -> {
            int fovValue = context.getArgument("fov", Integer.class);
            
            // Update the game options directly (FOV module removed, using Minecraft's built-in FOV)
            if (LodestarSuite.mc.options != null && LodestarSuite.mc.options.getFov() != null) {
                ((ISimpleOption) (Object) LodestarSuite.mc.options.getFov()).meteor$set(fovValue);
            }
            
            return SINGLE_SUCCESS;
        }));
    }
}
