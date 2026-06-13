package com.aiden.wuxia.mixin;

import com.aiden.wuxia.enums.Action;
import com.aiden.wuxia.mixin_extension.AvatarRenderStateMixinExtension;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(AvatarRenderState.class)
public class AvatarRenderStateMixin implements AvatarRenderStateMixinExtension {
    @Unique
    public Action wuxiaAction = Action.NONE;

    @Override
    public void wuxia$setAction(Action action) {
        this.wuxiaAction = action;
    }

    @Override
    public Action wuxia$getAction() {
        return this.wuxiaAction;
    }
}
