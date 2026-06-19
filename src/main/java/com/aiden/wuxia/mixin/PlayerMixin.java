package com.aiden.wuxia.mixin;

import com.aiden.wuxia.enums.Action;
import com.aiden.wuxia.enums.Skill;
import com.aiden.wuxia.mixin.accessor.LivingEntityAccessor;
import com.aiden.wuxia.mixin.invoker.PlayerEntityInvoker;
import com.aiden.wuxia.mixin_extension.PlayerMixinExtension;
import com.aiden.wuxia.payloads.WuxiaAttributesS2CPayload;
import com.aiden.wuxia.util.PlayerUtil;
import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@Mixin(Player.class)
public class PlayerMixin implements PlayerMixinExtension {
    // 基本拳脚、基本内功、基本招架、基本轻功、基本剑法
    @Unique
    public Map<Skill, Integer> skills = new HashMap<>();
    @Unique
    public int[] wuxiaAttributes = {
            0, 0, // 内力、内力上限 [0~1]
            25, 0, 25, 0, 15, 0, 15, 0, // 先天臂力、后天臂力、先天根骨、后天根骨、先天身法、后天身法、先天悟性、后天悟性 [2~9]
            0, 0, 0, 0, 0, // 攻击、防御、命中、躲闪、招架 [10~14]
            0, 0, 0, 0, 0, // Δ攻击、Δ防御、Δ命中、Δ躲闪、Δ招架 [15~19]
            0, 0, 0, 0, 0 // 攻击%、防御%、命中%、躲闪%、招架% [20~24]
    };
    @Unique
    public Skill[] equippedSkills = {
            Skill.JIBENQUANJIAO, Skill.JIBENNEIGONG, Skill.JIBENZHAOJIA, Skill.JIBENQINGGONG, Skill.JIBENJIANFA
    };
    @Unique
    public boolean awakened = false;
    @Unique
    public int wuxiaHealth = 1;
    @Unique
    public int wuxiaMaxHealth = 0;
    @Unique
    public int wuxiaDeltaHealth = 0;
    @Unique
    public int wuxiaHealthPercent = 0;
    @Unique
    private int wuxiaAttributesPacketSendCounter = 19;
    @Unique
    public Action wuxiaAction = Action.NONE;

    @Inject(method = "<init>", at = @At(value = "TAIL"))
    public void injectedInit(Level level, GameProfile gameProfile, CallbackInfo ci) {
        this.skills.put(Skill.JIBENQUANJIAO, 0);
        this.skills.put(Skill.JIBENNEIGONG, 0);
        this.skills.put(Skill.JIBENZHAOJIA, 0);
        this.skills.put(Skill.JIBENQINGGONG, 0);
        this.skills.put(Skill.JIBENJIANFA, 0);
        this.skills.put(Skill.HUASHANJIANFA, 0);
        this.skills.put(Skill.HENGSHANJIANFA, 0);
        this.skills.put(Skill.SONGSHANJIANFA, 0);
    }

    @Inject(method = "addAdditionalSaveData", at = @At(value = "TAIL"))
    public void addAdditionalSaveData(ValueOutput output, CallbackInfo ci) {
        output.putBoolean("Awakened", this.awakened);
        output.putInt("wuxiaHealth", this.wuxiaHealth);
        output.putInt("wuxiaMaxHealth", this.wuxiaMaxHealth);
        output.putInt("wuxiaDeltaHealth", this.wuxiaDeltaHealth);
        output.putInt("wuxiaHealthPercent", this.wuxiaHealthPercent);
        output.putString("wuxiaAction", this.wuxiaAction.name().toLowerCase());

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

        PlayerUtil.readAdditionalSkillsData(input, this.skills);
        PlayerUtil.readAdditionalEquippedSkillsData(input, this.equippedSkills);
        PlayerUtil.readAdditionalAttributesData(input, this.wuxiaAttributes);
    }

