/*
 * This file is part of the Lodestar Client distribution (https://github.com/waythread/lodestar-client).
 * Copyright (c) waythread.
 */

package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.BetterTooltips;
import meteordevelopment.meteorclient.systems.modules.render.ItemHighlight;
import meteordevelopment.meteorclient.utils.Utils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.BookScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static meteordevelopment.meteorclient.MeteorClient.mc;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin<T extends ScreenHandler> extends Screen implements ScreenHandlerProvider<T> {
    @Shadow
    protected Slot focusedSlot;

    @Shadow
    protected int x;
    @Shadow
    protected int y;

    @Shadow
    @Nullable
    protected abstract Slot getSlotAt(double xPosition, double yPosition);

    @Shadow
    public abstract T getScreenHandler();

    @Shadow
    private boolean doubleClicking;

    @Shadow
    protected abstract void onMouseClick(Slot slot, int invSlot, int clickData, SlotActionType actionType);

    @Shadow
    public abstract void close();

    @Unique
    private static final ItemStack[] ITEMS = new ItemStack[27];

    public HandledScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void onInit(CallbackInfo info) {
        // InventoryTweaks module removed - no inventory buttons
    }

    // Inventory Tweaks
    @Inject(method = "mouseDragged", at = @At("TAIL"))
    private void onMouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY, CallbackInfoReturnable<Boolean> info) {
        // InventoryTweaks module removed - no mouse drag modifications
    }

    // Middle click open
    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void mouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        BetterTooltips tooltips = Modules.get().get(BetterTooltips.class);

        if (button == GLFW.GLFW_MOUSE_BUTTON_MIDDLE && focusedSlot != null && !focusedSlot.getStack().isEmpty() && getScreenHandler().getCursorStack().isEmpty() && tooltips.middleClickOpen()) {
            ItemStack itemStack = focusedSlot.getStack();
            if (Utils.hasItems(itemStack) || itemStack.getItem() == Items.ENDER_CHEST) {
                cir.setReturnValue(Utils.openContainer(focusedSlot.getStack(), ITEMS, false));
            }
            else if (itemStack.get(DataComponentTypes.WRITTEN_BOOK_CONTENT) != null || itemStack.get(DataComponentTypes.WRITABLE_BOOK_CONTENT) != null) {
                close();
                mc.setScreen(new BookScreen(BookScreen.Contents.create(itemStack)));
                cir.setReturnValue(true);
            }
        }
    }

    // Item Highlight
    @Inject(method = "drawSlot", at = @At("HEAD"))
    private void onDrawSlot(DrawContext context, Slot slot, CallbackInfo ci) {
        int color = Modules.get().get(ItemHighlight.class).getColor(slot.getStack());
        if (color != -1) context.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, color);
    }
}
