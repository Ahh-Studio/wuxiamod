package com.aiden.wuxia.util;

import java.util.function.Function;

public class SkillProperty {
    public Function<Integer, Integer> strengthBonus = (_ -> 0), constitutionBonus = (_ -> 0), agilityBonus = (_ -> 0), wisdomBonus = (_ -> 0);
    public Function<Integer, Integer> attackBonus = (_ -> 0), defenseBonus = (_ -> 0), accuracyBonus = (_ -> 0), dodgeBonus = (_ -> 0), parryBonus = (_ -> 0);
    public Function<Integer, Integer> strengthPercent = (_ -> 0), constitutionPercent = (_ -> 0), agilityPercent = (_ -> 0), wisdomPercent = (_ -> 0);
    public Function<Integer, Integer> attackPercent = (_ -> 0), defensePercent = (_ -> 0), accuracyPercent = (_ -> 0), dodgePercent = (_ -> 0), parryPercent = (_ -> 0);
}
