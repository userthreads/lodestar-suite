/*
 * This file is part of the Lodestar Suite distribution (https://github.com/userthreads/lodestar-suite).
 * Copyright (c) userthreads.
 */

package userthreads.lodestarsuite.commands.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import userthreads.lodestarsuite.commands.Command;
import userthreads.lodestarsuite.commands.Commands;
import userthreads.lodestarsuite.systems.config.Config;
import userthreads.lodestarsuite.utils.Utils;
import userthreads.lodestarsuite.utils.player.ChatUtils;
import net.minecraft.command.CommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class CommandsCommand extends Command {
    public CommandsCommand() {
        super("commands", "List of all commands.", "help");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            ChatUtils.info("--- Lodestar Commands ((highlight)%d(default)) ---", Commands.COMMANDS.size());

            MutableText commands = Text.literal("");
            Commands.COMMANDS.forEach(command -> commands.append(getCommandText(command)));
            ChatUtils.sendMsg(commands);

            return SINGLE_SUCCESS;
        });
    }

    private MutableText getCommandText(Command command) {
        // Hover tooltip
        MutableText tooltip = Text.literal("");

        tooltip.append(Text.literal(Utils.nameToTitle(command.getName())).formatted(Formatting.BLUE, Formatting.BOLD)).append("\n");

        MutableText aliases = Text.literal(Config.get().prefix.get() + command.getName());
        if (!command.getAliases().isEmpty()) {
            aliases.append(", ");
            for (String alias : command.getAliases()) {
                if (alias.isEmpty()) continue;
                aliases.append(Config.get().prefix.get() + alias);
                if (!alias.equals(command.getAliases().getLast())) aliases.append(", ");
            }
        }
        tooltip.append(aliases.formatted(Formatting.GRAY)).append("\n\n");

        tooltip.append(Text.literal(command.getDescription()).formatted(Formatting.WHITE));

        // Text
        MutableText text = Text.literal(Utils.nameToTitle(command.getName()));
        if (command != Commands.COMMANDS.getLast())
            text.append(Text.literal(", ").formatted(Formatting.GRAY));
        text.setStyle(text
            .getStyle()
            .withHoverEvent(new HoverEvent.ShowText(tooltip))
            .withClickEvent(new ClickEvent.SuggestCommand(Config.get().prefix.get() + command.getName()))
        );

        return text;
    }

}
