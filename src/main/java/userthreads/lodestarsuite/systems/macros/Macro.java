/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package userthreads.lodestarsuite.systems.macros;

import userthreads.lodestarsuite.gui.utils.StarscriptTextBoxRenderer;
import userthreads.lodestarsuite.settings.*;
import userthreads.lodestarsuite.utils.misc.ISerializable;
import userthreads.lodestarsuite.utils.misc.Keybind;
import userthreads.lodestarsuite.utils.misc.MeteorStarscript;
import userthreads.lodestarsuite.utils.player.ChatUtils;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import org.meteordev.starscript.Script;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static userthreads.lodestarsuite.LodestarSuite.mc;

public class Macro implements ISerializable<Macro> {
    public final Settings settings = new Settings();

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    public Setting<String> name = sgGeneral.add(new StringSetting.Builder()
        .name("name")
        .description("The name of the macro.")
        .build()
    );

    public Setting<List<String>> messages = sgGeneral.add(new StringListSetting.Builder()
        .name("messages")
        .description("The messages for the macro to send.")
        .onChanged(v -> dirty = true)
        .renderer(StarscriptTextBoxRenderer.class)
        .build()
    );

    public Setting<Keybind> keybind = sgGeneral.add(new KeybindSetting.Builder()
        .name("keybind")
        .description("The bind to run the macro.")
        .build()
    );

    private final List<Script> scripts = new ArrayList<>(1);
    private boolean dirty;

    public Macro() {}
    public Macro(NbtElement tag) {
        fromTag((NbtCompound) tag);
    }

    public boolean onAction(boolean isKey, int value, int modifiers) {
        if (!keybind.get().matches(isKey, value, modifiers) || mc.currentScreen != null) return false;
        return onAction();
    }

    public boolean onAction() {
        if (dirty) {
            scripts.clear();

            for (String message : messages.get()) {
                Script script = MeteorStarscript.compile(message);
                if (script != null) scripts.add(script);
            }

            dirty = false;
        }

        for (Script script : scripts) {
            String message = MeteorStarscript.run(script);

            if (message != null) {
                ChatUtils.sendPlayerMsg(message, false);
            }
        }

        return true;
    }

    @Override
    public NbtCompound toTag() {
        NbtCompound tag = new NbtCompound();

        tag.put("settings", settings.toTag());

        return tag;
    }

    @Override
    public Macro fromTag(NbtCompound tag) {
        if (tag.contains("settings")) {
            settings.fromTag(tag.getCompoundOrEmpty("settings"));
        }

        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Macro macro = (Macro) o;
        return Objects.equals(macro.name.get(), this.name.get());
    }
}
