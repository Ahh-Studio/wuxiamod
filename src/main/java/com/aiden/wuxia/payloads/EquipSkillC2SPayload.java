package com.aiden.wuxia.payloads;

import com.aiden.wuxia.WuxiaMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record EquipSkillC2SPayload(String skillType, String skill) implements CustomPacketPayload {
    public static final Identifier ID = Identifier.fromNamespaceAndPath(WuxiaMod.MOD_ID, "equip_skill_c2s");
    public static final Type<EquipSkillC2SPayload> TYPE = new Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, EquipSkillC2SPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, EquipSkillC2SPayload::skillType,
            ByteBufCodecs.STRING_UTF8, EquipSkillC2SPayload::skill,
            EquipSkillC2SPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
