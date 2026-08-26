package com.bigchadguys.bigshopguys.client.screen;

import com.bigchadguys.bigshopguys.menu.ShopMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class ShopScreen extends AbstractContainerScreen<ShopMenu> {

    public ShopScreen(
            ShopMenu menu,
            Inventory playerInventory,
            Component title
    ) {
        super(menu, playerInventory, title);

        this.imageWidth = 176;
        this.imageHeight = 100;
    }

    @Override
    protected void renderBg(
            @NotNull GuiGraphics guiGraphics,
            float partialTick,
            int mouseX,
            int mouseY
    ) {
        int x = this.leftPos;
        int y = this.topPos;

        guiGraphics.fill(
                x,
                y,
                x + this.imageWidth,
                y + this.imageHeight,
                0xCC202020
        );
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(
                this.font,
                this.title,
                8,
                8,
                0xFFFFFF,
                false
        );

        graphics.drawString(
                this.font,
                "Shop ID: " + this.menu.getShopId(),
                8,
                28,
                0xFFFFFF,
                false
        );

        graphics.drawString(
                this.font,
                "Menu connection successful!",
                8,
                46,
                0xFFFFFF,
                false
        );
    }
}
