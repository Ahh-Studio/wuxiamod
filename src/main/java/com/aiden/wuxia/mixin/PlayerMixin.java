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
    public int[] wsAttributes = {
            0, 0, // 内力、内力上限
            25, 0, 25, 0, 15, 0, 15, 0, // 先天臂力、后天臂力、先天根骨、后天根骨、先天身法、后天身法、先天悟性、后天悟性
            0, 0, 0, 0, 0, // 攻击、防御、命中、躲闪、招架
            0, 0, 0, 0, 0 // Δ攻击、Δ防御、Δ命中、Δ躲闪、Δ招架
    };

    @Inject(method = "addAdditionalSaveData", at = @At(value = "TAIL"))
    public void addAdditionalSaveData(ValueOutput output, CallbackInfo ci) {
        ValueOutput skills = output.child("Skills");
        skills.putInt("jibenquanjiao", this.skills[0]); // 基本拳脚
        skills.putInt("jibenneigong", this.skills[1]); // 基本内功
        skills.putInt("jibenzhaojia", this.skills[2]); // 基本招架
        skills.putInt("jibenqinggong", this.skills[3]); // 基本轻功
        skills.putInt("jibenjianfa", this.skills[4]); // 基本剑法

        ValueOutput attributes = output.child("WSAttributes");
        attributes.putInt("mana", this.wsAttributes[0]); // 内力
        attributes.putInt("max_mana", this.wsAttributes[1]); // 内力上限
        attributes.putInt("innate_strength", this.wsAttributes[2]); // 臂力
        attributes.putInt("acquired_strength", this.wsAttributes[3]);
        attributes.putInt("innate_constitution", this.wsAttributes[4]); // 根骨
        attributes.putInt("acquired_constitution", this.wsAttributes[5]);
        attributes.putInt("innate_agility", this.wsAttributes[6]); // 身法
        attributes.putInt("acquired_agility", this.wsAttributes[7]);
        attributes.putInt("innate_wisdom", this.wsAttributes[8]); // 悟性
        attributes.putInt("acquired_wisdom", this.wsAttributes[9]);
        attributes.putInt("attack", this.wsAttributes[10]);
        attributes.putInt("defense", this.wsAttributes[11]);
        attributes.putInt("accuracy", this.wsAttributes[12]);
    }

    @Inject(method = "readAdditionalSaveData", at = @At(value = "TAIL"))
    public void readAdditionalSaveData(ValueInput input, CallbackInfo ci) {
        ValueInput skills = input.child("Skills").orElseThrow();
        this.skills[0] = skills.getIntOr("jibenquanjiao", 0);
        this.skills[1] = skills.getIntOr("jibenneigong", 0);
        this.skills[2] = skills.getIntOr("jibenzhaojia", 0);
        this.skills[3] = skills.getIntOr("jibenqinggong", 0);
        this.skills[4] = skills.getIntOr("jibenjianfa", 0);

        ValueInput attributes = input.child("WSAttributes").orElseThrow();
        this.wsAttributes[0] = attributes.getIntOr("mana", 0); // 内力
        this.wsAttributes[1] = attributes.getIntOr("max_mana", 0); // 内力上限
        this.wsAttributes[2] = attributes.getIntOr("innate_strength", 0); // 臂力
        this.wsAttributes[3] = attributes.getIntOr("acquired_strength", 0);
        this.wsAttributes[4] = attributes.getIntOr("innate_constitution", 0); // 根骨
        this.wsAttributes[5] = attributes.getIntOr("acquired_constitution", 0);
        this.wsAttributes[6] = attributes.getIntOr("innate_agility", 0); // 身法
        this.wsAttributes[7] = attributes.getIntOr("acquired_agility", 0);
        this.wsAttributes[8] = attributes.getIntOr("innate_wisdom", 0); // 悟性
        this.wsAttributes[9] = attributes.getIntOr("acquired_wisdom", 0);
        this.wsAttributes[10] = attributes.getIntOr("attack", 0);
        this.wsAttributes[11] = attributes.getIntOr("defense", 0);
        this.wsAttributes[12] = attributes.getIntOr("accuracy", 0);
    }

    @Inject(method = "tick", at = @At(value = "TAIL"))
    public void tick(CallbackInfo ci) {
        this.wsAttributes[10] = Math.round(this.wsAttributes[2] * this.wsAttributes[3] * 0.1F + this.wsAttributes[2] + getMeleeAttackDamage());
        this.wsAttributes[11] = Math.round((this.wsAttributes[2] + this.wsAttributes[4]) * 0.1F + this.wsAttributes[4] * this.wsAttributes[5] * 0.1F);
    }

    @Redirect(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurtOrSimulate(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    public boolean hurtOrSimulate(Entity instance, DamageSource source, float damage) {
        return instance.hurtOrSimulate(source, this.ws$getAttack());
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

    @Override
    public int[] ws$getAllAttributes() {
        return this.wsAttributes;
    }

    @Override
    public void ws$setAllAttributes(int[] attributes) {
        this.wsAttributes = attributes;
    }

    @Unique
    @Override
    public int ws$getMana() {
        return this.wsAttributes[0];
    }

    @Unique
    @Override
    public void ws$setMana(int mana) {
        this.wsAttributes[0] = mana;
    }

    @Unique
    @Override
    public int ws$getMaxMana() {
        return this.wsAttributes[1];
    }

    @Unique
    @Override
    public void ws$setMaxMana(int mana) {
        this.wsAttributes[1] = mana;
    }

    @Unique
    @Override
    public int ws$getInnateStrength() {
        return this.wsAttributes[2];
    }

    @Unique
    @Override
    public void ws$setInnateStrength(int innateStrength) {
        this.wsAttributes[2] = innateStrength;
    }

    @Unique
    @Override
    public int ws$getAcquiredStrength() {
        return this.wsAttributes[3];
    }

    @Unique
    @Override
    public void ws$setAcquiredStrength(int acquiredStrength) {
        this.wsAttributes[3] = acquiredStrength;
    }

    @Unique
    @Override
    public int ws$getInnateConstitution() {
        return this.wsAttributes[4];
    }

    @Unique
    @Override
    public void ws$setInnateConstitution(int innateConstitution) {
        this.wsAttributes[4] = innateConstitution;
    }

    @Unique
    @Override
    public int ws$getAcquiredConstitution() {
        return this.wsAttributes[5];
    }

    @Unique
    @Override
    public void ws$setAcquiredConstitution(int acquiredConstitution) {
        this.wsAttributes[5] = acquiredConstitution;
    }

    @Unique
    @Override
    public int ws$getInnateAgility() {
        return this.wsAttributes[6];
    }

    @Unique
    @Override
    public void ws$setInnateAgility(int innateAgility) {
        this.wsAttributes[6] = innateAgility;
    }

    @Unique
    @Override
    public int ws$getAcquiredAgility() {
        return this.wsAttributes[7];
    }

    @Unique
    @Override
    public void ws$setAcquiredAgility(int acquiredAgility) {
        this.wsAttributes[7] = acquiredAgility;
    }

    @Unique
    @Override
    public int ws$getInnateWisdom() {
        return this.wsAttributes[8];
    }

    @Unique
    @Override
    public void ws$setInnateWisdom(int innateWisdom) {
        this.wsAttributes[8] = innateWisdom;
    }

    @Unique
    @Override
    public int ws$getAcquiredWisdom() {
        return this.wsAttributes[9];
    }

    @Unique
    @Override
    public void ws$setAcquiredWisdom(int acquiredWisdom) {
        this.wsAttributes[9] = acquiredWisdom;
    }

    @Override
    public int ws$getAttack() {
        return this.wsAttributes[10];
    }

    @Override
    public void ws$setAttack(int attack) {
        this.wsAttributes[10] = attack;
    }

    @Override
    public int ws$getDefense() {
        return this.wsAttributes[11];
    }

    @Override
    public void ws$setDefense(int defense) {
        this.wsAttributes[11] = defense;
    }

    @Override
    public int ws$getAccuracy() {
        return this.wsAttributes[12];
    }

    @Override
    public void ws$setAccuracy(int accuracy) {
        this.wsAttributes[12] = accuracy;
    }
}
