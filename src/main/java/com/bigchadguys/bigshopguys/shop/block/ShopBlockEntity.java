package com.bigchadguys.bigshopguys.shop.block;

import com.bigchadguys.bigshopguys.block.ModBlockEntities;
import com.bigchadguys.bigshopguys.component.ModDataComponents;
import com.bigchadguys.bigshopguys.menu.ShopMenu;
import com.bigchadguys.bigshopguys.shop.ShopDefinition;
import com.bigchadguys.bigshopguys.shop.ShopRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ShopBlockEntity extends BlockEntity implements MenuProvider {

    private ResourceLocation shopId;

    public ShopBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SHOP.get(), pos, state);
    }

    public Optional<ResourceLocation> getShopId() {
        return Optional.ofNullable(shopId);
    }

    public static final ModelProperty<ResourceLocation> SHOP_ID_PROPERTY = new ModelProperty<>();

    public void setShopId(ResourceLocation shopId) {
        this.shopId = shopId;

        setChanged();
        requestModelDataUpdate();
    }

    @Override
    public @NotNull ModelData getModelData() {
        if (shopId == null) {
            return ModelData.EMPTY;
        }

        return ModelData.builder().with(
                SHOP_ID_PROPERTY,shopId
        )
                .build();
    }

    @Override
    protected void saveAdditional(
            @NotNull CompoundTag tag,
            HolderLookup.@NotNull Provider registries
    ) {
        super.saveAdditional(tag, registries);

        if(shopId != null) {
            tag.putString("shop_id", shopId.toString());
        }
    }

    @Override
    protected void loadAdditional(
            @NotNull CompoundTag tag,
            HolderLookup.@NotNull Provider registries
    ) {
        super.loadAdditional(tag, registries);

        if (tag.contains("shop_id")) {
            shopId = ResourceLocation.tryParse(
                    tag.getString("shop_id")
            );
        } else {
            shopId = null;
        }
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.@NotNull Builder builder) {
        super.collectImplicitComponents(builder);

        if (shopId != null) {
            builder.set(
                    ModDataComponents.SHOP_ID.get(),
                    shopId
            );
        }
    }

    @Override
    protected void applyImplicitComponents(
            @NotNull DataComponentInput input
    ) {
        super.applyImplicitComponents(input);

        shopId = input.get(
                ModDataComponents.SHOP_ID.get()
        );
    }

    @Override
    public @NotNull Component getDisplayName() {
        if (level != null && shopId != null) {

            var registry = level.registryAccess()
                    .registryOrThrow(
                            ShopRegistries.SHOP_DEFINITION_REGISTRY_KEY
                    );

            ShopDefinition definition = registry.get(shopId);

            if (definition != null) {
                return definition.title();
            }
        }

        return Component.translatable(
                "shop.bigshopguys.missing_shop_definition"
        );
    }


    @Override
    public @Nullable AbstractContainerMenu createMenu(
            int containerId,
            Inventory inventory,
            Player player
        ) {
            if (shopId == null) {
                return null;
            }

            return new ShopMenu(
                    containerId,
                    inventory,
                    worldPosition,
                    shopId
            );
        }
}