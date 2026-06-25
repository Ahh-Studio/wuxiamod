package com.aiden.wuxia.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;

import java.util.concurrent.CompletableFuture;

public class WuxiaDimensionProvider extends FabricDynamicRegistryProvider {
    public WuxiaDimensionProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void configure(HolderLookup.Provider registries, Entries entries) {
        entries.addAll(registries.lookupOrThrow(Registries.DIMENSION));
        entries.addAll(registries.lookupOrThrow(Registries.DIMENSION_TYPE));
    }

    @Override
    public String getName() {
        return "Wuxia Dimensions";
    }
}
