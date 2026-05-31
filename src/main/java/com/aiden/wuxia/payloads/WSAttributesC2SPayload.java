package com.aiden.wuxia.payloads;

import com.aiden.wuxia.WSMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record WSAttributesC2SPayload() implements CustomPacketPayload {
    public static final Identifier ID = Identifier.fromNamespaceAndPath(WSMod.MOD_ID, "ws_attributes_c2s");
    public static final CustomPacketPayload.Type<WSAttributesC2SPayload> TYPE = new Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, WSAttributesC2SPayload> CODEC = StreamCodec.unit(new WSAttributesC2SPayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
