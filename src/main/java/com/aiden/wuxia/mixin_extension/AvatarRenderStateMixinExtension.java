package com.aiden.wuxia.mixin_extension;

import com.aiden.wuxia.enums.Action;

public interface AvatarRenderStateMixinExtension {
    void wuxia$setAction(Action action);
    Action wuxia$getAction();
}
