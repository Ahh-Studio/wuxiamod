package com.aiden.wuxia.block;

import com.aiden.wuxia.WuxiaMod;
import com.aiden.wuxia.block.container_menu.EndReactorMenu;
import com.aiden.wuxia.block.entity.EndReactorBlockEntity;
import com.aiden.wuxia.block.entity.JianghuPortalBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Function;

public class ModBlocks {
    public static final Block END_REACTOR = register("end_reactor", EndReactor::new, BlockBehaviour.Properties.ofFullCopy(Blocks.END_STONE));
    public static final BlockEntityType<EndReactorBlockEntity> END_REACTOR_BLOCK_ENTITY_TYPE = registerBlockEntity(
            "end_reactor", EndReactorBlockEntity::new, END_REACTOR);
    public static final MenuType<EndReactorMenu> END_REACTOR_MENU_TYPE = registerMenuType("end_reactor", EndReactorMenu::new);

    public static final Block JIANGHU_PORTAL = register("jianghu_portal", JianghuPortalBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.END_PORTAL));
    public static final BlockEntityType<JianghuPortalBlockEntity> JIANGHU_PORTAL_BLOCK_ENTITY_TYPE = registerBlockEntity(
            "jianghu_portal", JianghuPortalBlockEntity::new, JIANGHU_PORTAL);

    public static void initialize() {
    }

    private static <T extends BlockEntity> BlockEntityType<T> registerBlockEntity(String name, FabricBlockEntityTypeBuilder.Factory<? extends T> entityFactory, Block... blocks) {
        Identifier id = Identifier.fromNamespaceAndPath(WuxiaMod.MOD_ID, name);
        return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id, FabricBlockEntityTypeBuilder.<T>create(entityFactory, blocks).build());
    }

    public static <T extends AbstractContainerMenu> MenuType<T> registerMenuType(String name, MenuType.MenuSupplier<T> constructor) {
        return Registry.register(BuiltInRegistries.MENU, name, new MenuType<>(constructor, FeatureFlagSet.of()));
    }

    private static Block register(String name, Function<BlockBehaviour.Properties, Block> blockFactory, BlockBehaviour.Properties properties) {
        ResourceKey<Block> blockKey = keyOfBlock(name);
        Block block = blockFactory.apply(properties.setId(blockKey));

        return Registry.register(BuiltInRegistries.BLOCK, blockKey, block);
    }

    private static ResourceKey<Block> keyOfBlock(String name) {
        return ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(WuxiaMod.MOD_ID, name));
    }
}
