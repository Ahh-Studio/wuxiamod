package com.aiden.wuxia.enums;

public enum Action {
    NONE,
    MEDITATE,
    HEAL;

    public static Action safeValueOf(String name) {
        for (Action action : values()) {
            if (action.name().equals(name)) {
                return action;
            }
        }
        return NONE;
    }
}
