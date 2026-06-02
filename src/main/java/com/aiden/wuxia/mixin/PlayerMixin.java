package com.aiden.wuxia.mixin;

import com.aiden.wuxia.mixin.accessor.LivingEntityAccessor;
import com.aiden.wuxia.mixin.invoker.PlayerEntityInvoker;
import com.aiden.wuxia.mixin_extension.PlayerMixinExtension;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class PlayerMixin implements PlayerMixinExtension {
    @Unique // 基本拳脚、基本内功、基本招架、基本轻功、基本剑法
    public int[] skills = {0, 0, 0, 0, 0};
    @Unique
    public int[] wuxiaAttributes = {
            0, 0, // 内力、内力上限 [0~1]
            25, 0, 25, 0, 15, 0, 15, 0, // 先天臂力、后天臂力、先天根骨、后天根骨、先天身法、后天身法、先天悟性、后天悟性 [2~9]
            0, 0, 0, 0, 0, // 攻击、防御、命中、躲闪、招架 [10~14]
            0, 0, 0, 0, 0, // Δ攻击、Δ防御、Δ命中、Δ躲闪、Δ招架 [15~19]
            0, 0, 0, 0, 0 // 攻击%、防御%、命中%、躲闪%、招架% [20~24]
    };
    @Unique
    public boolean awakened = false;

    @Inject(method = "addAdditionalSaveData", at = @At(value = "TAIL"))
    public void addAdditionalSaveData(ValueOutput output, CallbackInfo ci) {
        output.putBoolean("Awakened", this.awakened);
        ValueOutput skills = output.child("WuxiaSkills");
        skills.putInt("jibenquanjiao", this.skills[0]); // 基本拳脚
        skills.putInt("jibenneigong", this.skills[1]); // 基本内功
        skills.putInt("jibenzhaojia", this.skills[2]); // 基本招架
        skills.putInt("jibenqinggong", this.skills[3]); // 基本轻功
        skills.putInt("jibenjianfa", this.skills[4]); // 基本剑法

        ValueOutput attributes = output.child("WuxiaAttributes");
        attributes.putInt("mana", this.wuxiaAttributes[0]); // 内力
        attributes.putInt("max_mana", this.wuxiaAttributes[1]); // 内力上限
        attributes.putInt("innate_strength", this.wuxiaAttributes[2]); // 臂力
        attributes.putInt("acquired_strength", this.wuxiaAttributes[3]);
        attributes.putInt("innate_constitution", this.wuxiaAttributes[4]); // 根骨
        attributes.putInt("acquired_constitution", this.wuxiaAttributes[5]);
        attributes.putInt("innate_agility", this.wuxiaAttributes[6]); // 身法
        attributes.putInt("acquired_agility", this.wuxiaAttributes[7]);
        attributes.putInt("innate_wisdom", this.wuxiaAttributes[8]); // 悟性
        attributes.putInt("acquired_wisdom", this.wuxiaAttributes[9]);
        attributes.putInt("attack", this.wuxiaAttributes[10]);
        attributes.putInt("defense", this.wuxiaAttributes[11]);
        attributes.putInt("accuracy", this.wuxiaAttributes[12]);
        attributes.putInt("dodge", this.wuxiaAttributes[13]);
        attributes.putInt("parry", this.wuxiaAttributes[14]);
        attributes.putInt("delta_attack", this.wuxiaAttributes[15]);
        attributes.putInt("delta_defense", this.wuxiaAttributes[16]);
        attributes.putInt("delta_accuracy", this.wuxiaAttributes[17]);
        attributes.putInt("delta_dodge", this.wuxiaAttributes[18]);
        attributes.putInt("delta_parry", this.wuxiaAttributes[19]);
        attributes.putInt("attack_percent", this.wuxiaAttributes[20]);
        attributes.putInt("defense_percent", this.wuxiaAttributes[21]);
        attributes.putInt("accuracy_percent", this.wuxiaAttributes[22]);
        attributes.putInt("dodge_percent", this.wuxiaAttributes[23]);
        attributes.putInt("parry_percent", this.wuxiaAttributes[24]);
    }

    @Inject(method = "readAdditionalSaveData", at = @At(value = "TAIL"))
    public void readAdditionalSaveData(ValueInput input, CallbackInfo ci) {
        this.awakened = input.getBooleanOr("Awakened", false);
        ValueInput skills = input.child("WuxiaSkills").orElseThrow();
        this.skills[0] = skills.getIntOr("jibenquanjiao", 0);
        this.skills[1] = skills.getIntOr("jibenneigong", 0);
        this.skills[2] = skills.getIntOr("jibenzhaojia", 0);
        this.skills[3] = skills.getIntOr("jibenqinggong", 0);
        this.skills[4] = skills.getIntOr("jibenjianfa", 0);

        ValueInput attributes = input.child("WuxiaAttributes").orElseThrow();
        this.wuxiaAttributes[0] = attributes.getIntOr("mana", 0); // 内力
        this.wuxiaAttributes[1] = attributes.getIntOr("max_mana", 0); // 内力上限
        this.wuxiaAttributes[2] = attributes.getIntOr("innate_strength", 0); // 臂力
        this.wuxiaAttributes[3] = attributes.getIntOr("acquired_strength", 0);
        this.wuxiaAttributes[4] = attributes.getIntOr("innate_constitution", 0); // 根骨
        this.wuxiaAttributes[5] = attributes.getIntOr("acquired_constitution", 0);
        this.wuxiaAttributes[6] = attributes.getIntOr("innate_agility", 0); // 身法
        this.wuxiaAttributes[7] = attributes.getIntOr("acquired_agility", 0);
        this.wuxiaAttributes[8] = attributes.getIntOr("innate_wisdom", 0); // 悟性
        this.wuxiaAttributes[9] = attributes.getIntOr("acquired_wisdom", 0);
        this.wuxiaAttributes[10] = attributes.getIntOr("attack", 0);
        this.wuxiaAttributes[11] = attributes.getIntOr("defense", 0);
        this.wuxiaAttributes[12] = attributes.getIntOr("accuracy", 0);
        this.wuxiaAttributes[13] = attributes.getIntOr("dodge", 0);
        this.wuxiaAttributes[14] = attributes.getIntOr("parry", 0);
        this.wuxiaAttributes[15] = attributes.getIntOr("delta_attack", 0);
        this.wuxiaAttributes[16] = attributes.getIntOr("delta_defense", 0);
        this.wuxiaAttributes[17] = attributes.getIntOr("delta_accuracy", 0);
        this.wuxiaAttributes[18] = attributes.getIntOr("delta_dodge", 0);
        this.wuxiaAttributes[19] = attributes.getIntOr("delta_parry", 0);
        this.wuxiaAttributes[20] = attributes.getIntOr("attack_percent", 0);
        this.wuxiaAttributes[21] = attributes.getIntOr("defense_percent", 0);
        this.wuxiaAttributes[22] = attributes.getIntOr("accuracy_percent", 0);
        this.wuxiaAttributes[23] = attributes.getIntOr("dodge_percent", 0);
        this.wuxiaAttributes[24] = attributes.getIntOr("parry_percent", 0);
    }

    @Inject(method = "tick", at = @At(value = "TAIL"))
    public void tick(CallbackInfo ci) {
        if (this.awakened) {
            wuxia$setDeltaAttack(getMeleeAttackDamage());
            wuxia$setDeltaDefense(0);
            wuxia$setDeltaAccuracy(0);
            wuxia$setDeltaDodge(0);
            wuxia$setDeltaParry(0);
            // 更新 攻击防御命中躲闪招架
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
    }

    @Redirect(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurtOrSimulate(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    public boolean hurtOrSimulate(Entity instance, DamageSource source, float damage) {
        return instance.hurtOrSimulate(source, this.awakened ? this.wuxia$getAttack() : damage);
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
}
