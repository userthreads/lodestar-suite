/*
 * This file is part of the Lodestar Client distribution (https://github.com/copiuum/lodestar-client).
 * Copyright (c) copiuum.
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

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class Fov extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

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
        if (mc.options != null && mc.options.getFov() != null) {
            ((ISimpleOption) (Object) mc.options.getFov()).meteor$set(fov.get());
        }
    }

    @Override
    public void onDeactivate() {
        if (mc.options != null && mc.options.getFov() != null) {
            ((ISimpleOption) (Object) mc.options.getFov()).meteor$set(70); // Reset to default FOV
        }
    }

    @EventHandler
    private void onGetFov(GetFovEvent event) {
        if (isActive()) {
            event.fov = fov.get();
        }
    }

    private void onFovChanged(Integer value) {
        if (isActive() && mc.options != null && mc.options.getFov() != null) {
            ((ISimpleOption) (Object) mc.options.getFov()).meteor$set(value);
        }
    }
}
