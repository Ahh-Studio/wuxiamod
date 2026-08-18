package com.aiden.wuxia.mixin;

import com.aiden.wuxia.WuxiaMod;
import com.aiden.wuxia.enums.Action;
import com.aiden.wuxia.skill.Skill;
import com.aiden.wuxia.mixin.accessor.LivingEntityAccessor;
import com.aiden.wuxia.mixin.invoker.PlayerEntityInvoker;
import com.aiden.wuxia.mixin_extension.PlayerMixinExtension;
import com.aiden.wuxia.payloads.WuxiaAttributesS2CPayload;
import com.aiden.wuxia.skill.Skills;
import com.aiden.wuxia.util.PlayerUtil;
import com.mojang.authlib.GameProfile;
import it.unimi.dsi.fastutil.doubles.DoubleDoubleImmutablePair;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlocksAttacks;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;

/**
 * 为玩家添加了一些实体数据。如技能、属性、武侠生命值、是否觉醒等。<br>
 * 还修改了一些玩家的计算逻辑。
 * @see PlayerMixinExtension
 * @see PlayerUtil
 * @author Aiden
 */
@Mixin(Player.class)
public abstract class PlayerMixin implements PlayerMixinExtension {
    // 基本拳脚、基本内功、基本招架、基本轻功、基本剑法
    @Unique public Map<Skill, Integer> skills = new HashMap<>();
    @Unique public int[] wuxiaAttributes = {
            0, 0, // 内力、内力上限 [0~1]
            25, 0, 25, 0, 15, 0, 15, 0, // 先天臂力、后天臂力、先天根骨、后天根骨、先天身法、后天身法、先天悟性、后天悟性 [2~9]
            0, 0, 0, 0, 0, // 攻击、防御、命中、躲闪、招架 [10~14]
            0, 0, 0, 0, 0, // Δ攻击、Δ防御、Δ命中、Δ躲闪、Δ招架 [15~19]
            0, 0, 0, 0, 0 // 攻击%、防御%、命中%、躲闪%、招架% [20~24]
    };
    @Unique public Skill[] equippedSkills = {
            Skills.JIBENQUANJIAO, Skills.JIBENNEIGONG, Skills.JIBENZHAOJIA, Skills.JIBENQINGGONG, Skills.JIBENJIANFA
    };
    @Unique public boolean awakened = false;
    @Unique public int wuxiaHealth = 0;
    @Unique public int wuxiaMaxHealth = 0;
    @Unique public int wuxiaDeltaHealth = 0;
    @Unique public int wuxiaHealthPercent = 0;
    @Unique private int wuxiaAttributesPacketSendCounter = 19;
    @Unique public Action wuxiaAction = Action.NONE;
    @Unique public int particleColor = 0xD3D3D3; // 内力粒子颜色，默认灰白色
    @Unique public double chargeReachBonus = 0.0; // 蓄力触距加成
    @Unique public int lastChargeDuration = 0; // 上次蓄力时长（tick），用于伤害倍率计算

