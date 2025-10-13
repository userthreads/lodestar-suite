/*
 * This file is part of the Lodestar Suite distribution (https://github.com/waythread/lodestar-suite).
 * Copyright (c) waythread.
 */

package meteordevelopment.meteorclient.systems.modules.misc;

//Created by waythread

import meteordevelopment.discordipc.DiscordIPC;
import meteordevelopment.discordipc.RichPresence;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.game.OpenScreenEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.WidgetScreen;
import meteordevelopment.meteorclient.gui.utils.StarscriptTextBoxRenderer;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WVerticalList;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.misc.MeteorStarscript;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.gui.screen.CreditsScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.AddServerScreen;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.gui.screen.multiplayer.DirectConnectScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.option.*;
import net.minecraft.client.gui.screen.pack.PackScreen;
import net.minecraft.client.gui.screen.world.*;
import net.minecraft.client.realms.gui.screen.RealmsScreen;
import net.minecraft.util.Pair;
import net.minecraft.util.Util;
import org.meteordev.starscript.Script;

import java.util.ArrayList;
import java.util.List;

public class DiscordPresence extends Module {
    public enum SelectMode {
        Random,
        Sequential
    }

    private final SettingGroup sgLine1 = settings.createGroup("Line 1");
    private final SettingGroup sgLine2 = settings.createGroup("Line 2");
    private final SettingGroup sgLine3 = settings.createGroup("Line 3");
    private final SettingGroup sgNameMC = settings.createGroup("NameMC");

    // Line 1

    private final Setting<List<String>> line1Strings = sgLine1.add(new StringListSetting.Builder()
        .name("line-1-messages")
        .description("Messages used for the first line.")
        .defaultValue("{player}", "{server}")
        .onChanged(strings -> recompileLine1())
        .renderer(StarscriptTextBoxRenderer.class)
        .build()
    );

    private final Setting<Integer> line1UpdateDelay = sgLine1.add(new IntSetting.Builder()
        .name("line-1-update-delay")
        .description("How fast to update the first line in ticks.")
        .defaultValue(200)
        .min(10)
        .sliderRange(10, 200)
        .build()
    );

    private final Setting<SelectMode> line1SelectMode = sgLine1.add(new EnumSetting.Builder<SelectMode>()
        .name("line-1-select-mode")
        .description("How to select messages for the first line.")
        .defaultValue(SelectMode.Sequential)
        .build()
    );

    // Line 2

    private final Setting<List<String>> line2Strings = sgLine2.add(new StringListSetting.Builder()
        .name("line-2-messages")
        .description("Messages used for the second line.")
        .defaultValue("lodestar on top", "{round(server.tps, 1)} TPS", "Playing on {server.difficulty} difficulty.", "{server.player_count} Players online")
        .onChanged(strings -> recompileLine2())
        .renderer(StarscriptTextBoxRenderer.class)
        .build()
    );

    private final Setting<Integer> line2UpdateDelay = sgLine2.add(new IntSetting.Builder()
        .name("line-2-update-delay")
        .description("How fast to update the second line in ticks.")
        .defaultValue(100)
        .min(10)
        .sliderRange(10, 200)
        .build()
    );

    private final Setting<SelectMode> line2SelectMode = sgLine2.add(new EnumSetting.Builder<SelectMode>()
        .name("line-2-select-mode")
        .description("How to select messages for the second line.")
        .defaultValue(SelectMode.Sequential)
        .build()
    );

    // Line 3

    private final Setting<List<String>> line3Strings = sgLine3.add(new StringListSetting.Builder()
        .name("line-3-messages")
        .description("Messages used for the third line.")
        .defaultValue("vibe coded, unstable")
        .onChanged(strings -> recompileLine3())
        .renderer(StarscriptTextBoxRenderer.class)
        .build()
    );

    private final Setting<Integer> line3UpdateDelay = sgLine3.add(new IntSetting.Builder()
        .name("line-3-update-delay")
        .description("How fast to update the third line in ticks.")
        .defaultValue(200)
        .min(10)
        .sliderRange(10, 200)
        .build()
    );

    private final Setting<SelectMode> line3SelectMode = sgLine3.add(new EnumSetting.Builder<SelectMode>()
        .name("line-3-select-mode")
        .description("How to select messages for the third line.")
        .defaultValue(SelectMode.Sequential)
        .build()
    );

    // NameMC

    private final Setting<Boolean> showNameMCLink = sgNameMC.add(new BoolSetting.Builder()
        .name("show-namemc-link")
        .description("Show NameMC link in Discord presence.")
        .defaultValue(true)
        .build()
    );

