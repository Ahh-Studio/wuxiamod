package com.aiden.wuxia.payloads;

import com.aiden.wuxia.WuxiaMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record WuxiaAttributesS2CPayload(int[] wuxiaAttributes, int health, int maxHealth) implements CustomPacketPayload {
    public static final Identifier ID = Identifier.fromNamespaceAndPath(WuxiaMod.MOD_ID, "ws_attributes_s2c");
    public static final CustomPacketPayload.Type<WuxiaAttributesS2CPayload> TYPE = new Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, WuxiaAttributesS2CPayload> CODEC = StreamCodec.composite(
            Codecs.INT_ARRAY, WuxiaAttributesS2CPayload::wuxiaAttributes,
            ByteBufCodecs.INT, WuxiaAttributesS2CPayload::health,
            ByteBufCodecs.INT, WuxiaAttributesS2CPayload::maxHealth, WuxiaAttributesS2CPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
