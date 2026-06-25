package com.aiden.wuxia.datagen;

import com.aiden.wuxia.dimension.ModDimensions;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;

public class WuxiaModDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        //FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        //pack.addProvider(WuxiaDimensionProvider::new);
    }

    @Override
    public void buildRegistry(RegistrySetBuilder registryBuilder) {
        //registryBuilder.add(Registries.DIMENSION_TYPE, ModDimensions::bootstrapDimensionType);
    }
}
