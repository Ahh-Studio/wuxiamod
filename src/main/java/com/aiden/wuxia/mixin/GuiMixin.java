package com.aiden.wuxia.mixin;

import com.aiden.wuxia.mixin_extension.PlayerMixinExtension;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Gui.class)
public class GuiMixin {

    /**
     * 觉醒后：用 wuxiaHealth 百分比 (0~20) 替代原版 getHealth()，使红心只显示 wuxia 血量比例
     */
    @Redirect(method = "extractPlayerHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getHealth()F"))
    private float redirectGetHealth(Player player) {
        PlayerMixinExtension ext = (PlayerMixinExtension) player;
        if (ext.wuxia$isAwakened() && ext.wuxia$getMaxHealth() > 0) {
            // 将 wuxiaHealth 映射到 0~20 范围（10个红心）
            return 20.0f * (float) ext.wuxia$getHealth() / (float) ext.wuxia$getMaxHealth();
        }
        return player.getHealth();
    }

    /**
     * 觉醒后：取消伤害吸收金心（absorption 设为 0）
     */
    @Redirect(method = "extractPlayerHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getAbsorptionAmount()F"))
    private float redirectGetAbsorption(Player player) {
        PlayerMixinExtension ext = (PlayerMixinExtension) player;
        if (ext.wuxia$isAwakened()) {
            return 0.0f;
        }
        return player.getAbsorptionAmount();
    }

    /**
     * 觉醒后：固定最大生命值为 20（10颗红心上限），不受原版生命值影响
     */
    @Redirect(method = "extractPlayerHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getAttributeValue(Lnet/minecraft/core/Holder;)D"))
    private double redirectGetAttributeValue(Player player, Holder<Attribute> holder) {
        PlayerMixinExtension ext = (PlayerMixinExtension) player;
        if (ext.wuxia$isAwakened() && holder == Attributes.MAX_HEALTH) {
            return 20.0;
        }
        return player.getAttributeValue(holder);
    }
}