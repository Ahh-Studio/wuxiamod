package com.aiden.wuxia.payloads;

import com.aiden.wuxia.WuxiaMod;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/**
 * 客户端发送到服务器的数据包，通知服务器蓄力满3s时破坏瞄准的方块。
 * @param pos 方块位置
 * @param direction 瞄准的方向
 */
public record ChargeBlockBreakC2SPayload(BlockPos pos, Direction direction) implements CustomPacketPayload {
    public static final Identifier ID = Identifier.fromNamespaceAndPath(WuxiaMod.MOD_ID, "charge_block_break_c2s");
    public static final Type<ChargeBlockBreakC2SPayload> TYPE = new Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, ChargeBlockBreakC2SPayload> CODEC = StreamCodec.of(
            (buf, payload) -> {
                buf.writeBlockPos(payload.pos);
                buf.writeEnum(payload.direction);
            },
            buf -> new ChargeBlockBreakC2SPayload(buf.readBlockPos(), buf.readEnum(Direction.class))
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}