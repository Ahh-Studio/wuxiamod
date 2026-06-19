package com.aiden.wuxia.payloads;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public interface Codecs {
    StreamCodec<ByteBuf, int[]> INT_ARRAY = new StreamCodec<>() {
        @Override
        public int[] decode(ByteBuf input) {
            int length = ByteBufCodecs.readCount(input, 1024);
            int[] array = new int[length];
            for (int i = 0; i < length; i++) {
                array[i] = input.readInt();
            }
            return array;
        }

        @Override
        public void encode(ByteBuf output, int[] value) {
            ByteBufCodecs.writeCount(output, value.length, 1024);
            for (int i : value) {
                output.writeInt(i);
            }
        }
    };
}
