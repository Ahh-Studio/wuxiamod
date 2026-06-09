package com.aiden.wuxia.skill;

public enum Skill {
    JIBENQUANJIAO,
    JIBENNEIGONG,
    JIBENZHAOJIA,
    JIBENQINGGONG,
    JIBENJIANFA;

    public static Skill safeValueOf(String name) {
        for (Skill skill : values()) {
            if (skill.name().equals(name)) {
                return skill;
            }
        }
        return null;
    }
}
