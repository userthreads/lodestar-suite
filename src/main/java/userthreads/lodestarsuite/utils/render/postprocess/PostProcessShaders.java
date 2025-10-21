package userthreads.lodestarsuite.utils.render.postprocess;

import userthreads.lodestarsuite.utils.PreInit;
import net.minecraft.client.render.VertexConsumerProvider;

public class PostProcessShaders {
    // ChamsShader, EntityOutlineShader, and StorageOutlineShader removed
    public static boolean rendering;

    private PostProcessShaders() {}

    @PreInit
    public static void init() {
        // Shader initialization removed - no post-process shaders
    }

    public static void beginRender() {
        // Shader rendering removed
    }

    public static void endRender() {
        // Shader rendering removed
    }

    public static void onResized(int width, int height) {
        // Shader resizing removed
    }

    public static boolean isCustom(VertexConsumerProvider vcp) {
        // Custom vertex consumer provider check removed
        return false;
    }
}
