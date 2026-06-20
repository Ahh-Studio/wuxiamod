package com.aiden.wuxia.client;

import com.aiden.wuxia.WuxiaMod;
import com.aiden.wuxia.client.keybinding.ModKeyBindings;
import com.aiden.wuxia.mixin_extension.PlayerMixinExtension;
import com.aiden.wuxia.client.screen.MenuScreen;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
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
                (graphics, _) -> {
                    LocalPlayer localPlayer = Minecraft.getInstance().player;
                    if (localPlayer == null) return;
                    PlayerMixinExtension playerMixinExtension = (PlayerMixinExtension) localPlayer;
                    int maxHealth = playerMixinExtension.wuxia$getMaxHealth();
                    int health = playerMixinExtension.wuxia$getHealth();
                    int color = getHealthColor(health, maxHealth);

                    String s = "Health: " + health + " / " + maxHealth;
                    graphics.text(Minecraft.getInstance().font, s, 40, 40, color);
                }
        );
    }

    private int getHealthColor(int health, int maxHealth) {
        float healthPercent = (float) health / maxHealth;
        int color = 0xFFFFFFFF;

        if (healthPercent > 0.8) {
            color = 0xFF00FF00;
        } else if (0.5 < healthPercent && healthPercent <= 0.8) {
            color = 0xFF008000;
        } else if (0.2 < healthPercent && healthPercent <= 0.5) {
            color = 0xFF808000;
        } else if (0.0 < healthPercent && healthPercent <= 0.2) {
            color = 0xFFFF0000;
        }
        return color;
    }
}
