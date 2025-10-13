/*
 * This file is part of the Lodestar Suite distribution (https://github.com/waythread/lodestar-suite).
 * Copyright (c) waythread.
 */

package meteordevelopment.meteorclient.utils;

import java.lang.annotation.Annotation;

public class ReflectInit {
    private ReflectInit() {
    }

    public static void registerPackages() {
        // No longer needed - we'll call init methods directly
    }

    public static void init(Class<? extends Annotation> annotation) {
        // Since we removed the addon system, we can call the init methods directly
        // This is much simpler and doesn't require reflection scanning
        
        if (annotation == PreInit.class) {
            // Call PreInit methods directly
            meteordevelopment.meteorclient.utils.Utils.init();
            meteordevelopment.meteorclient.gui.tabs.Tabs.init();
            meteordevelopment.meteorclient.gui.GuiThemes.init();
            meteordevelopment.meteorclient.utils.world.BlockIterator.init();
            meteordevelopment.meteorclient.utils.world.BlockUtils.init();
            meteordevelopment.meteorclient.utils.player.Rotations.init();
            meteordevelopment.meteorclient.utils.player.EChestMemory.init();
            meteordevelopment.meteorclient.utils.render.postprocess.PostProcessShaders.init();
            meteordevelopment.meteorclient.utils.network.MeteorExecutor.init();
            meteordevelopment.meteorclient.utils.misc.Names.init();
            meteordevelopment.meteorclient.utils.misc.CPSUtils.init();
            meteordevelopment.meteorclient.utils.misc.MeteorStarscript.init();
            meteordevelopment.meteorclient.utils.misc.FakeClientPlayer.init();
            meteordevelopment.meteorclient.renderer.FullScreenRenderer.init();
            meteordevelopment.meteorclient.renderer.Renderer2D.init();
            meteordevelopment.meteorclient.renderer.Fonts.refresh();
        } else if (annotation == PostInit.class) {
            // Call PostInit methods directly
            meteordevelopment.meteorclient.commands.Commands.init();
            meteordevelopment.meteorclient.utils.render.RenderUtils.init();
            meteordevelopment.meteorclient.gui.GuiThemes.postInit();
            meteordevelopment.meteorclient.utils.player.ChatUtils.init();
            meteordevelopment.meteorclient.utils.render.color.RainbowColors.init();
            meteordevelopment.meteorclient.utils.render.PlayerHeadUtils.init();
            meteordevelopment.meteorclient.gui.renderer.GuiRenderer.init();
        }
    }

}
