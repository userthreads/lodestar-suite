/*
 * This file is part of the Lodestar Client distribution (https://github.com/waythread/lodestar-client).
 * Copyright (c) waythread.
 */

package meteordevelopment.meteorclient.systems.modules.render;

import meteordevelopment.meteorclient.events.render.GetFovEvent;
import meteordevelopment.meteorclient.mixininterface.ISimpleOption;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;

import meteordevelopment.meteorclient.MeteorClient;

public class Fov extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    
    // Static flag to track when we're rendering hands
    private static boolean renderingHands = false;

    public final Setting<Integer> fov = sgGeneral.add(new IntSetting.Builder()
        .name("fov")
        .description("Your custom FOV.")
        .defaultValue(120)
        .min(0)
        .max(180)
        .sliderMin(30)
        .sliderMax(180)
        .onChanged(this::onFovChanged)
        .build()
    );

    public Fov() {
        super(Categories.Render, "fov", "Allows you to change your FOV.");
    }

    @Override
    public void onActivate() {
        if (MeteorClient.mc.options != null && MeteorClient.mc.options.getFov() != null) {
            ((ISimpleOption) (Object) MeteorClient.mc.options.getFov()).meteor$set(fov.get());
        }
    }

    @Override
    public void onDeactivate() {
        if (MeteorClient.mc.options != null && MeteorClient.mc.options.getFov() != null) {
            ((ISimpleOption) (Object) MeteorClient.mc.options.getFov()).meteor$set(120); // Reset to default FOV
        }
    }

    @EventHandler(priority = 1000) // High priority to run after Zoom module
    private void onGetFov(GetFovEvent event) {
        if (isActive() && !renderingHands) {
            event.fov = fov.get();
        }
    }
    
    // Static methods to control hand rendering state
    public static void setRenderingHands(boolean rendering) {
        renderingHands = rendering;
    }
    
    public static boolean isRenderingHands() {
        return renderingHands;
    }

    private void onFovChanged(Integer value) {
        if (isActive() && MeteorClient.mc.options != null && MeteorClient.mc.options.getFov() != null) {
            ((ISimpleOption) (Object) MeteorClient.mc.options.getFov()).meteor$set(value);
        }
    }
}
