package com.aiden.wuxia.payloads;

import com.aiden.wuxia.WuxiaMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record WuxiaAttributesC2SPayload() implements CustomPacketPayload {
    public static final Identifier ID = Identifier.fromNamespaceAndPath(WuxiaMod.MOD_ID, "wuxia_attributes_c2s");
    public static final CustomPacketPayload.Type<WuxiaAttributesC2SPayload> TYPE = new Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, WuxiaAttributesC2SPayload> CODEC = StreamCodec.unit(new WuxiaAttributesC2SPayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
