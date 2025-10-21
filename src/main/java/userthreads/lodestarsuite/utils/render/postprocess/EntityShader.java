package userthreads.lodestarsuite.utils.render.postprocess;

import com.mojang.blaze3d.systems.RenderSystem;
import userthreads.lodestarsuite.mixininterface.IWorldRenderer;

import java.util.OptionalInt;

import static userthreads.lodestarsuite.LodestarSuite.mc;

public abstract class EntityShader extends PostProcessShader {
    @Override
    public boolean beginRender() {
        if (super.beginRender()) {
            RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "Meteor EntityShader", framebuffer.getColorAttachmentView(), OptionalInt.of(0)).close();
            return true;
        }

        return false;
    }

    @Override
    protected void preDraw() {
        ((IWorldRenderer) mc.worldRenderer).meteor$pushEntityOutlineFramebuffer(framebuffer);
    }

    @Override
    protected void postDraw() {
        ((IWorldRenderer) mc.worldRenderer).meteor$popEntityOutlineFramebuffer();
    }

    public void endRender() {
        endRender(() -> vertexConsumerProvider.draw());
    }
}
