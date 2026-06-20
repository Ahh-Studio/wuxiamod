package com.aiden.wuxia.enums;

import com.aiden.wuxia.util.SkillProperty;

import java.util.function.Function;

public enum Skill {
    JIBENQUANJIAO(Type.QUANJIAO, Rarity.COMMON, property1 -> {
        property1.strengthBonus = lv -> (int) (lv * 0.1);
        return property1;
    }),
    JIBENNEIGONG(Type.NEIGONG, Rarity.COMMON, property1 -> {
        property1.constitutionBonus = lv -> (int) (lv * 0.1);
        return property1;
    }),
    JIBENZHAOJIA(Type.ZHAOJIA, Rarity.COMMON, property1 -> {
        property1.parryBonus = lv -> lv;
        return property1;
    }),
    JIBENQINGGONG(Type.QINGGONG, Rarity.COMMON, property1 -> {
        property1.agilityBonus = lv -> (int) (lv * 0.1);
        return property1;
    }),
    JIBENJIANFA(Type.JIANFA, Rarity.COMMON, property1 -> {
        property1.accuracyBonus = lv -> lv;
        return property1;
    }),

    HUASHANJIANFA(Type.JIANFA, Rarity.UNCOMMON, property1 -> {
        property1.attackBonus = lv -> lv + 10;
        return property1;
    }),
    HENGSHANJIANFA(Type.JIANFA, Rarity.EPIC, property1 -> {
        property1.attackBonus = lv -> lv + 20;
        property1.accuracyBonus = lv -> (int) (lv * 1.5) + 200;
        return property1;
    }),
    SONGSHANJIANFA(Type.JIANFA, Rarity.EPIC, property1 -> {
        property1.attackPercent = lv -> lv * 2 + 10;
        property1.accuracyBonus = lv -> lv + 20;
        property1.strengthBonus = lv -> (int) (lv * 0.125) + 2;
        return property1;
    }),

    HUASHANQUANFA(Type.QUANJIAO, Rarity.UNCOMMON, property1 -> {
        property1.attackBonus = lv -> (int) (lv * 1.1) + 20;
        return property1;
    }),
    PISHIPOYUQUAN(Type.QUANJIAO, Rarity.UNCOMMON, property1 -> {
        property1.attackBonus = lv -> lv + 20;
        return property1;
    })
    ;

    public final Type type;
    public final String translationKey;
    public final Rarity rarity;
    public final SkillProperty property;

    Skill(Type type, Rarity rarity, Function<SkillProperty, SkillProperty> propertyFunction) {
        this.type = type;
        this.translationKey = "skill." + this.name().toLowerCase();
        this.rarity = rarity;
        this.property = propertyFunction.apply(new SkillProperty());
    }

    public static Skill safeValueOf(String name) {
        for (Skill skill : values()) {
            if (skill.name().equals(name)) {
                return skill;
            }
        }
        return null;
    }

    public enum Type {
        QUANJIAO,
        NEIGONG,
        ZHAOJIA,
        QINGGONG,
        JIANFA
    }
}
