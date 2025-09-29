/*
 * This file is part of the Lodestar Client distribution (https://github.com/copiuum/lodestar-client).
 * Copyright (c) copiuum.
 */

package meteordevelopment.meteorclient.commands.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.mixininterface.ISimpleOption;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.Fov;
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
            
            // Update the FOV module setting if it exists
            Fov fovModule = Modules.get().get(Fov.class);
            if (fovModule != null) {
                fovModule.fov.set(fovValue);
            }
            
            // Also update the game options directly
            if (MeteorClient.mc.options != null && MeteorClient.mc.options.getFov() != null) {
                ((ISimpleOption) (Object) MeteorClient.mc.options.getFov()).meteor$set(fovValue);
            }
            
            return SINGLE_SUCCESS;
        }));
    }
}
