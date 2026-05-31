package com.aiden.wuxia.payloads;

import com.aiden.wuxia.WSMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record WSAttributesS2CPayload(int[] wsAttributes) implements CustomPacketPayload {
    public static final Identifier ID = Identifier.fromNamespaceAndPath(WSMod.MOD_ID, "ws_attributes_s2c");
    public static final CustomPacketPayload.Type<WSAttributesS2CPayload> TYPE = new Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, WSAttributesS2CPayload> CODEC = StreamCodec.composite(Codecs.INT_ARRAY, WSAttributesS2CPayload::wsAttributes, WSAttributesS2CPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
