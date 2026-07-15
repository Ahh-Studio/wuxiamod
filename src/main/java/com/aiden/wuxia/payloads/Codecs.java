package com.aiden.wuxia.payloads;

import com.aiden.wuxia.skill.Skill;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.Map;

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

    StreamCodec<ByteBuf, Skill> SKILL_SINGLE = new StreamCodec<>() {
        @Override
        public @NonNull Skill decode(@NonNull ByteBuf input) {
            return Skill.safeValueOf(ByteBufCodecs.STRING_UTF8.decode(input));
        }

        @Override
        public void encode(@NonNull ByteBuf output, Skill value) {
            ByteBufCodecs.STRING_UTF8.encode(output, value.name());
        }
    };

    StreamCodec<ByteBuf, Map<Skill, Integer>> SKILL_MAP_STR = ByteBufCodecs.map(HashMap::new, SKILL_SINGLE, ByteBufCodecs.VAR_INT);

    StreamCodec<ByteBuf, Skill[]> SKILL_ARRAY = new StreamCodec<>() {
        @Override
        public Skill[] decode(ByteBuf input) {
            int length = ByteBufCodecs.readCount(input, 16);
            Skill[] array = new Skill[length];
            for (int i = 0; i < length; i++) {
                array[i] = Skill.safeValueOf(ByteBufCodecs.STRING_UTF8.decode(input));
            }
            return array;
        }

        @Override
        public void encode(ByteBuf output, Skill[] value) {
            ByteBufCodecs.writeCount(output, value.length, 16);
            for (Skill skill : value) {
                ByteBufCodecs.STRING_UTF8.encode(output, skill.name());
            }
        }
    };
}
