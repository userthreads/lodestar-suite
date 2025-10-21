/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package userthreads.lodestarsuite.gui.renderer;

import userthreads.lodestarsuite.gui.utils.Cell;
import userthreads.lodestarsuite.gui.widgets.WWidget;
import userthreads.lodestarsuite.gui.widgets.containers.WContainer;
import userthreads.lodestarsuite.renderer.MeshBuilder;
import userthreads.lodestarsuite.renderer.MeshRenderer;
import userthreads.lodestarsuite.renderer.MeteorRenderPipelines;
import userthreads.lodestarsuite.utils.render.color.Color;
import net.minecraft.client.MinecraftClient;

public class GuiDebugRenderer {
    private static final Color CELL_COLOR = new Color(25, 225, 25);
    private static final Color WIDGET_COLOR = new Color(25, 25, 225);

    private final MeshBuilder mesh = new MeshBuilder(MeteorRenderPipelines.WORLD_COLORED_LINES);

    public void render(WWidget widget) {
        if (widget == null) return;

        mesh.begin();
        renderWidget(widget);
        mesh.end();

        MeshRenderer.begin()
            .attachments(MinecraftClient.getInstance().getFramebuffer())
            .pipeline(MeteorRenderPipelines.WORLD_COLORED_LINES)
            .mesh(mesh)
            .end();
    }

    private void renderWidget(WWidget widget) {
        lineBox(widget.x, widget.y, widget.width, widget.height, WIDGET_COLOR);

        if (widget instanceof WContainer) {
            for (Cell<?> cell : ((WContainer) widget).cells) {
                lineBox(cell.x, cell.y, cell.width, cell.height, CELL_COLOR);
                renderWidget(cell.widget());
            }
        }
    }

    private void lineBox(double x, double y, double width, double height, Color color) {
        line(x, y, x + width, y, color);
        line(x + width, y, x + width, y + height, color);
        line(x, y, x, y + height, color);
        line(x, y + height, x + width, y + height, color);
    }

    private void line(double x1, double y1, double x2, double y2, Color color) {
        mesh.ensureLineCapacity();

        mesh.line(
            mesh.vec3(x1, y1, 0).color(color).next(),
            mesh.vec3(x2, y2, 0).color(color).next()
        );
    }
}
