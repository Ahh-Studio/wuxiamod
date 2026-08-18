package com.aiden.wuxia.client;

import com.aiden.wuxia.WuxiaMod;
import com.aiden.wuxia.block.ModBlocks;
import com.aiden.wuxia.client.keybinding.ModKeyBindings;
import com.aiden.wuxia.client.renderer.block_entity.JianghuPortalRenderer;
import com.aiden.wuxia.client.screen.AttributesScreen;
import com.aiden.wuxia.client.screen.EndReactorScreen;
import com.aiden.wuxia.client.screen.MenuScreen;
import com.aiden.wuxia.client.screen.SkillsScreen;
import com.aiden.wuxia.enums.Action;
import com.aiden.wuxia.mixin_extension.PlayerMixinExtension;
import com.aiden.wuxia.payloads.ChargeBlockBreakC2SPayload;
import com.aiden.wuxia.payloads.ChargeReleaseC2SPayload;
import com.aiden.wuxia.payloads.WuxiaAttributesC2SPayload;
import com.aiden.wuxia.payloads.WuxiaAttributesS2CPayload;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class WuxiaClient implements ClientModInitializer {
    private int chargePressTick = -1; // 蓄力按下时的tick，-1表示未蓄力

    @Override
    public void onInitializeClient() {
        KeyMapping wsKeyMapping = KeyMappingHelper.registerKeyMapping(
                new KeyMapping("key.wuxia.wuxia", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_F7, ModKeyBindings.WS_KEYMAPPING_CATEGORY)
        );

        // 在游戏 tick 开始前（即 handleKeybinds 之前）处理蓄力攻击
        ClientTickEvents.START_CLIENT_TICK.register(minecraft -> {
            if (minecraft.player == null) return;
            PlayerMixinExtension ext = (PlayerMixinExtension) minecraft.player;
            if (ext.wuxia$isAwakened()) {
                if (minecraft.options.keyAttack.isDown()) {
                    // 按住左键：设置 missTime 阻止攻击，记录蓄力开始tick
                    minecraft.missTime = 10;
                    if (this.chargePressTick < 0) {
                        this.chargePressTick = minecraft.player.tickCount;
                        WuxiaMod.LOGGER.info("Charge start at tick {}", this.chargePressTick);
                    }

                    int chargeTicks = minecraft.player.tickCount - this.chargePressTick;

                    // 更新蓄力触距加成
                    double reachBonus = Math.max(0.0, (chargeTicks - 10) / 50.0 * 6.0);
                    ext.wuxia$setChargeReachBonus(reachBonus);

                    // 蓄力超过 2.0s（>40 ticks）时产生重锤猛击特效
                    if (chargeTicks > 40 && chargeTicks % 3 == 0) {
                        spawnHeavyHammerEffect(minecraft);
                    }

                    // 蓄力期间产生飘荡的 DUST_PILLAR 粒子，表示内力作用效果
                    if (chargeTicks % 3 == 0) {
                        spawnChargeFloatingParticles(minecraft, chargeTicks);
                    }

                } else if (this.chargePressTick >= 0) {
                    // 松开左键：重置触距加成
                    ext.wuxia$setChargeReachBonus(0.0);

                    // 发送蓄力释放数据包
                    int duration = minecraft.player.tickCount - this.chargePressTick;
                    ClientPlayNetworking.send(new ChargeReleaseC2SPayload(duration));

                    // 蓄力满 3s（60 ticks）且瞄准方块时，通知服务器破坏方块
                    if (duration >= 60 && minecraft.hitResult != null && minecraft.hitResult.getType() == HitResult.Type.BLOCK) {
                        BlockHitResult blockHit = (BlockHitResult) minecraft.hitResult;
                        ClientPlayNetworking.send(new ChargeBlockBreakC2SPayload(blockHit.getBlockPos(), blockHit.getDirection()));
                    }

                    // 蓄力 > 1.5s（>30 ticks）且瞄准方块或实体时，在玩家位置生成750重锤猛击粒子
                    if (duration > 30 && minecraft.hitResult != null && minecraft.hitResult.getType() != HitResult.Type.MISS) {
                        spawnMaceSmashParticles(minecraft);
                    }

                    this.chargePressTick = -1;
                }
            } else {
                if (this.chargePressTick >= 0) {
                    // 非觉醒状态重置触距加成
                    ((PlayerMixinExtension) minecraft.player).wuxia$setChargeReachBonus(0.0);
                }
                this.chargePressTick = -1;
            }
        });

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
                    PlayerMixinExtension ext = (PlayerMixinExtension) localPlayer;

                    // 第1行：血量（左上角靠边）
                    int maxHealth = ext.wuxia$getMaxHealth();
                    int health = ext.wuxia$getHealth();
                    int healthColor = getHealthColor(health, maxHealth);
                    graphics.text(Minecraft.getInstance().font, "Health: " + health + " / " + maxHealth, 2, 32, healthColor, true);

                    // 第2行：内力（蓝色）
                    int mana = ext.wuxia$getMana();
                    int maxMana = ext.wuxia$getMaxMana();
                    graphics.text(Minecraft.getInstance().font, "Mana: " + mana + " / " + maxMana, 2, 44, 0xFF5555FF, true);

                    // 蓄力条（左下角，仅蓄力时显示）
                    if (this.chargePressTick >= 0) {
                        int chargeTicks = localPlayer.tickCount - this.chargePressTick;
                        float chargePercent = Math.min((float) chargeTicks / 60.0f, 1.0f);
                        int barWidth = 80;
                        int barHeight = 6;
                        int barX = 2;
                        int barY = graphics.guiHeight() - 20;
                        int fillWidth = (int) (barWidth * chargePercent);

                        // 蓄力条颜色：绿→黄→红
                        int barColor;
                        if (chargePercent < 0.5f) {
                            barColor = 0xFF00FF00;
                        } else if (chargePercent < 0.8f) {
                            barColor = 0xFFFFFF00;
                        } else {
                            barColor = 0xFFFF0000;
                        }

                        // 背景
                        graphics.fill(barX, barY, barX + barWidth, barY + barHeight, 0xFF333333);
                        // 填充
                        if (fillWidth > 0) {
                            graphics.fill(barX, barY, barX + fillWidth, barY + barHeight, barColor);
                        }
                        // 百分比
                        graphics.text(Minecraft.getInstance().font, String.format("%d%%", (int) (chargePercent * 100)), barX + barWidth + 4, barY - 1, 0xFFFFFFFF, true);
                    }
                }
        );

        MenuScreens.register(ModBlocks.END_REACTOR_MENU_TYPE, EndReactorScreen::new);

        BlockEntityRenderers.register(ModBlocks.JIANGHU_PORTAL_BLOCK_ENTITY_TYPE, JianghuPortalRenderer::new);

        ClientPlayNetworking.registerGlobalReceiver(WuxiaAttributesS2CPayload.TYPE, (payload, context) -> {
            LocalPlayer player = context.player();
            PlayerMixinExtension playerMixinExtension = (PlayerMixinExtension) player;
            playerMixinExtension.wuxia$setAllAttributes(payload.wuxiaAttributes());
            playerMixinExtension.wuxia$setHealth(payload.health());
            playerMixinExtension.wuxia$setMaxHealth(payload.maxHealth());
            playerMixinExtension.wuxia$setAction(Action.safeValueOf(payload.action()));
            playerMixinExtension.wuxia$setAllSkills(payload.skills());
            playerMixinExtension.wuxia$setEquippedSkills(payload.equippedSkills());
            playerMixinExtension.wuxia$setAwakened(payload.awakened());

            Screen screen = context.client().screen;
            if (screen instanceof AttributesScreen attributesScreen) {
                attributesScreen.refreshAttributes();
            }
            if (screen instanceof SkillsScreen skillsScreen) {
                skillsScreen.refreshSkillsList();
            }
        });
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

    /**
     * 在玩家周围产生重锤猛击特效（Mace 风格的 DUST_PILLAR 方块纹理粒子）
     * 参考：ParticleUtils.spawnSmashAttackParticles + LevelEvent.PARTICLES_SMASH_ATTACK (2013)
     */
    private void spawnHeavyHammerEffect(Minecraft minecraft) {
        if (minecraft.level == null) return;
        BlockPos pos = minecraft.player.blockPosition().below();
        Vec3 center = Vec3.atBottomCenterOf(pos).add(0, 0.5, 0);
        RandomSource random = minecraft.player.getRandom();

        BlockParticleOption particle = new BlockParticleOption(ParticleTypes.DUST_PILLAR, minecraft.level.getBlockState(pos));
        int count = 120; // 蓄力期间使用较少的粒子

        // 中心爆发
        for (int i = 0; i < count / 3; i++) {
            double x = center.x + random.nextGaussian() / 2.0;
            double y = center.y;
            double z = center.z + random.nextGaussian() / 2.0;
            minecraft.level.addParticle(particle, x, y, z,
                    random.nextGaussian() * 0.2, random.nextGaussian() * 0.2, random.nextGaussian() * 0.2);
        }

        // 环形扩散（半径 3.5~4.5，匹配 Mace 原始半径）
        for (int i = 0; i < count / 1.5; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double radius = 3.5 + random.nextDouble() * 1.0;
            double x = center.x + radius * Math.cos(angle);
            double z = center.z + radius * Math.sin(angle);
            minecraft.level.addParticle(particle, x, center.y, z,
                    random.nextGaussian() * 0.05, random.nextGaussian() * 0.05, random.nextGaussian() * 0.05);
        }
    }

    /**
     * 在玩家位置生成750个满数量Mace重锤猛击粒子
     */
    private void spawnMaceSmashParticles(Minecraft minecraft) {
        if (minecraft.level == null) return;

        Vec3 pos = minecraft.player.position();
        BlockPos groundPos = minecraft.player.blockPosition().below();
        BlockParticleOption particle = new BlockParticleOption(ParticleTypes.DUST_PILLAR, minecraft.level.getBlockState(groundPos));
        RandomSource random = minecraft.player.getRandom();
        int count = 750;

        // 中心爆发
        for (int i = 0; i < count / 3; i++) {
            double x = pos.x + random.nextGaussian() / 2.0;
            double y = pos.y + 0.5;
            double z = pos.z + random.nextGaussian() / 2.0;
            minecraft.level.addParticle(particle, x, y, z,
                    random.nextGaussian() * 0.2, random.nextGaussian() * 0.2, random.nextGaussian() * 0.2);
        }

        // 环形扩散（半径 3.5~4.5）
        for (int i = 0; i < count / 1.5; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double radius = 3.5 + random.nextDouble() * 1.0;
            double x = pos.x + radius * Math.cos(angle);
            double z = pos.z + radius * Math.sin(angle);
            minecraft.level.addParticle(particle, x, pos.y + 0.5, z,
                    random.nextGaussian() * 0.05, random.nextGaussian() * 0.05, random.nextGaussian() * 0.05);
        }
    }

    /**
     * 蓄力期间在玩家与预计喷射范围之间产生飘荡的 DUST_PILLAR 粒子
     */
    private void spawnChargeFloatingParticles(Minecraft minecraft, int chargeTicks) {
        if (minecraft.level == null) return;

        float chargeSeconds = chargeTicks / 20.0f;
        double expectedRange = Math.min(chargeSeconds * 6.0, 20.0);
        if (expectedRange < 1.0) return;

        Vec3 eyePos = minecraft.player.getEyePosition();
        Vec3 lookVec = minecraft.player.getLookAngle();
        RandomSource random = minecraft.player.getRandom();

        // 使用玩家脚下方块纹理
        BlockPos groundPos = minecraft.player.blockPosition().below();
        BlockParticleOption particle = new BlockParticleOption(ParticleTypes.DUST_PILLAR, minecraft.level.getBlockState(groundPos));

        int count = 3 + random.nextInt(3); // 每 tick 少量粒子

        for (int i = 0; i < count; i++) {
            // 在玩家到预计射程之间随机位置
            double dist = 1.0 + random.nextDouble() * (expectedRange - 1.0);
            // 不在标准线段上，加随机偏移（锥形体积）
            double spread = 0.8 + dist * 0.15;
            double ox = (random.nextDouble() - 0.5) * spread * 2.0;
            double oy = (random.nextDouble() - 0.5) * spread * 2.0;
            double oz = (random.nextDouble() - 0.5) * spread * 2.0;

            Vec3 pos = eyePos.add(
                    lookVec.x * dist + ox,
                    lookVec.y * dist + oy,
                    lookVec.z * dist + oz
            );

            // 飘荡速度（缓慢随机运动）
            double vx = (random.nextDouble() - 0.5) * 0.05;
            double vy = (random.nextDouble() - 0.5) * 0.05;
            double vz = (random.nextDouble() - 0.5) * 0.05;

            minecraft.level.addParticle(particle, pos.x, pos.y, pos.z, vx, vy, vz);
        }
    }
}