    private static final RichPresence rpc = new RichPresence();
    private SmallImage currentSmallImage;
    private int ticks;
    private boolean forceUpdate, lastWasInMainMenu;

    private final List<Script> line1Scripts = new ArrayList<>();
    private int line1Ticks, line1I;

    private final List<Script> line2Scripts = new ArrayList<>();
    private int line2Ticks, line2I;

    private final List<Script> line3Scripts = new ArrayList<>();
    private int line3Ticks, line3I;

    public static final List<Pair<String, String>> customStates = new ArrayList<>();

    static {
        registerCustomState("com.terraformersmc.modmenu.gui", "Browsing mods");
        registerCustomState("me.jellysquid.mods.sodium.client", "Changing options");
    }

    public DiscordPresence() {
        super(Categories.Misc, "discord-presence", "Displays Lodestar as your presence on Discord.");

        runInMainMenu = true;
    }

    /** Registers a custom state to be used when the current screen is a class in the specified package. */
    public static void registerCustomState(String packageName, String state) {
        for (var pair : customStates) {
            if (pair.getLeft().equals(packageName)) {
                pair.setRight(state);
                return;
            }
        }

        customStates.add(new Pair<>(packageName, state));
    }

    /** The package name must match exactly to the one provided through {@link #registerCustomState(String, String)}. */
    public static void unregisterCustomState(String packageName) {
        customStates.removeIf(pair -> pair.getLeft().equals(packageName));
    }

    @Override
    public void onActivate() {
        DiscordIPC.start(1022550011428474890L, null);

        rpc.setStart(System.currentTimeMillis() / 1000L);

        String largeText = "Lodestar Suite " + MeteorClient.VERSION;
        if (!MeteorClient.BUILD_NUMBER.isEmpty()) largeText += " Build: " + MeteorClient.BUILD_NUMBER;
        rpc.setLargeImage("lodestar_client", largeText);

        currentSmallImage = SmallImage.waythread;

        recompileLine1();
        recompileLine2();
        recompileLine3();

        ticks = 0;
        line1Ticks = 0;
        line2Ticks = 0;
        line3Ticks = 0;
        lastWasInMainMenu = false;

        line1I = 0;
        line2I = 0;
        line3I = 0;
    }

    @Override
    public void onDeactivate() {
        DiscordIPC.stop();
    }

    private void recompile(List<String> messages, List<Script> scripts) {
        scripts.clear();

        for (String message : messages) {
            Script script = MeteorStarscript.compile(message);
            if (script != null) scripts.add(script);
        }

        forceUpdate = true;
    }

    private void recompileLine1() {
        recompile(line1Strings.get(), line1Scripts);
    }

    private void recompileLine2() {
        recompile(line2Strings.get(), line2Scripts);
    }

