package com.bigchadguys.bigshopguys.client.theme;

import com.bigchadguys.bigshopguys.BigShopGuys;
import net.minecraft.resources.ResourceLocation;

public record ShopTheme(
        ResourceLocation background,
        ResourceLocation buyButton,
        ResourceLocation buyButtonHovered,
        ResourceLocation buyButtonDisabled,
        ResourceLocation scrollbarTrack,
        ResourceLocation scrollbarThumb,
        ResourceLocation clickSound,
        ResourceLocation hoverSound,
        ResourceLocation scrollSound
) {

    public static final ResourceLocation DEFAULT_ID =
            ResourceLocation.fromNamespaceAndPath(
                    BigShopGuys.MOD_ID,
                    "v1/default"
            );

    public static ShopTheme fromId(ResourceLocation themeId){
        String basePath = "shop_themes/" + themeId.getPath();

        return new ShopTheme(
                ResourceLocation.fromNamespaceAndPath(themeId.getNamespace(), basePath + "/background"),
                ResourceLocation.fromNamespaceAndPath(themeId.getNamespace(), basePath + "/buy_button"),
                ResourceLocation.fromNamespaceAndPath(themeId.getNamespace(), basePath + "/buy_button_hovered"),
                ResourceLocation.fromNamespaceAndPath(themeId.getNamespace(), basePath + "/buy_button_disabled"),
                ResourceLocation.fromNamespaceAndPath(themeId.getNamespace(), basePath + "/scrollbar_track"),
                ResourceLocation.fromNamespaceAndPath(themeId.getNamespace(), basePath + "/scrollbar_thumb"),
                ResourceLocation.fromNamespaceAndPath(themeId.getNamespace(), basePath + "/click"),
                ResourceLocation.fromNamespaceAndPath(themeId.getNamespace(), basePath + "/hover"),
                ResourceLocation.fromNamespaceAndPath(themeId.getNamespace(), basePath + "/scroll")
        );
    }

    public static ShopTheme defaultTheme(){
        return fromId(DEFAULT_ID);
    }

}