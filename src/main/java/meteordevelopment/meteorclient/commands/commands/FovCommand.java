/*
 * This file is part of the Lodestar Suite distribution (https://github.com/waythread/lodestar-suite).
 * Copyright (c) waythread.
 */

package meteordevelopment.meteorclient.commands.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.mixininterface.ISimpleOption;
import net.minecraft.command.CommandSource;

import meteordevelopment.meteorclient.MeteorClient;

public class FovCommand extends Command {
    public FovCommand() {
        super("fov", "Changes your fov.");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("fov", IntegerArgumentType.integer(0, 180)).executes(context -> {
            int fovValue = context.getArgument("fov", Integer.class);
            
            // Update the game options directly (FOV module removed, using Minecraft's built-in FOV)
            if (MeteorClient.mc.options != null && MeteorClient.mc.options.getFov() != null) {
                ((ISimpleOption) (Object) MeteorClient.mc.options.getFov()).meteor$set(fovValue);
            }
            
            return SINGLE_SUCCESS;
        }));
    }
}
