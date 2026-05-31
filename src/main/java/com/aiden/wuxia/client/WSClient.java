package com.aiden.wuxia.client;

import com.aiden.wuxia.client.keybinding.ModKeyBindings;
import com.aiden.wuxia.screen.MenuScreen;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class WSClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        KeyMapping wsKeyMapping = KeyMappingHelper.registerKeyMapping(
                new KeyMapping("key.wuxia.wuxia", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_F7, ModKeyBindings.WS_KEYMAPPING_CATEGORY)
        );

        ClientTickEvents.END_CLIENT_TICK.register(minecraft -> {
            while (wsKeyMapping.consumeClick()) {
                if (minecraft.player != null) {
                    minecraft.setScreenAndShow(new MenuScreen());
                }
            }
        });
    }
}
