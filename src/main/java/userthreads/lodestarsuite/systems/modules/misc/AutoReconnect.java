/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package userthreads.lodestarsuite.systems.modules.misc;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectObjectImmutablePair;
import userthreads.lodestarsuite.LodestarSuite;
import userthreads.lodestarsuite.events.world.ServerConnectBeginEvent;
import userthreads.lodestarsuite.settings.BoolSetting;
import userthreads.lodestarsuite.settings.DoubleSetting;
import userthreads.lodestarsuite.settings.Setting;
import userthreads.lodestarsuite.settings.SettingGroup;
import userthreads.lodestarsuite.systems.modules.Categories;
import userthreads.lodestarsuite.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;

public class AutoReconnect extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    public final Setting<Double> time = sgGeneral.add(new DoubleSetting.Builder()
        .name("delay")
        .description("The amount of seconds to wait before reconnecting to the server.")
        .defaultValue(3.5)
        .min(0)
        .decimalPlaces(1)
        .build()
    );

    public final Setting<Boolean> button = sgGeneral.add(new BoolSetting.Builder()
        .name("hide-buttons")
        .description("Will hide the buttons related to Auto Reconnect.")
        .defaultValue(false)
        .build()
    );

    public Pair<ServerAddress, ServerInfo> lastServerConnection;

    public AutoReconnect() {
        super(Categories.Misc, "auto-reconnect", "Automatically reconnects when disconnected from a server.");
        LodestarSuite.EVENT_BUS.subscribe(new StaticListener());
    }

    private class StaticListener {
        @EventHandler
        private void onGameJoined(ServerConnectBeginEvent event) {
            lastServerConnection = new ObjectObjectImmutablePair<>(event.address, event.info);
        }
    }
}
