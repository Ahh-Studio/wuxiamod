package com.aiden.wuxia.client.keybinding;

import com.aiden.wuxia.WSMod;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;

public class ModKeyBindings {
    public static final KeyMapping.Category WS_KEYMAPPING_CATEGORY = KeyMapping.Category.register(
            Identifier.fromNamespaceAndPath(WSMod.MOD_ID, "wuxia")
    );
}
