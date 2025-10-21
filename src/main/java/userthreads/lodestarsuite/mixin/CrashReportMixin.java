/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package userthreads.lodestarsuite.mixin;

import userthreads.lodestarsuite.LodestarSuite;
import userthreads.lodestarsuite.systems.hud.Hud;
import userthreads.lodestarsuite.systems.hud.HudElement;
import userthreads.lodestarsuite.systems.hud.elements.TextHud;
import userthreads.lodestarsuite.systems.modules.Category;
import userthreads.lodestarsuite.systems.modules.Module;
import userthreads.lodestarsuite.systems.modules.Modules;
import net.minecraft.util.crash.CrashReport;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(CrashReport.class)
public abstract class CrashReportMixin {
    @Inject(method = "addDetails", at = @At("TAIL"))
    private void onAddDetails(StringBuilder sb, CallbackInfo info) {
        sb.append("\n\n-- Meteor Client --\n\n");
        sb.append("Version: ").append(LodestarSuite.VERSION).append("\n");
        if (!LodestarSuite.BUILD_NUMBER.isEmpty()) {
            sb.append("Build: ").append(LodestarSuite.BUILD_NUMBER).append("\n");
        }

        if (Modules.get() != null) {
            boolean modulesActive = false;
            for (Category category : Modules.loopCategories()) {
                List<Module> modules = Modules.get().getGroup(category);
                boolean categoryActive = false;

                for (Module module : modules) {
                    if (module == null || !module.isActive()) continue;

                    if (!modulesActive) {
                        modulesActive = true;
                        sb.append("\n[[ Active Modules ]]\n");
                    }

                    if (!categoryActive) {
                        categoryActive = true;
                        sb.append("\n[")
                          .append(category)
                          .append("]:\n");
                    }

                    sb.append(module.name).append("\n");
                }

            }

        }

        if (Hud.get() != null && Hud.get().active) {
            boolean hudActive = false;
            for (HudElement element : Hud.get()) {
                if (element == null || !element.isActive()) continue;

                if (!hudActive) {
                    hudActive = true;
                    sb.append("\n[[ Active Hud Elements ]]\n");
                }

                if (!(element instanceof TextHud textHud)) sb.append(element.info.name).append("\n");
                else {
                    sb.append("Text\n{")
                      .append(textHud.text.get())
                      .append("}\n");
                    if (textHud.shown.get() != TextHud.Shown.Always) {
                        sb.append("(")
                          .append(textHud.shown.get())
                          .append(textHud.condition.get())
                          .append(")\n");
                    }
                }
            }
        }
    }
}
