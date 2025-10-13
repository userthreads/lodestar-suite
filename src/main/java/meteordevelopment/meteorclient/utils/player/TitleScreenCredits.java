/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.utils.player;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class TitleScreenCredits {
    private static final List<Credit> credits = new ArrayList<>();

    private TitleScreenCredits() {
    }

    private static void init() {
        // Add Lodestar Suite credit
        Credit credit = new Credit();
        credit.text.append(Text.literal("Lodestar Suite").styled(style -> style.withColor(0xFF913DDE)));
        credit.text.append(Text.literal(" by ").formatted(Formatting.GRAY));
        credit.text.append(Text.literal("waythread").formatted(Formatting.WHITE));
        
        credits.add(credit);
    }


    public static void render(DrawContext context) {
        if (credits.isEmpty()) init();

        int y = 3;
        for (Credit credit : credits) {
            synchronized (credit.text) {
                int x = mc.currentScreen.width - 3 - mc.textRenderer.getWidth(credit.text);

                context.drawTextWithShadow(mc.textRenderer, credit.text, x, y, -1);
            }

            y += mc.textRenderer.fontHeight + 2;
        }
    }

    public static boolean onClicked(double mouseX, double mouseY) {
        // No click functionality needed for single credit
        return false;
    }

    private static class Credit {
        public final MutableText text = Text.empty();
    }

}
