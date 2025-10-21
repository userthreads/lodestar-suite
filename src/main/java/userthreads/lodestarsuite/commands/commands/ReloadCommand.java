/*
 * This file is part of the Lodestar Suite distribution (https://github.com/userthreads/lodestar-suite).
 * Copyright (c) userthreads.
 */

package userthreads.lodestarsuite.commands.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import userthreads.lodestarsuite.commands.Command;
import userthreads.lodestarsuite.renderer.Fonts;
import userthreads.lodestarsuite.systems.Systems;
import userthreads.lodestarsuite.systems.friends.Friend;
import userthreads.lodestarsuite.systems.friends.Friends;
// Capes system removed - client-sided mod should not have network functionality
import userthreads.lodestarsuite.utils.network.MeteorExecutor;
import net.minecraft.command.CommandSource;

public class ReloadCommand extends Command {
    public ReloadCommand() {
        super("reload", "Reloads many systems.");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            warning("Reloading systems, this may take a while.");

            Systems.load();
            Fonts.refresh();
            MeteorExecutor.execute(() -> Friends.get().forEach(Friend::updateInfo));

            return SINGLE_SUCCESS;
        });
    }
}
