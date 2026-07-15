package com.aiden.wuxia;

import com.aiden.wuxia.block.ModBlocks;
import com.aiden.wuxia.command.ModCommands;
import com.aiden.wuxia.dimension.JianghuOverworldTerrain;
import com.aiden.wuxia.dimension.ModDimensions;
import com.aiden.wuxia.enums.Action;
import com.aiden.wuxia.skill.Skill;
import com.aiden.wuxia.item.ModItems;
import com.aiden.wuxia.mixin_extension.PlayerMixinExtension;
import com.aiden.wuxia.payloads.EquipSkillC2SPayload;
import com.aiden.wuxia.payloads.SetActionC2SPayload;
import com.aiden.wuxia.payloads.WuxiaAttributesC2SPayload;
import com.aiden.wuxia.payloads.WuxiaAttributesS2CPayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
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

        // 在服务器启动时初始化江湖维度出生点附近的主世界地形生成器
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            ServerLevel jianghuLevel = server.getLevel(ModDimensions.JIANGHU);
            if (jianghuLevel != null) {
                if (jianghuLevel.getChunkSource().getGenerator() instanceof NoiseBasedChunkGenerator noiseGen) {
                    BiomeSource biomeSource = noiseGen.getBiomeSource();
                    long seed = jianghuLevel.getSeed();
                    JianghuOverworldTerrain.initialize(biomeSource, seed, jianghuLevel.registryAccess(), noiseGen);
                    LOGGER.info("Jianghu overworld terrain initialized for spawn area (radius: {} blocks)", JianghuOverworldTerrain.RADIUS);
                }
            }
        });

        PayloadTypeRegistry.clientboundPlay().register(WuxiaAttributesS2CPayload.TYPE, WuxiaAttributesS2CPayload.CODEC);

        PayloadTypeRegistry.serverboundPlay().register(WuxiaAttributesC2SPayload.TYPE, WuxiaAttributesC2SPayload.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(WuxiaAttributesC2SPayload.TYPE, (_, context) -> {
            PlayerMixinExtension playerMixinExtension = (PlayerMixinExtension) context.player();
            ServerPlayNetworking.send(context.player(), new WuxiaAttributesS2CPayload(
                    playerMixinExtension.wuxia$getAllAttributes(), playerMixinExtension.wuxia$getHealth(),
                    playerMixinExtension.wuxia$getMaxHealth(), playerMixinExtension.wuxia$getAction().name(),
                    playerMixinExtension.wuxia$getAllSkills(),
                    playerMixinExtension.wuxia$getEquippedSkills()
            ));
        });
        PayloadTypeRegistry.serverboundPlay().register(SetActionC2SPayload.TYPE, SetActionC2SPayload.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(SetActionC2SPayload.TYPE, (payload, context) -> {
            PlayerMixinExtension playerMixinExtension = (PlayerMixinExtension) context.player();
            playerMixinExtension.wuxia$setAction(Action.safeValueOf(payload.action()));
        });

        PayloadTypeRegistry.serverboundPlay().register(EquipSkillC2SPayload.TYPE,  EquipSkillC2SPayload.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(EquipSkillC2SPayload.TYPE, (payload, context) -> {
            Player player = context.player();
            PlayerMixinExtension playerMixinExtension = (PlayerMixinExtension) player;
            Skill skill = Skill.safeValueOf(payload.skill());
            Skill.Type type = Skill.Type.valueOf(payload.skillType());
            if (skill == null) return;
            playerMixinExtension.wuxia$equipSkill(type, skill);
        });
    }
}