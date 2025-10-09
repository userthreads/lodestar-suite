/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.systems.modules.render;

import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.config.Config;
import meteordevelopment.meteorclient.systems.friends.Friend;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import net.minecraft.world.GameMode;

public class BetterTab extends Module {

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    public final Setting<Integer> tabSize = sgGeneral.add(new IntSetting.Builder()
        .name("tablist-size")
        .description("How many players in total to display in the tablist.")
        .defaultValue(100)
        .min(1)
        .sliderRange(1, 1000)
        .build()
    );

    public final Setting<Integer> tabHeight = sgGeneral.add(new IntSetting.Builder()
        .name("column-height")
        .description("How many players to display in each column.")
        .defaultValue(20)
        .min(1)
        .sliderRange(1, 1000)
        .build()
    );

    private final Setting<Boolean> self = sgGeneral.add(new BoolSetting.Builder()
        .name("highlight-self")
        .description("Highlights yourself in the tablist.")
        .defaultValue(true)
        .build()
    );

    private final Setting<SettingColor> selfColor = sgGeneral.add(new ColorSetting.Builder()
        .name("self-color")
        .description("The color to highlight your name with.")
        .defaultValue(new SettingColor(250, 130, 30))
        .visible(self::get)
        .build()
    );

    private final Setting<Boolean> friends = sgGeneral.add(new BoolSetting.Builder()
        .name("highlight-friends")
        .description("Highlights friends in the tablist.")
        .defaultValue(true)
        .build()
    );

    public final Setting<Boolean> accurateLatency = sgGeneral.add(new BoolSetting.Builder()
        .name("accurate-latency")
        .description("Shows latency as a number in the tablist.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> gamemode = sgGeneral.add(new BoolSetting.Builder()
        .name("gamemode")
        .description("Display gamemode next to the nick.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> blurBackground = sgGeneral.add(new BoolSetting.Builder()
        .name("blur-background")
        .description("Adds a blur backdrop behind the tab list for better readability.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Double> blurIntensity = sgGeneral.add(new DoubleSetting.Builder()
        .name("blur-intensity")
        .description("Intensity of the blur effect for tab list background.")
        .defaultValue(2.0)
        .min(0.0)
        .max(5.0)
        .sliderRange(0.0, 5.0)
        .visible(blurBackground::get)
        .build()
    );

    private final Setting<SettingColor> blurTintColor = sgGeneral.add(new ColorSetting.Builder()
        .name("blur-tint")
        .description("Color to tint the blur effect for tab list background.")
        .defaultValue(new SettingColor(0, 0, 0, 120))
        .visible(blurBackground::get)
        .build()
    );


    public BetterTab() {
        super(Categories.Render, "better-tab", "Various improvements to the tab list.");
    }

    public Text getPlayerName(PlayerListEntry playerListEntry) {
        Text name;
        Color color = null;

        name = playerListEntry.getDisplayName();
        if (name == null) name = Text.literal(playerListEntry.getProfile().getName());

        if (playerListEntry.getProfile().getId().toString().equals(mc.player.getGameProfile().getId().toString()) && self.get()) {
            color = selfColor.get();
        }
        else if (friends.get() && Friends.get().isFriend(playerListEntry)) {
            Friend friend = Friends.get().get(playerListEntry);
            if (friend != null) color = Config.get().friendColor.get();
        }

        if (color != null) {
            String nameString = name.getString();

            for (Formatting format : Formatting.values()) {
                if (format.isColor()) nameString = nameString.replace(format.toString(), "");
            }

            name = Text.literal(nameString).setStyle(name.getStyle().withColor(TextColor.fromRgb(color.getPacked())));
        }

        if (gamemode.get()) {
            GameMode gm = playerListEntry.getGameMode();
            String gmText = "?";
            if (gm != null) {
                gmText = switch (gm) {
                    case SPECTATOR -> "Sp";
                    case SURVIVAL -> "S";
                    case CREATIVE -> "C";
                    case ADVENTURE -> "A";
                };
            }
            MutableText text = Text.literal("");
            text.append(name);
            text.append(" [" + gmText + "]");
            name = text;
        }

        return name;
    }

    /**
     * Renders a blur backdrop for the tab list if enabled
     * This method should be called from the tab list rendering mixin
     */
    public void renderTabListBlurBackdrop(double x, double y, double width, double height) {
        if (!isActive() || !blurBackground.get()) return;

        // Convert SettingColor to Color
        Color blurColor = new Color(
            blurTintColor.get().r,
            blurTintColor.get().g,
            blurTintColor.get().b,
            blurTintColor.get().a
        );

        // Render blur backdrop
        meteordevelopment.meteorclient.gui.renderer.BlurRenderer.getInstance().renderSimpleBlurBackdrop(
            x, y, width, height,
            blurIntensity.get().floatValue(),
            blurColor
        );
    }

}
