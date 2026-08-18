package com.aiden.wuxia.payloads;

import com.aiden.wuxia.WuxiaMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/**
 * 客户端发送到服务器的数据包，通知服务器蓄力释放。
 * 包含蓄力时长（tick数），用于计算粒子强度。
 * @param chargeDuration 蓄力时长（tick数，20 ticks = 1秒）
 */
public record ChargeReleaseC2SPayload(int chargeDuration) implements CustomPacketPayload {
    public static final Identifier ID = Identifier.fromNamespaceAndPath(WuxiaMod.MOD_ID, "charge_release_c2s");
    public static final Type<ChargeReleaseC2SPayload> TYPE = new Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, ChargeReleaseC2SPayload> CODEC = StreamCodec.of(
            (buf, payload) -> buf.writeInt(payload.chargeDuration),
            buf -> new ChargeReleaseC2SPayload(buf.readInt())
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}