package com.aiden.wuxia.item;

import com.aiden.wuxia.WuxiaMod;
import com.aiden.wuxia.block.ModBlocks;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.function.Function;

public class ModItems {
    public static final Item END_REACTOR = registerBlock(ModBlocks.END_REACTOR);

    public static final ResourceKey<CreativeModeTab> WUXIA_CREATIVE_TAB_KEY = ResourceKey.create(
            BuiltInRegistries.CREATIVE_MODE_TAB.key(), Identifier.fromNamespaceAndPath(WuxiaMod.MOD_ID, "creative_tab")
    );

    public static final CreativeModeTab WUXIA_CREATIVE_TAB = FabricCreativeModeTab.builder()
            .icon(END_REACTOR::getDefaultInstance)
            .title(Component.translatable("creativeTab.wuxia"))
            .displayItems((params, output) -> {
                output.accept(END_REACTOR);
            })
            .build();

    public static Item registerBlock(Block block) {
        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, block.builtInRegistryHolder().key().identifier());
        BlockItem blockItem = new BlockItem(block, new Item.Properties().setId(itemKey).useBlockDescriptionPrefix());
        return Registry.register(BuiltInRegistries.ITEM, itemKey, blockItem);
    }

    public static <T extends Item> T register(String name, Function<Item.Properties, T> itemFactory, Item.Properties settings) {
        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(WuxiaMod.MOD_ID, name));
        T item = itemFactory.apply(settings.setId(itemKey));
        Registry.register(BuiltInRegistries.ITEM, itemKey, item);

        return item;
    }

    public static void initialize() {
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, WUXIA_CREATIVE_TAB_KEY, WUXIA_CREATIVE_TAB);
    }
}
