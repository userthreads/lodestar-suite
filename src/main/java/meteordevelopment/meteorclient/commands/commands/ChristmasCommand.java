/*
 * This file is part of the Lodestar Client distribution (https://github.com/copiuum/lodestar-client).
 * Copyright (c) copiuum.
 */

package meteordevelopment.meteorclient.commands.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.gui.GuiThemes;
import meteordevelopment.meteorclient.systems.christmas.ChristmasMode;
import net.minecraft.command.CommandSource;

public class ChristmasCommand extends Command {
    public ChristmasCommand() {
        super("christmas", "Manages Christmas mode and theme.");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(literal("toggle").executes(context -> {
            ChristmasMode christmas = ChristmasMode.get();
            christmas.enabled.set(!christmas.enabled.get());
            if (christmas.enabled.get()) {
                info("Christmas mode manually enabled!");
            } else {
                info("Christmas mode manually disabled!");
            }
            return SINGLE_SUCCESS;
        }));
        
        builder.then(literal("theme").executes(context -> {
            GuiThemes.checkChristmasMode();
            if (ChristmasMode.get().isChristmasSeason()) {
                info("Switched to Christmas theme!");
            } else {
                info("Christmas theme is only available during Christmas season (December 20 - January 6)");
            }
            return SINGLE_SUCCESS;
        }));
        
        builder.then(literal("info").executes(context -> {
            ChristmasMode christmas = ChristmasMode.get();
            info("Christmas Mode Information:");
            info("Manually enabled: %s", christmas.enabled.get() ? "Yes" : "No");
            info("Date-based active: %s", christmas.isChristmasSeason() ? "Yes" : "No");
            info("Currently active: %s", christmas.isActive() ? "Yes" : "No");
            info("Current theme: %s", GuiThemes.get().name);
            info("Snowflakes falling: %s", christmas.isActive() ? "Yes" : "No");
            info("Features: Red/green/white theme, animated snowflakes, festive colors");
            return SINGLE_SUCCESS;
        }));
    }
}
