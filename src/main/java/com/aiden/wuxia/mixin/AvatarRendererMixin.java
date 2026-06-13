package com.aiden.wuxia.mixin;

import com.aiden.wuxia.mixin_extension.AvatarRenderStateMixinExtension;
import com.aiden.wuxia.mixin_extension.PlayerMixinExtension;
import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AvatarRenderer.class)
public class AvatarRendererMixin<AvatarlikeEntity extends Avatar & ClientAvatarEntity> {
    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;F)V", at = @At(value = "TAIL"))
    public void extractRenderState(AvatarlikeEntity entity, AvatarRenderState state, float partialTicks, CallbackInfo ci) {
        if (entity instanceof Player player) {
            PlayerMixinExtension playerExt = (PlayerMixinExtension) player;
            AvatarRenderStateMixinExtension avatarRenderStateExt = (AvatarRenderStateMixinExtension) state;
            avatarRenderStateExt.wuxia$setAction(playerExt.wuxia$getAction()); // wuxia: 更新动作
        }
    }
}
