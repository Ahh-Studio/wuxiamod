package com.aiden.wuxia.mixin.invoker;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Player.class)
public interface PlayerEntityInvoker {
    @Invoker(value = "getEnchantedDamage")
    float invokeGetEnchantedDamage(final Entity entity, final float dmg, final DamageSource damageSource);
}
