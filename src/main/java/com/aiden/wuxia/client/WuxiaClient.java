package com.aiden.wuxia.client;

import com.aiden.wuxia.WuxiaMod;
import com.aiden.wuxia.client.keybinding.ModKeyBindings;
import com.aiden.wuxia.mixin_extension.PlayerMixinExtension;
import com.aiden.wuxia.screen.MenuScreen;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import org.lwjgl.glfw.GLFW;

public class WuxiaClient implements ClientModInitializer {
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

        HudElementRegistry.attachElementBefore(
                VanillaHudElements.HEALTH_BAR,
                Identifier.fromNamespaceAndPath(WuxiaMod.MOD_ID, "health"),
                (graphics, tracker) -> {
                    LocalPlayer localPlayer = Minecraft.getInstance().player;
                    if (localPlayer == null) return;
                    PlayerMixinExtension playerMixinExtension = (PlayerMixinExtension) localPlayer;
                    int maxHealth = playerMixinExtension.wuxia$getMaxHealth();
                    int health = playerMixinExtension.wuxia$getHealth();
                    String s = "Health: " + health + " / " + maxHealth;

                    graphics.text(Minecraft.getInstance().font, s, 40, 40, 0xFFFFFFFF);
                }
        );
    }
}
