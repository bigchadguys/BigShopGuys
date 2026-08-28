package com.bigchadguys.bigshopguys.menu;

import com.bigchadguys.bigshopguys.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ShopMenu extends AbstractContainerMenu {
    private final BlockPos shopPos;
    private final ResourceLocation shopId;
    private final ContainerLevelAccess access;

    // Client
    public ShopMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buffer) {
        this(containerId, playerInventory, buffer.readBlockPos(), buffer.readResourceLocation());
    }

    // Server
    public ShopMenu(int containerId, Inventory playerInventory, BlockPos shopPos, ResourceLocation shopId) {
        super(ModMenus.SHOP.get(), containerId);

        this.shopPos = shopPos;
        this.shopId = shopId;
        this.access = ContainerLevelAccess.create(
                playerInventory.player.level(),
                shopPos
        );

        addPlayerInventory(playerInventory);
    }

    private void addPlayerInventory(Inventory playerInventory) {
        int inventoryStartY = 176;

        for(int row = 0; row < 3; ++row) {
            for(int column = 0; column < 9; ++column) {
                this.addSlot(
                        new Slot(
                                playerInventory,
                                column + row * 9 + 9,
                                8 + column * 18,
                                inventoryStartY + row * 18
                        )
                );
            }
        }

        int hotBayY = 234;

        for(int column = 0; column < 9; ++column) {
            this.addSlot(
                    new Slot(
                            playerInventory,
                            column,
                            8 + column * 18,
                            hotBayY
                    )
            );
        }
    }

    public ResourceLocation getShopId() {
        return shopId;
    }

    public BlockPos getShopPos() {
        return shopPos;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return stillValid(
                access,
                player,
                ModBlocks.SHOP.get()
        );
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int i) {
        return ItemStack.EMPTY;
    }
}
