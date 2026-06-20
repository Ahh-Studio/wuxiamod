package com.aiden.wuxia.enums;

public enum Rarity {
    COMMON(0xFFFFFFFF, 0), // white
    UNCOMMON(0xFF00FF00, 1), // green
    RARE(0xFF00FFFF, 2), // blue
    EPIC(0xFFFFFF00, 3), // yellow
    LEGENDARY(0xFF912CEE, 4), // purple
    SUPER(0xFFFFA500, 5); // orange

    public final int color;
    public final int sortNum;

    Rarity(int color, int sortNum) {
        this.color = color;
        this.sortNum = sortNum;
    }
}
