package com.aiden.wuxia.client.keybinding;

import com.aiden.wuxia.WuxiaMod;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;

public class ModKeyBindings {
    public static final KeyMapping.Category WS_KEYMAPPING_CATEGORY = KeyMapping.Category.register(
            Identifier.fromNamespaceAndPath(WuxiaMod.MOD_ID, "wuxia")
    );
}
