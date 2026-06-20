package com.aiden.wuxia.client.keybinding;

import com.aiden.wuxia.WuxiaMod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;

@Environment(EnvType.CLIENT)
public class ModKeyBindings {
    public static final KeyMapping.Category WS_KEYMAPPING_CATEGORY = KeyMapping.Category.register(
            Identifier.fromNamespaceAndPath(WuxiaMod.MOD_ID, "wuxia")
    );
}
