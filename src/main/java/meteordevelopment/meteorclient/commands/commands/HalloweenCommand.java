/*
 * This file is part of the Lodestar Client distribution (https://github.com/waythread/lodestar-client).
 * Copyright (c) waythread.
 */

package meteordevelopment.meteorclient.commands.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.gui.GuiThemes;
import meteordevelopment.meteorclient.systems.halloween.HalloweenMode;
import net.minecraft.command.CommandSource;

public class HalloweenCommand extends Command {
    public HalloweenCommand() {
        super("halloween", "Manages Halloween mode and theme.");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(literal("toggle").executes(context -> {
            HalloweenMode halloween = HalloweenMode.get();
            halloween.enabled.set(!halloween.enabled.get());
            if (halloween.enabled.get()) {
                info("Halloween mode manually enabled!");
            } else {
                info("Halloween mode manually disabled!");
            }
            return SINGLE_SUCCESS;
        }));
        
        builder.then(literal("theme").executes(context -> {
            GuiThemes.checkHalloweenMode();
            if (HalloweenMode.get().isHalloweenWeek()) {
                info("Switched to Halloween theme!");
            } else {
                info("Halloween theme is only available during Halloween week (October 27 - November 2)");
            }
            return SINGLE_SUCCESS;
        }));
        
        builder.then(literal("info").executes(context -> {
            HalloweenMode halloween = HalloweenMode.get();
            info("Halloween Mode Information:");
            info("Manually enabled: %s", halloween.enabled.get() ? "Yes" : "No");
            info("Date-based active: %s", halloween.isHalloweenWeek() ? "Yes" : "No");
            info("Currently active: %s", halloween.isActive() ? "Yes" : "No");
            info("Current theme: %s", GuiThemes.get().name);
            info("Bats flying: %s", halloween.isActive() ? "Yes" : "No");
            info("Features: Orange/dark theme, animated bats, spooky glow effects");
            return SINGLE_SUCCESS;
        }));
    }
}