    private void recompileLine3() {
        recompile(line3Strings.get(), line3Scripts);
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        boolean update = false;

        // Image
        if (ticks >= 200 || forceUpdate) {
            currentSmallImage = currentSmallImage.next();
            currentSmallImage.apply();
            update = true;

            ticks = 0;
        }
        else ticks++;

        if (Utils.canUpdate()) {
            // Line 1
            if (line1Ticks >= line1UpdateDelay.get() || forceUpdate) {
                if (!line1Scripts.isEmpty()) {
                    int i = Utils.random(0, line1Scripts.size());
                    if (line1SelectMode.get() == SelectMode.Sequential) {
                        if (line1I >= line1Scripts.size()) line1I = 0;
                        i = line1I++;
                    }

                    String message = MeteorStarscript.run(line1Scripts.get(i));
                    if (message != null) {
                        // Add NameMC link if enabled and message contains player name
                        if (showNameMCLink.get() && message.contains("{player}") && mc.player != null) {
                            String playerName = mc.player.getGameProfile().getName();
                            message = message.replace("{player}", playerName + " (namemc.com/" + playerName + ")");
                        }
                        rpc.setDetails(message);
                    }
                }
                update = true;

                line1Ticks = 0;
            } else line1Ticks++;

            // Line 2
            if (line2Ticks >= line2UpdateDelay.get() || forceUpdate) {
                if (!line2Scripts.isEmpty()) {
                    int i = Utils.random(0, line2Scripts.size());
                    if (line2SelectMode.get() == SelectMode.Sequential) {
                        if (line2I >= line2Scripts.size()) line2I = 0;
                        i = line2I++;
                    }

                    String message = MeteorStarscript.run(line2Scripts.get(i));
                    if (message != null) rpc.setState(message);
                }
                update = true;

                line2Ticks = 0;
            } else line2Ticks++;

            // Line 3 - Combine with Line 2 since Discord RPC only supports 2 text fields
            if (line3Ticks >= line3UpdateDelay.get() || forceUpdate) {
                if (!line3Scripts.isEmpty()) {
                    int i = Utils.random(0, line3Scripts.size());
                    if (line3SelectMode.get() == SelectMode.Sequential) {
                        if (line3I >= line3Scripts.size()) line3I = 0;
                        i = line3I++;
                    }

                    String line3Message = MeteorStarscript.run(line3Scripts.get(i));
                    if (line3Message != null) {
                        // Use line 3 as alternative state or combine with line 2
                        rpc.setState(line3Message);
                    }
                }
                update = true;

                line3Ticks = 0;
            } else line3Ticks++;
        }
        else {
            if (!lastWasInMainMenu) {
                rpc.setDetails("Lodestar Suite " + (MeteorClient.BUILD_NUMBER.isEmpty() ? MeteorClient.VERSION : MeteorClient.VERSION + " " + MeteorClient.BUILD_NUMBER));

                if (mc.currentScreen instanceof TitleScreen) rpc.setState("Looking at splash screen");
                else if (mc.currentScreen instanceof SelectWorldScreen) rpc.setState("Selecting world");
                else if (mc.currentScreen instanceof CreateWorldScreen || mc.currentScreen instanceof EditGameRulesScreen) rpc.setState("Creating world");
                else if (mc.currentScreen instanceof EditWorldScreen) rpc.setState("Editing world");
                else if (mc.currentScreen instanceof LevelLoadingScreen) rpc.setState("Loading world");
                else if (mc.currentScreen instanceof MultiplayerScreen) rpc.setState("Selecting server");
                else if (mc.currentScreen instanceof AddServerScreen) rpc.setState("Adding server");
                else if (mc.currentScreen instanceof ConnectScreen || mc.currentScreen instanceof DirectConnectScreen) rpc.setState("Connecting to server");
                else if (mc.currentScreen instanceof WidgetScreen) rpc.setState("Browsing Lodestar's GUI");
                else if (mc.currentScreen instanceof OptionsScreen || mc.currentScreen instanceof SkinOptionsScreen || mc.currentScreen instanceof SoundOptionsScreen || mc.currentScreen instanceof VideoOptionsScreen || mc.currentScreen instanceof ControlsOptionsScreen || mc.currentScreen instanceof LanguageOptionsScreen || mc.currentScreen instanceof ChatOptionsScreen || mc.currentScreen instanceof PackScreen || mc.currentScreen instanceof AccessibilityOptionsScreen) rpc.setState("Changing options");
                else if (mc.currentScreen instanceof CreditsScreen) rpc.setState("Reading credits");
                else if (mc.currentScreen instanceof RealmsScreen) rpc.setState("Browsing Realms");
                else {
                    boolean setState = false;
                    if (mc.currentScreen != null) {
                        String className = mc.currentScreen.getClass().getName();
                        for (var pair : customStates) {
                            if (className.startsWith(pair.getLeft())) {
                                rpc.setState(pair.getRight());
                                setState = true;
                                break;
                            }
                        }
                    }
                    if (!setState) rpc.setState("In main menu");
                }

                update = true;
            }
        }

        // Update
        if (update) DiscordIPC.setActivity(rpc);
        forceUpdate = false;
        lastWasInMainMenu = !Utils.canUpdate();
    }

    @EventHandler
    private void onOpenScreen(OpenScreenEvent event) {
        if (!Utils.canUpdate()) lastWasInMainMenu = false;
    }

    @Override
    public WWidget getWidget(GuiTheme theme) {
        WVerticalList list = theme.verticalList();
        
        WButton help = list.add(theme.button("Open documentation.")).expandX().widget();
        help.action = () -> Util.getOperatingSystem().open("https://github.com/waythread/lodestar-suite/wiki/Starscript");
        
        if (mc.player != null) {
            String playerName = mc.player.getGameProfile().getName();
            WButton nameMC = list.add(theme.button("Open NameMC: " + playerName)).expandX().widget();
            nameMC.action = () -> Util.getOperatingSystem().open("https://namemc.com/" + playerName);
        }
        
        return list;
    }

    private enum SmallImage {
        waythread("waythread", "waythread");

        private final String key, text;

        SmallImage(String key, String text) {
            this.key = key;
            this.text = text;
        }

        void apply() {
            rpc.setSmallImage(key, text);
        }

        SmallImage next() {
            return waythread;
        }
    }
}
