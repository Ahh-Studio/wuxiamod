package com.aiden.wuxia;

import com.aiden.wuxia.command.ModCommands;
import com.aiden.wuxia.mixin_extension.PlayerMixinExtension;
import com.aiden.wuxia.payloads.WSAttributesC2SPayload;
import com.aiden.wuxia.payloads.WSAttributesS2CPayload;
import com.aiden.wuxia.screen.AttributesScreen;
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
		LOGGER.info("WS Mod Initialized!");
		ModCommands.init();

		PayloadTypeRegistry.clientboundPlay().register(WSAttributesS2CPayload.TYPE, WSAttributesS2CPayload.CODEC);
		ClientPlayNetworking.registerGlobalReceiver(WSAttributesS2CPayload.TYPE, (payload, context) -> {
			LocalPlayer player = context.player();
            PlayerMixinExtension playerMixinExtension = (PlayerMixinExtension) player;
			playerMixinExtension.wuxia$setAllAttributes(payload.wuxiaAttributes());
			Screen screen = context.client().screen;
			if (screen instanceof AttributesScreen oldAttributesScreen) {
				context.client().setScreen(new AttributesScreen(oldAttributesScreen.parent));
			}
		});
		PayloadTypeRegistry.serverboundPlay().register(WSAttributesC2SPayload.TYPE, WSAttributesC2SPayload.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(WSAttributesC2SPayload.TYPE, (_, context) -> {
			PlayerMixinExtension playerMixinExtension = (PlayerMixinExtension) context.player();
			ServerPlayNetworking.send(context.player(), new WSAttributesS2CPayload(playerMixinExtension.wuxia$getAllAttributes()));
		});
	}
}