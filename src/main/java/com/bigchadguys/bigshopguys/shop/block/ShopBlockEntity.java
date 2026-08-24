package com.bigchadguys.bigshopguys.shop.block;

import com.bigchadguys.bigshopguys.block.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ShopBlockEntity extends BlockEntity {

    private ResourceLocation shopId;

    public ShopBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SHOP.get(), pos, state);
    }

    public Optional<ResourceLocation> getShopId() {
        return Optional.ofNullable(shopId);
    }

    public void setShopId(ResourceLocation shopId) {
        this.shopId = shopId;
        setChanged();
    }

    @Override
    protected void saveAdditional(
            @NotNull CompoundTag tag,
            HolderLookup.Provider registries
    ) {
        super.saveAdditional(tag, registries);

        if(shopId != null) {
            tag.putString("shopId", shopId.toString());
        }
    }

    @Override
    protected void loadAdditional(
            @NotNull CompoundTag tag,
            HolderLookup.Provider registries
    ) {
        super.loadAdditional(tag, registries);

        if (tag.contains("shopId")) {
            shopId = ResourceLocation.tryParse(
                    tag.getString("shopId")
            );
        } else {
            shopId = null;
        }
    }
}
