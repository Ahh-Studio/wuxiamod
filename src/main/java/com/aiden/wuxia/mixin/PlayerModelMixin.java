package com.aiden.wuxia.mixin;

import com.aiden.wuxia.mixin_extension.AvatarRenderStateMixinExtension;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerModel.class)
public abstract class PlayerModelMixin {
    @Inject(
            method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;)V",
            at = @At(value = "TAIL")
    )
    public void setupAnim(AvatarRenderState state, CallbackInfo ci) {
        PlayerModel instance = (PlayerModel) (Object) this;
        AvatarRenderStateMixinExtension stateExt = (AvatarRenderStateMixinExtension) state;
        switch (stateExt.wuxia$getAction()) {
            case HEAL -> {
                instance.head.y += 16.0F;
                instance.hat.y += 16.0F;
                instance.body.y += 16.0F;
                instance.rightArm.y += 16.0F;
                instance.leftArm.y += 16.0F;
                instance.rightLeg.y += 16.0F;
                instance.leftLeg.y += 16.0F;

                instance.rightLeg.xRot = (float) (-Math.PI / 2);
                instance.rightLeg.yRot = (float) (Math.PI / 10);
                instance.rightLeg.zRot = 0.07853982F;
                instance.leftLeg.xRot = (float) (-Math.PI / 2);
                instance.leftLeg.yRot = (float) (-Math.PI / 10);
                instance.leftLeg.zRot = -0.07853982F;
            }
            case MEDITATE -> {
                instance.head.y += 16.0F;
                instance.hat.y += 16.0F;
                instance.body.y += 16.0F;
                instance.rightArm.y += 16.0F;
                instance.leftArm.y += 16.0F;
                instance.rightLeg.y += 16.0F;
                instance.leftLeg.y += 16.0F;

                instance.rightArm.xRot += (float) (-Math.PI / 4);
                instance.leftArm.xRot += (float) (-Math.PI / 4);
                instance.rightLeg.xRot = (float) (-Math.PI / 2);
                instance.rightLeg.yRot = (float) (Math.PI / 10);
                instance.rightLeg.zRot = 0.07853982F;
                instance.leftLeg.xRot = (float) (-Math.PI / 2);
                instance.leftLeg.yRot = (float) (-Math.PI / 10);
                instance.leftLeg.zRot = -0.07853982F;
            }
            case null, default -> {

            }
        }
    }
}
