/*
 * This file is part of the Lodestar Suite distribution (https://github.com/userthreads/lodestar-suite).
 * Copyright (c) userthreads.
 */

package userthreads.lodestarsuite.commands.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import userthreads.lodestarsuite.commands.Command;
import userthreads.lodestarsuite.commands.arguments.ModuleArgumentType;
import userthreads.lodestarsuite.commands.arguments.SettingArgumentType;
import userthreads.lodestarsuite.commands.arguments.SettingValueArgumentType;
import userthreads.lodestarsuite.gui.GuiThemes;
import userthreads.lodestarsuite.gui.WidgetScreen;
import userthreads.lodestarsuite.gui.tabs.TabScreen;
import userthreads.lodestarsuite.gui.tabs.Tabs;
import userthreads.lodestarsuite.gui.tabs.builtin.HudTab;
import userthreads.lodestarsuite.settings.Setting;
import userthreads.lodestarsuite.systems.modules.Module;
import userthreads.lodestarsuite.utils.Utils;
import net.minecraft.command.CommandSource;

public class SettingCommand extends Command {
    public SettingCommand() {
        super("settings", "Allows you to view and change module settings.", "s");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(
            literal("hud")
                .executes(context -> {
                    TabScreen screen = Tabs.get(HudTab.class).createScreen(GuiThemes.get());
                    screen.parent = null;

                    Utils.screenToOpen = screen;
                    return SINGLE_SUCCESS;
                })
        );

        // Open module screen
        builder.then(
            argument("module", ModuleArgumentType.create())
                .executes(context -> {
                    Module module = context.getArgument("module", Module.class);

                    WidgetScreen screen = GuiThemes.get().moduleScreen(module);
                    screen.parent = null;

                    Utils.screenToOpen = screen;
                    return SINGLE_SUCCESS;
                })
        );

        // View or change settings
        builder.then(
                argument("module", ModuleArgumentType.create())
                .then(
                        argument("setting", SettingArgumentType.create())
                        .executes(context -> {
                            // Get setting value
                            Setting<?> setting = SettingArgumentType.get(context);

                            ModuleArgumentType.get(context).info("Setting (highlight)%s(default) is (highlight)%s(default).", setting.title, setting.get());

                            return SINGLE_SUCCESS;
                        })
                        .then(
                                argument("value", SettingValueArgumentType.create())
                                .executes(context -> {
                                    // Set setting value
                                    Setting<?> setting = SettingArgumentType.get(context);
                                    String value = SettingValueArgumentType.get(context);

                                    if (setting.parse(value)) {
                                        ModuleArgumentType.get(context).info("Setting (highlight)%s(default) changed to (highlight)%s(default).", setting.title, value);
                                    }

                                    return SINGLE_SUCCESS;
                                })
                        )
                )
        );
    }
}
