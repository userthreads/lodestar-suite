/*
 * This file is part of the Lodestar Suite distribution (https://github.com/userthreads/lodestar-suite).
 * Copyright (c) userthreads.
 */

package userthreads.lodestarsuite.utils;

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
            userthreads.lodestarsuite.utils.Utils.init();
            userthreads.lodestarsuite.gui.tabs.Tabs.init();
            userthreads.lodestarsuite.gui.GuiThemes.init();
            userthreads.lodestarsuite.utils.world.BlockIterator.init();
            userthreads.lodestarsuite.utils.world.BlockUtils.init();
            userthreads.lodestarsuite.utils.player.Rotations.init();
            userthreads.lodestarsuite.utils.player.EChestMemory.init();
            userthreads.lodestarsuite.utils.render.postprocess.PostProcessShaders.init();
            userthreads.lodestarsuite.utils.network.MeteorExecutor.init();
            userthreads.lodestarsuite.utils.misc.Names.init();
            userthreads.lodestarsuite.utils.misc.CPSUtils.init();
            userthreads.lodestarsuite.utils.misc.MeteorStarscript.init();
            userthreads.lodestarsuite.utils.misc.FakeClientPlayer.init();
            userthreads.lodestarsuite.renderer.FullScreenRenderer.init();
            userthreads.lodestarsuite.renderer.Renderer2D.init();
            userthreads.lodestarsuite.renderer.Fonts.refresh();
        } else if (annotation == PostInit.class) {
            // Call PostInit methods directly
            userthreads.lodestarsuite.commands.Commands.init();
            userthreads.lodestarsuite.utils.render.RenderUtils.init();
            userthreads.lodestarsuite.gui.GuiThemes.postInit();
            userthreads.lodestarsuite.utils.player.ChatUtils.init();
            userthreads.lodestarsuite.utils.render.color.RainbowColors.init();
            userthreads.lodestarsuite.utils.render.PlayerHeadUtils.init();
            userthreads.lodestarsuite.gui.renderer.GuiRenderer.init();
        }
    }

}