    @Inject(method = "<init>", at = @At(value = "TAIL"))
    public void injectedInit(Level level, GameProfile gameProfile, CallbackInfo ci) {
        for (Skill skill : Skill.values()) {
            this.skills.put(skill, 0);
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At(value = "TAIL"))
    public void addAdditionalSaveData(ValueOutput output, CallbackInfo ci) {
        output.putBoolean("Awakened", this.awakened);
        output.putInt("wuxiaHealth", this.wuxiaHealth);
        output.putInt("wuxiaMaxHealth", this.wuxiaMaxHealth);
        output.putInt("wuxiaDeltaHealth", this.wuxiaDeltaHealth);
        output.putInt("wuxiaHealthPercent", this.wuxiaHealthPercent);
        output.putString("wuxiaAction", this.wuxiaAction.name().toLowerCase());
        output.putInt("ParticleColor", this.particleColor);

        PlayerUtil.addAdditionalSkillsData(output, this.skills);
        PlayerUtil.addAdditionalEquippedSkillsData(output, this.equippedSkills);
        PlayerUtil.addAdditionalAttributesData(output, this.wuxiaAttributes);
    }

    @Inject(method = "readAdditionalSaveData", at = @At(value = "TAIL"))
    public void readAdditionalSaveData(ValueInput input, CallbackInfo ci) {
        this.awakened = input.getBooleanOr("Awakened", false);
        this.wuxiaHealth = input.getIntOr("wuxiaHealth", 0);
        this.wuxiaMaxHealth = input.getIntOr("wuxiaMaxHealth", 0);
        this.wuxiaDeltaHealth = input.getIntOr("wuxiaDeltaHealth", 0);
        this.wuxiaHealthPercent = input.getIntOr("wuxiaHealthPercent", 0);
        this.wuxiaAction = Action.safeValueOf(input.getStringOr("wuxiaAction", "none").toUpperCase());
        this.particleColor = input.getIntOr("ParticleColor", 0xD3D3D3);

        this.skills = PlayerUtil.readAdditionalSkillsData(input, this.skills);
        this.equippedSkills = PlayerUtil.readAdditionalEquippedSkillsData(input, this.equippedSkills);
        this.wuxiaAttributes = PlayerUtil.readAdditionalAttributesData(input, this.wuxiaAttributes);
    }

    @Inject(method = "tick", at = @At(value = "TAIL"))
    public void tick(CallbackInfo ci) {
        Player instance = (Player) (Object) this;
        if (this.awakened) {
            // 发包，由外部改变`wuxiaAttributesPacketSendCounter`触发，把数据同步到客户端
            if (this.wuxiaAttributesPacketSendCounter > 0) {
                this.wuxiaAttributesPacketSendCounter--;
            } else {
                WuxiaAttributesS2CPayload wuxiaAttributesS2CPayload = new WuxiaAttributesS2CPayload(
                        this.wuxiaAttributes, this.wuxiaHealth, this.wuxiaMaxHealth,
                        this.wuxiaAction.name(), this.skills, this.equippedSkills,
                        this.awakened
                );
                if (instance instanceof ServerPlayer serverPlayer) {
                    ServerPlayNetworking.send(serverPlayer, wuxiaAttributesS2CPayload);
                }
            }

            // 更新属性数值
            this.updateAttributes();

            // 疗伤和打坐逻辑（死亡时跳过）
            if (this.wuxiaHealth > 0 && this.wuxiaAction == Action.HEAL && this.wuxiaHealth < this.wuxiaMaxHealth) {
                int i = Math.min(1, this.wuxiaMaxHealth / 4000);
                this.wuxiaHealth = Math.min(this.wuxiaMaxHealth, this.wuxiaHealth + i);
            }

            if (this.wuxiaAction == Action.MEDITATE) {
                int mana = this.wuxia$getMana();
                int maxMana = this.wuxia$getMaxMana();
                if (mana < maxMana) {
                    int i = Math.max(1, maxMana / 12000);
                    this.wuxia$setMana(Math.min(maxMana, mana + i));
                }
            }
        }
    }

    @Unique
    public boolean isDeadOrDying() {
        if (this.awakened) {
            return this.wuxiaHealth <= 0;
        } else {
            return ((Player) (Object) this).getHealth() <= 0;
        }
    }

    @Redirect(method = "hurtServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Avatar;hurtServer(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    public boolean hurtServer(Avatar avatar, final ServerLevel level, final DamageSource source, float damage) {
        Player instance = (Player) avatar;
        if (instance.isInvulnerableTo(level, source)) {
            return false;
        } else if (instance.isDeadOrDying()) {
            return false;
        } else if (source.is(DamageTypeTags.IS_FIRE) && instance.hasEffect(MobEffects.FIRE_RESISTANCE)) {
            return false;
        } else {
            if (instance.isSleeping()) {
                instance.stopSleeping();
            }

            instance.setNoActionTime(0);
            if (damage < 0.0F) {
                damage = 0.0F;
            }

            ItemStack itemInUse = instance.getUseItem();
            float damageBlocked = instance.applyItemBlocking(level, source, damage);
            damage -= damageBlocked;
            boolean blocked = damageBlocked > 0.0F;
            if (source.is(DamageTypeTags.IS_FREEZING) && instance.is(EntityTypeTags.FREEZE_HURTS_EXTRA_TYPES)) {
                damage *= 5.0F;
            }

            if (source.is(DamageTypeTags.DAMAGES_HELMET) && !instance.getItemBySlot(EquipmentSlot.HEAD).isEmpty()) {
                this.hurtHelmet(source, damage);
                damage *= 0.75F;
            }

            if (Float.isNaN(damage) || Float.isInfinite(damage)) {
                damage = Float.MAX_VALUE;
            }

            boolean tookFullDamage = true;
            if (instance.invulnerableTime > 10.0F && !source.is(DamageTypeTags.BYPASSES_COOLDOWN)) {
                if (damage <= instance.lastHurt) {
                    return false;
                }

                this.wuxia$ActuallyHurt(level, source, damage - instance.lastHurt);
                instance.lastHurt = damage;
                tookFullDamage = false;
            } else {
                instance.lastHurt = damage;
                instance.invulnerableTime = 20;
                this.wuxia$ActuallyHurt(level, source, damage);
                instance.hurtDuration = 10;
                instance.hurtTime = instance.hurtDuration;
            }

            instance.resolveMobResponsibleForDamage(source);
            instance.resolvePlayerResponsibleForDamage(source);
            if (tookFullDamage) {
                BlocksAttacks blocksAttacks = itemInUse.get(DataComponents.BLOCKS_ATTACKS);
                if (blocked && blocksAttacks != null) {
                    blocksAttacks.onBlocked(level, instance);
                } else {
                    level.broadcastDamageEvent(instance, source);
                }

                if (!source.is(DamageTypeTags.NO_IMPACT) && (!blocked || damage > 0.0F)) {
                    instance.markHurt();
                }

                if (!source.is(DamageTypeTags.NO_KNOCKBACK)) {
                    double xd = 0.0;
                    double zd = 0.0;
                    if (source.getDirectEntity() instanceof Projectile projectile) {
                        DoubleDoubleImmutablePair knockbackDirection = projectile.calculateHorizontalHurtKnockbackDirection(instance, source);
                        xd = -knockbackDirection.leftDouble();
                        zd = -knockbackDirection.rightDouble();
                    } else if (source.getSourcePosition() != null) {
                        xd = source.getSourcePosition().x() - instance.getX();
                        zd = source.getSourcePosition().z() - instance.getZ();
                    }

                    instance.knockback(0.4F, xd, zd);
                    if (!blocked) {
                        instance.indicateDamage(xd, zd);
                    }
                }
            }

            if (instance.isDeadOrDying()) {
                if (!instance.checkTotemDeathProtection(source)) {
                    if (tookFullDamage) {
                        instance.makeSound(instance.getDeathSound());
                        instance.playSecondaryHurtSound(source);
                    }

                    instance.die(source);
                    // 死后将玩家状态重置为无状态（停止疗伤/打坐等）
                    this.wuxiaAction = Action.NONE;
                }
            } else if (tookFullDamage) {
                instance.playHurtSound(source);
                instance.playSecondaryHurtSound(source);
            }

            boolean success = !blocked || damage > 0.0F;
            if (success) {
                instance.lastDamageSource = source;
                instance.lastDamageStamp = instance.level().getGameTime();

                for (MobEffectInstance effect : instance.getActiveEffects()) {
                    effect.onMobHurt(level, instance, source, damage);
                }
            }

            if (instance instanceof ServerPlayer serverPlayer) {
                CriteriaTriggers.ENTITY_HURT_PLAYER.trigger(serverPlayer, source, damage, damage, blocked);
                if (damageBlocked > 0.0F && damageBlocked < 3.4028235E37F) {
                    serverPlayer.awardStat(Stats.DAMAGE_BLOCKED_BY_SHIELD, Math.round(damageBlocked * 10.0F));
                }
            }

            if (source.getEntity() instanceof ServerPlayer sourcePlayer) {
                CriteriaTriggers.PLAYER_HURT_ENTITY.trigger(sourcePlayer, instance, source, damage, damage, blocked);
            }

            return success;
        }
    }

    @Unique
    public void wuxia$ActuallyHurt(final ServerLevel level, final DamageSource source, float dmg) {
        Player instance = (Player) (Object) this;
        if (!instance.isInvulnerableTo(level, source)) {
            if (dmg != 0.0F) {
                instance.causeFoodExhaustion(source.getFoodExhaustion());
                instance.getCombatTracker().recordDamage(source, dmg);
                if (this.awakened) {
                    // 觉醒玩家使用自定义气血系统
                    this.wuxiaHealth -= (int) dmg;
                    if (this.wuxiaHealth <= 0) {
                        this.wuxiaHealth = 0;
                        instance.setHealth(0.0F);
                    }
                } else {
                    // 未觉醒玩家使用原版生命值
                    instance.setHealth(instance.getHealth() - dmg);
                }
                if (dmg < 3.4028235E37F) {
                    instance.awardStat(Stats.DAMAGE_TAKEN, Math.round(dmg * 10.0F));
                }
                instance.gameEvent(GameEvent.ENTITY_DAMAGE);
            }
        }
    }

    @Shadow
    protected abstract void hurtHelmet(final DamageSource damageSource, final float damage);

    @SuppressWarnings("deprecation")
    @Redirect(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurtOrSimulate(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    public boolean hurtOrSimulate(Entity instance, DamageSource source, float damage) {
        if (this.awakened) {
            // 蓄力伤害倍率：0s→0.5x, 3s(60tick)→2x，线性，无蓄力普攻也是0.5x
            float multiplier = 0.5f + (this.lastChargeDuration / 60.0f) * 1.5f;
            multiplier = Math.min(multiplier, 2.0f); // 上限2x
            return instance.hurtOrSimulate(source, this.wuxia$getAttack() * multiplier);
        }
        return instance.hurtOrSimulate(source, damage);
    }

    @Inject(method = "travel", at = @At(value = "HEAD"), cancellable = true)
    public void travel(Vec3 input, CallbackInfo ci) {
        if (this.wuxiaAction != Action.NONE) {
            ci.cancel();
        }
    }

    /**
     * 服务器端处理蓄力释放：根据当前视角做射线检测，攻击目标实体或空挥，
     * 并根据蓄力时长决定是否产生内力化形粒子。
     * @param chargeDuration 蓄力时长（tick数）
     */
    @Unique
    @Override
    public void wuxia$handleChargeRelease(int chargeDuration) {
        Player instance = (Player) (Object) this;
        if (!this.awakened) {
            WuxiaMod.LOGGER.warn("handleChargeRelease called but not awakened!");
            return;
        }
        if (!(instance.level() instanceof ServerLevel serverLevel)) {
            WuxiaMod.LOGGER.warn("handleChargeRelease called on non-server level!");
            return;
        }

        float chargeSeconds = chargeDuration / 20.0f;
        WuxiaMod.LOGGER.info("handleChargeRelease: duration={} ticks", chargeDuration);

        // 射线检测找当前目标（松手时的指向），射程根据蓄力时长动态增长
        Entity targetEntity;
        double reachBonus = Math.max(0.0, (chargeDuration - 10) / 50.0 * 6.0);
        double reach = Math.min(10.0, 6.0 + reachBonus);
        Vec3 eyePos = instance.getEyePosition();
        Vec3 lookVec = instance.getLookAngle();
        Vec3 reachVec = eyePos.add(lookVec.x * reach, lookVec.y * reach, lookVec.z * reach);

        AABB aabb = instance.getBoundingBox().expandTowards(lookVec.scale(reach)).inflate(1.0);
        EntityHitResult entityHitResult = ProjectileUtil.getEntityHitResult(
                instance, eyePos, reachVec, aabb,
                entity -> !entity.isSpectator() && entity.isPickable(),
                reach * reach
        );

        if (entityHitResult != null) {
            targetEntity = entityHitResult.getEntity();
            // 记录蓄力时长用于伤害倍率计算
            this.lastChargeDuration = chargeDuration;
            // 攻击当前指向的目标（松手时的指向）
            instance.attack(targetEntity);
            // 攻击后重置
            this.lastChargeDuration = 0;
        } else {
            // 空挥：显示挥动动画并重置攻击冷却
            instance.swing(InteractionHand.MAIN_HAND);
            instance.resetAttackStrengthTicker();
        }

        // 蓄力达到0.5秒以上，产生内力化形粒子
        if (chargeSeconds >= 0.5f) {
            float intensity = 0.1f + (chargeSeconds - 0.5f) * 0.36f;
            intensity = Math.min(intensity, 1.0f);
            spawnInnerForceParticles(intensity);
        }
    }

    @Unique
    private void spawnInnerForceParticles(float chargeIntensity) {
        Player instance = (Player) (Object) this;
        if (!(instance.level() instanceof ServerLevel serverLevel)) return;

        int mana = wuxia$getMana();
        if (mana <= 0) return;

        float manaPercent = Math.min((float) mana / 10000000, 1.0f);
        float effectFactor = manaPercent * chargeIntensity;

        int baseCount = Math.round(120 * effectFactor);
        if (baseCount <= 0) return;

        ItemStack weapon = instance.getWeaponItem();
        boolean hasSword = !weapon.isEmpty() && weapon.is(item -> item.is(ItemTags.SWORDS));
        if (hasSword) {
            baseCount = Math.max(baseCount / 2, 1);
        }

        float maxLength = effectFactor * 20.0f;

        Vec3 look = instance.getLookAngle();
        Vec3 eyePos = instance.getEyePosition();
        RandomSource random = instance.getRandom();

        float startDist = 0.0f;
        float range = maxLength - startDist;
        if (range <= 0) return;

        int rgb = particleColor & 0xFFFFFF;

        // 计算垂直于视线方向的基向量，用于环形波
        Vec3 ringU = Math.abs(look.y) < 0.99
                ? new Vec3(0, 1, 0).cross(look).normalize()
                : new Vec3(1, 0, 0).cross(look).normalize();
        Vec3 ringV = look.cross(ringU).normalize();

        // ============ Layer 1: 核心光柱 ============
        int coreCount = Math.max(baseCount / 3, 3);
        int coreAlpha = 0x99;
        int coreColor = (coreAlpha << 24) | rgb;
        float coreSpread = 0.05f;
        for (int i = 0; i < coreCount; i++) {
            float distance = startDist + random.nextFloat() * range;
            double ox = (random.nextDouble() - 0.5) * coreSpread * 2.0;
            double oy = (random.nextDouble() - 0.5) * coreSpread * 2.0;
            double oz = (random.nextDouble() - 0.5) * coreSpread * 2.0;
            Vec3 pos = eyePos.add(
                    look.x * distance + ox,
                    look.y * distance + oy - 0.15,
                    look.z * distance + oz
            );
            float scale = 0.3f + random.nextFloat() * 0.2f;
            serverLevel.sendParticles(new DustParticleOptions(coreColor, scale),
                    pos.x, pos.y, pos.z, 1, 0.0, 0.0, 0.0, 0.0);
        }

        // ============ Layer 2: 主锥形波（散射更开） ============
        int mainCount = baseCount * 2 / 3;
        int mainAlpha = 0x80;
        int mainColor = (mainAlpha << 24) | rgb;
        for (int i = 0; i < mainCount; i++) {
            float distance = startDist + random.nextFloat() * range;
            float spread = 0.5f + distance * 0.3f;
            double ox = (random.nextDouble() - 0.5) * spread * 2.0;
            double oy = (random.nextDouble() - 0.5) * spread * 2.0;
            double oz = (random.nextDouble() - 0.5) * spread * 2.0;
            Vec3 pos = eyePos.add(
                    look.x * distance + ox,
                    look.y * distance + oy - 0.15,
                    look.z * distance + oz
            );
            float scale = 0.5f + random.nextFloat() * 0.4f;
            serverLevel.sendParticles(new DustParticleOptions(mainColor, scale),
                    pos.x, pos.y, pos.z, 1, 0.0, 0.0, 0.0, 0.0);
        }

        // ============ Layer 3: 散落火星 ============
        int sparkCount = Math.max(baseCount / 2, 3);
        int sparkAlpha = 0x50;
        int sparkColor = (sparkAlpha << 24) | rgb;
        float sparkSpread = 0.7f;
        for (int i = 0; i < sparkCount; i++) {
            float distance = startDist + random.nextFloat() * range * 1.2f;
            double ox = (random.nextDouble() - 0.5) * sparkSpread * 2.0;
            double oy = (random.nextDouble() - 0.5) * sparkSpread * 2.0;
            double oz = (random.nextDouble() - 0.5) * sparkSpread * 2.0;
            Vec3 pos = eyePos.add(
                    look.x * distance + ox,
                    look.y * distance + oy - 0.15,
                    look.z * distance + oz
            );
            float scale = 0.1f + random.nextFloat() * 0.15f;
            serverLevel.sendParticles(new DustParticleOptions(sparkColor, scale),
                    pos.x, pos.y, pos.z, 1, 0.0, 0.0, 0.0, 0.0);
        }

        // ============ Layer 4: 环形波（从3格起，每2格一圈） ============
        int ringAlpha = 0x88;
        int ringColor = (ringAlpha << 24) | rgb;
        for (float d = 3.0f; d <= maxLength; d += 2.0f) {
            float ringRadius = 1.5f + (d - 3.0f) * 0.3f;
            int ringParticles = 12 + (int) (d - 3) * 2;
            Vec3 center = eyePos.add(look.x * d, look.y * d - 0.15, look.z * d);
            // 外圈：随机角度偏移 + 随机径向偏移
            for (int j = 0; j < ringParticles; j++) {
                double baseAngle = 2.0 * Math.PI * j / ringParticles;
                double angle = baseAngle + (random.nextDouble() - 0.5) * 0.4;
                double r = ringRadius + (random.nextDouble() - 0.5) * 1.6;
                double rx = r * Math.cos(angle);
                double ry = r * Math.sin(angle);
                Vec3 pos = center.add(ringU.scale(rx).add(ringV.scale(ry)));
                float scale = 0.3f + random.nextFloat() * 0.25f;
                serverLevel.sendParticles(new DustParticleOptions(ringColor, scale),
                        pos.x, pos.y, pos.z, 1, 0.0, 0.0, 0.0, 0.0);
            }
            // 内圈：半径/2，偏移量减半，角度错开
            float innerRadius = ringRadius * 0.5f;
            int innerParticles = Math.max(ringParticles / 2, 4);
            for (int j = 0; j < innerParticles; j++) {
                double baseAngle = 2.0 * Math.PI * j / innerParticles + Math.PI / innerParticles;
                double angle = baseAngle + (random.nextDouble() - 0.5) * 0.3;
                double r = innerRadius + (random.nextDouble() - 0.5) * 0.8;
                double rx = r * Math.cos(angle);
                double ry = r * Math.sin(angle);
                Vec3 pos = center.add(ringU.scale(rx).add(ringV.scale(ry)));
                float scale = 0.25f + random.nextFloat() * 0.2f;
                serverLevel.sendParticles(new DustParticleOptions(ringColor, scale),
                        pos.x, pos.y, pos.z, 1, 0.0, 0.0, 0.0, 0.0);
            }
        }

        // ============ Layer 5: 爆炸大粒子（内力>10格时，玩家周围爆发） ============
        if (maxLength > 10.0f) {
            int explosionAlpha = 0x70;
            int explosionColor = (explosionAlpha << 24) | rgb;
            Vec3 playerPos = instance.position().add(0, instance.getBbHeight() * 0.5, 0);
            int explosionCount = 20 + random.nextInt(16);
            float explosionRadius = 2.5f + effectFactor * 2.0f;
            for (int i = 0; i < explosionCount; i++) {
                double theta = random.nextDouble() * 2.0 * Math.PI;
                double phi = Math.acos(2.0 * random.nextDouble() - 1.0);
                float r = explosionRadius * (0.5f + random.nextFloat() * 0.5f);
                double ex = r * Math.sin(phi) * Math.cos(theta);
                double ey = r * Math.sin(phi) * Math.sin(theta);
                double ez = r * Math.cos(phi);
                Vec3 pos = playerPos.add(ex, ey, ez);
                float scale = 0.8f + random.nextFloat() * 0.7f;
                serverLevel.sendParticles(new DustParticleOptions(explosionColor, scale),
                        pos.x, pos.y, pos.z, 1, 0.0, 0.0, 0.0, 0.0);
            }
        }
    }

    @Unique
    private int getMeleeAttackDamage() {
        Player instance = (Player) (Object) this;
        LivingEntityAccessor livingEntityAccessor = (LivingEntityAccessor) instance;
        PlayerEntityInvoker playerEntityInvoker = (PlayerEntityInvoker) instance;
        float baseDamage = instance.isAutoSpinAttack() ? livingEntityAccessor.getAutoSpinAttackDmg() : (float) instance.getAttributeValue(Attributes.ATTACK_DAMAGE);
        float attackStrengthScale = instance.getAttackStrengthScale(0.5F);
        float magicBoost = attackStrengthScale * (playerEntityInvoker.invokeGetEnchantedDamage(instance, baseDamage, instance.level().damageSources().generic()) - baseDamage);
        baseDamage *= 0.2F + attackStrengthScale * attackStrengthScale * 0.8F;
        baseDamage += instance.getWeaponItem().getItem().getAttackDamageBonus(null, baseDamage, instance.level().damageSources().generic());
        return Math.round(baseDamage + magicBoost);
    }

    @Unique
    private void updateAttributes() {
        Player instance = (Player) (Object) this;
        // 最大生命值、生命值
        int maxHealth = (int) Math.floor((wuxia$getInnateConstitution() * 5 +
                (wuxia$getMaxMana() * 0.1 + wuxia$getInnateConstitution() * wuxia$getAcquiredConstitution() + wuxia$getDeltaHealth())
        ) * (100 + wuxia$getHealthPercent()) / 100);
        wuxia$setMaxHealth(maxHealth);
        if (this.wuxiaHealth <= 0 && instance.getHealth() > 0.0F) {
            // 重生后恢复满气血
            this.wuxiaHealth = Math.max(maxHealth, 1);
        } else {
            this.wuxiaHealth = Mth.clamp(this.wuxiaHealth, 0, maxHealth);
        }

        // Δ属性
        wuxia$setDeltaAttack(getMeleeAttackDamage() + PlayerUtil.getDamageBonusFromSkill(this.skills, this.equippedSkills));
        wuxia$setDeltaDefense(PlayerUtil.getDefenseBonusFromSkill(this.skills, this.equippedSkills));
        wuxia$setDeltaAccuracy(PlayerUtil.getAccuracyBonusFromSkill(this.skills, this.equippedSkills));
        wuxia$setDeltaDodge(PlayerUtil.getDodgeBonusFromSkill(this.skills, this.equippedSkills));
        wuxia$setDeltaParry(PlayerUtil.getParryBonusFromSkill(this.skills, this.equippedSkills));

        // 属性%
        wuxia$setAttackPercent(PlayerUtil.getDamagePercentFromSkill(this.skills, this.equippedSkills));
        wuxia$setDefensePercent(PlayerUtil.getDefensePercentFromSkill(this.skills, this.equippedSkills));
        wuxia$setAccuracyPercent(PlayerUtil.getAccuracyPercentFromSkill(this.skills, this.equippedSkills));
        wuxia$setDodgePercent(PlayerUtil.getDodgePercentFromSkill(this.skills, this.equippedSkills));
        wuxia$setParryPercent(PlayerUtil.getParryPercentFromSkill(this.skills, this.equippedSkills));

        // 后天臂力、根骨、身法、悟性（来自技能加成）
        wuxia$setAcquiredStrength(PlayerUtil.getStrengthBonusFromSkill(this.skills, this.equippedSkills));
        wuxia$setAcquiredConstitution(PlayerUtil.getConstitutionBonusFromSkill(this.skills, this.equippedSkills));
        wuxia$setAcquiredAgility(PlayerUtil.getAgilityBonusFromSkill(this.skills, this.equippedSkills));
        wuxia$setAcquiredWisdom(PlayerUtil.getWisdomBonusFromSkill(this.skills, this.equippedSkills));

        // 血量、血量%、最大内力
        wuxia$setDeltaHealth(PlayerUtil.getHealthBonusFromSkill(this.skills, this.equippedSkills)
                + PlayerUtil.transferMana2Health(wuxia$getMaxMana(), this.equippedSkills));
        wuxia$setHealthPercent(PlayerUtil.getHealthPercentFromSkill(this.skills, this.equippedSkills));
        wuxia$setMaxMana(PlayerUtil.getMaxManaBonusFromSkill(this.skills, this.equippedSkills) * (1 + PlayerUtil.getMaxManaPercentFromSkill(this.skills, this.equippedSkills) / 100));

        // 更新攻击、防御、命中、躲闪、招架的值
        this.wuxia$setAttack((int) Math.floor((wuxia$getInnateStrength() +
                wuxia$getInnateStrength() * wuxia$getAcquiredStrength() * 0.1 +
                wuxia$getDeltaAttack()
        ) * (100 + wuxia$getAttackPercent()) / 100));
        this.wuxia$setDefense((int) Math.floor(((wuxia$getInnateStrength() + wuxia$getInnateConstitution()) * 0.1 +
                wuxia$getInnateConstitution() * wuxia$getAcquiredConstitution() * 0.1 +
                wuxia$getDeltaDefense()
        ) * (100 + wuxia$getDefensePercent()) / 100));
        this.wuxia$setAccuracy((int) Math.floor((wuxia$getInnateAgility() * 0.5 +
                wuxia$getDeltaAccuracy()
        ) * (100 + wuxia$getAccuracyPercent()) / 100));
        this.wuxia$setDodge((int) Math.floor((wuxia$getInnateAgility() * 0.5 +
                wuxia$getInnateAgility() * wuxia$getAcquiredAgility() * 0.1 +
                wuxia$getDeltaDodge()
        ) * (100 + wuxia$getDodgePercent()) / 100));
        this.wuxia$setParry((int) Math.floor((wuxia$getInnateStrength() * 0.5 +
                wuxia$getInnateStrength() * wuxia$getAcquiredStrength() * 0.1 +
                wuxia$getDeltaParry()
        ) * (100 + wuxia$getParryPercent()) / 100));
    }

    @Unique
    @Override
    public boolean wuxia$isAwakened() {
        return this.awakened;
    }

    @Unique
    @Override
    public void wuxia$setAwakened(boolean awakened) {
        boolean wasAwakened = this.awakened;
        this.awakened = awakened;
        if (wasAwakened && !awakened) {
            // 取消觉醒，恢复原版生命值系统
            this.wuxiaHealth = 0;
            ((Player) (Object) this).setHealth(((Player) (Object) this).getMaxHealth());
        }
    }

    @Unique
    @Override
    public int wuxia$getHealth() {
        return this.wuxiaHealth;
    }

    @Unique
    @Override
    public void wuxia$setHealth(int health) {
        this.wuxiaHealth = health;
    }

    @Unique
    @Override
    public int wuxia$getMaxHealth() {
        return this.wuxiaMaxHealth;
    }

    @Unique
    @Override
    public void wuxia$setMaxHealth(int maxHealth) {
        this.wuxiaMaxHealth = maxHealth;
    }

    @Override
    public int wuxia$getDeltaHealth() {
        return this.wuxiaDeltaHealth;
    }

    @Override
    public void wuxia$setDeltaHealth(int deltaHealth) {
        this.wuxiaDeltaHealth = deltaHealth;
    }

    @Override
    public int wuxia$getHealthPercent() {
        return this.wuxiaHealthPercent;
    }

    @Override
    public void wuxia$setHealthPercent(int healthPercent) {
        this.wuxiaHealthPercent = healthPercent;
    }

    @Unique
    @Override
    public int[] wuxia$getAllAttributes() {
        return this.wuxiaAttributes;
    }

    @Unique
    @Override
    public void wuxia$setAllAttributes(int[] attributes) {
        this.wuxiaAttributes = attributes;
    }

    @Unique
    @Override
    public int wuxia$getMana() {
        return this.wuxiaAttributes[0];
    }

    @Unique
    @Override
    public void wuxia$setMana(int mana) {
        this.wuxiaAttributes[0] = Math.min(mana, this.wuxiaAttributes[1]);
    }

    @Unique
    @Override
    public int wuxia$getMaxMana() {
        return this.wuxiaAttributes[1];
    }

    @Unique
    @Override
    public void wuxia$setMaxMana(int mana) {
        this.wuxiaAttributes[1] = mana;
    }

    @Unique
    @Override
    public int wuxia$getInnateStrength() {
        return this.wuxiaAttributes[2];
    }

    @Unique
    @Override
    public void wuxia$setInnateStrength(int innateStrength) {
        this.wuxiaAttributes[2] = innateStrength;
    }

    @Unique
    @Override
    public int wuxia$getAcquiredStrength() {
        return this.wuxiaAttributes[3];
    }

    @Unique
    @Override
    public void wuxia$setAcquiredStrength(int acquiredStrength) {
        this.wuxiaAttributes[3] = acquiredStrength;
    }

    @Unique
    @Override
    public int wuxia$getInnateConstitution() {
        return this.wuxiaAttributes[4];
    }

    @Unique
    @Override
    public void wuxia$setInnateConstitution(int innateConstitution) {
        this.wuxiaAttributes[4] = innateConstitution;
    }

    @Unique
    @Override
    public int wuxia$getAcquiredConstitution() {
        return this.wuxiaAttributes[5];
    }

    @Unique
    @Override
    public void wuxia$setAcquiredConstitution(int acquiredConstitution) {
        this.wuxiaAttributes[5] = acquiredConstitution;
    }

    @Unique
    @Override
    public int wuxia$getInnateAgility() {
        return this.wuxiaAttributes[6];
    }

    @Unique
    @Override
    public void wuxia$setInnateAgility(int innateAgility) {
        this.wuxiaAttributes[6] = innateAgility;
    }

    @Unique
    @Override
    public int wuxia$getAcquiredAgility() {
        return this.wuxiaAttributes[7];
    }

    @Unique
    @Override
    public void wuxia$setAcquiredAgility(int acquiredAgility) {
        this.wuxiaAttributes[7] = acquiredAgility;
    }

    @Unique
    @Override
    public int wuxia$getInnateWisdom() {
        return this.wuxiaAttributes[8];
    }

    @Unique
    @Override
    public void wuxia$setInnateWisdom(int innateWisdom) {
        this.wuxiaAttributes[8] = innateWisdom;
    }

    @Unique
    @Override
    public int wuxia$getAcquiredWisdom() {
        return this.wuxiaAttributes[9];
    }

    @Unique
    @Override
    public void wuxia$setAcquiredWisdom(int acquiredWisdom) {
        this.wuxiaAttributes[9] = acquiredWisdom;
    }

    @Unique
    @Override
    public int wuxia$getAttack() {
        return this.wuxiaAttributes[10];
    }

    @Unique
    @Override
    public void wuxia$setAttack(int attack) {
        this.wuxiaAttributes[10] = attack;
    }

    @Unique
    @Override
    public int wuxia$getDefense() {
        return this.wuxiaAttributes[11];
    }

    @Unique
    @Override
    public void wuxia$setDefense(int defense) {
        this.wuxiaAttributes[11] = defense;
    }

    @Unique
    @Override
    public int wuxia$getAccuracy() {
        return this.wuxiaAttributes[12];
    }

    @Unique
    @Override
    public void wuxia$setAccuracy(int accuracy) {
        this.wuxiaAttributes[12] = accuracy;
    }

    @Unique
    @Override
    public int wuxia$getDodge() {
        return wuxiaAttributes[13];
    }

    @Unique
    @Override
    public void wuxia$setDodge(int dodge) {
        wuxiaAttributes[13] = dodge;
    }

    @Unique
    @Override
    public int wuxia$getParry() {
        return wuxiaAttributes[14];
    }

    @Unique
    @Override
    public void wuxia$setParry(int parry) {
        wuxiaAttributes[14] = parry;
    }

    @Unique
    @Override
    public int wuxia$getDeltaAttack() {
        return wuxiaAttributes[15];
    }

    @Unique
    @Override
    public void wuxia$setDeltaAttack(int dAttack) {
        wuxiaAttributes[15] = dAttack;
    }

    @Unique
    @Override
    public int wuxia$getDeltaDefense() {
        return wuxiaAttributes[16];
    }

    @Unique
    @Override
    public void wuxia$setDeltaDefense(int dDefense) {
        wuxiaAttributes[16] = dDefense;
    }

    @Unique
    @Override
    public int wuxia$getDeltaAccuracy() {
        return wuxiaAttributes[17];
    }

    @Unique
    @Override
    public void wuxia$setDeltaAccuracy(int dAccuracy) {
        wuxiaAttributes[17] = dAccuracy;
    }

    @Unique
    @Override
    public int wuxia$getDeltaDodge() {
        return wuxiaAttributes[18];
    }

    @Unique
    @Override
    public void wuxia$setDeltaDodge(int dDodge) {
        wuxiaAttributes[18] = dDodge;
    }

    @Unique
    @Override
    public int wuxia$getDeltaParry() {
        return wuxiaAttributes[19];
    }

    @Unique
    @Override
    public void wuxia$setDeltaParry(int dParry) {
        wuxiaAttributes[19] = dParry;
    }

    @Unique
    @Override
    public int wuxia$getAttackPercent() {
        return wuxiaAttributes[20];
    }

    @Unique
    @Override
    public void wuxia$setAttackPercent(int attackPercent) {
        wuxiaAttributes[20] = attackPercent;
    }

    @Unique
    @Override
    public int wuxia$getDefensePercent() {
        return wuxiaAttributes[21];
    }

    @Unique
    @Override
    public void wuxia$setDefensePercent(int defensePercent) {
        wuxiaAttributes[21] = defensePercent;
    }

    @Unique
    @Override
    public int wuxia$getAccuracyPercent() {
        return wuxiaAttributes[22];
    }

    @Unique
    @Override
    public void wuxia$setAccuracyPercent(int accuracyPercent) {
        wuxiaAttributes[22] = accuracyPercent;
    }

    @Unique
    @Override
    public int wuxia$getDodgePercent() {
        return wuxiaAttributes[23];
    }

    @Unique
    @Override
    public void wuxia$setDodgePercent(int dodgePercent) {
        wuxiaAttributes[23] = dodgePercent;
    }

    @Unique
    @Override
    public int wuxia$getParryPercent() {
        return wuxiaAttributes[24];
    }

    @Unique
    @Override
    public void wuxia$setParryPercent(int parryPercent) {
        wuxiaAttributes[24] = parryPercent;
    }

    @Unique
    @Override
    public Map<Skill, Integer> wuxia$getAllSkills() {
        return this.skills;
    }

    @Override
    public void wuxia$setAllSkills(Map<Skill, Integer> skills) {
        if (skills == null) return;
        for (Skill skill : Skill.values()) {
            Integer value = skills.get(skill);
            if (value != null) {
                this.skills.put(skill, value);
            }
        }
    }

    @Unique
    @Override
    public void wuxia$setSkill(Skill skill, int value) {
        this.skills.replace(skill, value);
    }

    @Override
    public void wuxia$setAction(Action action) {
        this.wuxiaAction = action;
    }

    @Override
    public Action wuxia$getAction() {
        return this.wuxiaAction;
    }

    @Override
    public void wuxia$equipSkill(Skill.Type type, Skill skill) {
        this.equippedSkills[type.ordinal()] = skill;
    }

    @Override
    public Skill[] wuxia$getEquippedSkills() {
        return this.equippedSkills;
    }

    @Override
    public void wuxia$setEquippedSkills(Skill[] equippedSkills) {
        if (equippedSkills != null) {
            System.arraycopy(equippedSkills, 0, this.equippedSkills, 0, Math.min(equippedSkills.length, this.equippedSkills.length));
        }
    }

    @Unique
    @Override
    public int wuxia$getParticleColor() {
        return this.particleColor;
    }

    @Unique
    @Override
    public void wuxia$setParticleColor(int color) {
        this.particleColor = color;
    }

    @Unique
    @Override
    public void wuxia$setChargeReachBonus(double bonus) {
        this.chargeReachBonus = bonus;
    }

    @Unique
    @Override
    public double wuxia$getChargeReachBonus() {
        return this.chargeReachBonus;
    }

    // ========== 触距调整 ==========

    @Inject(method = "blockInteractionRange", at = @At("RETURN"), cancellable = true)
    private void modifyBlockInteractionRange(CallbackInfoReturnable<Double> cir) {
        cir.setReturnValue(Math.max(cir.getReturnValue(), Math.min(10.0, cir.getReturnValue() + this.chargeReachBonus)));
    }

    @Inject(method = "entityInteractionRange", at = @At("RETURN"), cancellable = true)
    private void modifyEntityInteractionRange(CallbackInfoReturnable<Double> cir) {
        cir.setReturnValue(Math.max(cir.getReturnValue(), Math.min(10.0, cir.getReturnValue() + this.chargeReachBonus)));
    }
}
