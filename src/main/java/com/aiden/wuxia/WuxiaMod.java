package com.aiden.wuxia;

import com.aiden.wuxia.block.ModBlocks;
import com.aiden.wuxia.client.screen.AttributesScreen;
import com.aiden.wuxia.client.screen.SkillsScreen;
import com.aiden.wuxia.command.ModCommands;
import com.aiden.wuxia.enums.Action;
import com.aiden.wuxia.item.ModItems;
import com.aiden.wuxia.mixin_extension.PlayerMixinExtension;
import com.aiden.wuxia.payloads.SetActionC2SPayload;
import com.aiden.wuxia.payloads.WuxiaAttributesC2SPayload;
import com.aiden.wuxia.payloads.WuxiaAttributesS2CPayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WuxiaMod implements ModInitializer {
    public static final String MOD_ID = "wuxia";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Wuxia Mod Initialized!");
        ModCommands.init();
        ModBlocks.initialize();
        ModItems.initialize();

        PayloadTypeRegistry.clientboundPlay().register(WuxiaAttributesS2CPayload.TYPE, WuxiaAttributesS2CPayload.CODEC);
        ClientPlayNetworking.registerGlobalReceiver(WuxiaAttributesS2CPayload.TYPE, (payload, context) -> {
            LocalPlayer player = context.player();
            PlayerMixinExtension playerMixinExtension = (PlayerMixinExtension) player;
            playerMixinExtension.wuxia$setAllAttributes(payload.wuxiaAttributes());
            playerMixinExtension.wuxia$setHealth(payload.health());
            playerMixinExtension.wuxia$setMaxHealth(payload.maxHealth());
            playerMixinExtension.wuxia$setAction(Action.safeValueOf(payload.action()));
            playerMixinExtension.wuxia$setAllSkills(payload.skills());
            Screen screen = context.client().screen;
            if (screen instanceof AttributesScreen oldAttributesScreen) {
                context.client().setScreen(new AttributesScreen(oldAttributesScreen.parent));
            }
            if (screen instanceof SkillsScreen oldSkillsScreen) {
                context.client().setScreen(new SkillsScreen(oldSkillsScreen.parent));
            }
        });
        PayloadTypeRegistry.serverboundPlay().register(WuxiaAttributesC2SPayload.TYPE, WuxiaAttributesC2SPayload.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(WuxiaAttributesC2SPayload.TYPE, (_, context) -> {
            PlayerMixinExtension playerMixinExtension = (PlayerMixinExtension) context.player();
            ServerPlayNetworking.send(context.player(), new WuxiaAttributesS2CPayload(
                    playerMixinExtension.wuxia$getAllAttributes(), playerMixinExtension.wuxia$getHealth(),
                    playerMixinExtension.wuxia$getMaxHealth(), playerMixinExtension.wuxia$getAction().name(),
                    playerMixinExtension.wuxia$getAllSkills()
            ));
        });
        PayloadTypeRegistry.serverboundPlay().register(SetActionC2SPayload.TYPE, SetActionC2SPayload.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(SetActionC2SPayload.TYPE, (payload, context) -> {
            PlayerMixinExtension playerMixinExtension = (PlayerMixinExtension) context.player();
            playerMixinExtension.wuxia$setAction(Action.safeValueOf(payload.action()));
        });
    }
}