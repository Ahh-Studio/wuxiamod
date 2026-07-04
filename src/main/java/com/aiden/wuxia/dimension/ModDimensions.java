package com.aiden.wuxia.dimension;

import com.aiden.wuxia.WuxiaMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public class ModDimensions {
    public static final ResourceKey<Level> JIANGHU = ResourceKey.create(Registries.DIMENSION, Identifier.fromNamespaceAndPath(WuxiaMod.MOD_ID, "jianghu"));
}