    @Inject(method = "tick", at = @At(value = "TAIL"))
    public void tick(CallbackInfo ci) {
        Player instance = (Player) (Object) this;
        if (this.awakened) {
            if (this.wuxiaAttributesPacketSendCounter > 0) {
                this.wuxiaAttributesPacketSendCounter--;
            } else {
                this.wuxiaAttributesPacketSendCounter = 19;
                WuxiaAttributesS2CPayload wuxiaAttributesS2CPayload = new WuxiaAttributesS2CPayload(this.wuxiaAttributes, this.wuxiaHealth, this.wuxiaMaxHealth, this.wuxiaAction.name());
                ServerPlayer serverPlayer = (ServerPlayer) instance;
                ServerPlayNetworking.send(serverPlayer, wuxiaAttributesS2CPayload);
            }

            wuxia$setMaxHealth((int) Math.floor((wuxia$getInnateConstitution() * 5 +
                    (wuxia$getMaxMana() * 0.1 + wuxia$getInnateConstitution() * wuxia$getAcquiredConstitution() + wuxia$getDeltaHealth())
            ) * (100 + wuxia$getHealthPercent()) / 100));

            wuxia$setDeltaAttack(getMeleeAttackDamage() + getDamageBonusFromSkill());
            wuxia$setDeltaDefense(0);
            wuxia$setDeltaAccuracy(0);
            wuxia$setDeltaDodge(0);
            wuxia$setDeltaParry(
                    this.skills.get(Skill.JIBENZHAOJIA)
            );
            wuxia$setAcquiredStrength(
                    (int) Math.floor(this.skills.get(Skill.JIBENQUANJIAO) * 0.1)
            );
            wuxia$setAcquiredConstitution(
                    (int) Math.floor(this.skills.get(Skill.JIBENNEIGONG) * 0.1)
            );
            wuxia$setAcquiredAgility(
                    (int) Math.floor(this.skills.get(Skill.JIBENQINGGONG) * 0.1)
            );
            // 更新: 攻击防御命中躲闪招架
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

            if (this.wuxiaAction == Action.HEAL && this.wuxiaHealth < this.wuxiaMaxHealth) {
                int i = this.wuxiaMaxHealth / 40;
                this.wuxiaHealth = Math.min(this.wuxiaMaxHealth, this.wuxiaHealth + i);
            }

            if (this.wuxiaAction == Action.MEDITATE && this.wuxia$getMana() < this.wuxia$getMaxMana()) {
                int i = this.wuxia$getMana() / 20;
                this.wuxia$setMana(Math.min(this.wuxia$getMaxMana(), wuxia$getMana() + i));
            }
        }
    }

    @Redirect(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurtOrSimulate(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    public boolean hurtOrSimulate(Entity instance, DamageSource source, float damage) {
        return instance.hurtOrSimulate(source, this.awakened ? this.wuxia$getAttack() : damage);
    }

    @Inject(method = "travel", at = @At(value = "HEAD"), cancellable = true)
    public void travel(Vec3 input, CallbackInfo ci) {
        if (this.wuxiaAction != Action.NONE) {
            ci.cancel();
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
    private int getDamageBonusFromSkill() {
        int jibenjianfaDamageBonus = this.skills.get(Skill.JIBENJIANFA);
        Skill specialJianfa = this.equippedSkills[4];
        int specialJianfaDamageBonus = 0;
        switch (specialJianfa) {
            case HUASHANJIANFA -> specialJianfaDamageBonus = this.skills.get(Skill.HUASHANJIANFA) + 10;
            case HENGSHANJIANFA -> specialJianfaDamageBonus = this.skills.get(Skill.HENGSHANJIANFA) + 20;
            case SONGSHANJIANFA -> specialJianfaDamageBonus = this.skills.get(Skill.SONGSHANJIANFA) * 2 + 10;
        }
        return jibenjianfaDamageBonus + specialJianfaDamageBonus;
    }

    @Unique
    @Override
    public boolean wuxia$isAwakened() {
        return this.awakened;
    }

    @Unique
    @Override
    public void wuxia$setAwakened(boolean awakened) {
        this.awakened = awakened;
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
        this.wuxiaAttributes[0] = mana;
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
        switch (type) {
            case QUANJIAO: this.equippedSkills[0] = skill;
            case NEIGONG: this.equippedSkills[1] = skill;
            case ZHAOJIA: this.equippedSkills[2] = skill;
            case QINGGONG: this.equippedSkills[3] = skill;
            case JIANFA: this.equippedSkills[4] = skill;
        }
    }
}
