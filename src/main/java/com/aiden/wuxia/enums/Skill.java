package com.aiden.wuxia.enums;

public enum Skill {
    JIBENQUANJIAO(Type.QUANJIAO),
    JIBENNEIGONG(Type.NEIGONG),
    JIBENZHAOJIA(Type.ZHAOJIA),
    JIBENQINGGONG(Type.QINGGONG),
    JIBENJIANFA(Type.JIANFA),
    HUASHANJIANFA(Type.JIANFA),
    HENGSHANJIANFA(Type.JIANFA),
    SONGSHANJIANFA(Type.JIANFA);

    public final Type type;

    Skill(Type type) {
        this.type = type;
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
