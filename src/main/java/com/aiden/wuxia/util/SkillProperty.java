package com.aiden.wuxia.util;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public final class SkillProperty {
    public Function<Integer, Integer> strengthBonus = (_ -> 0), constitutionBonus = (_ -> 0), agilityBonus = (_ -> 0), wisdomBonus = (_ -> 0);
    public Function<Integer, Integer> attackBonus = (_ -> 0), defenseBonus = (_ -> 0), accuracyBonus = (_ -> 0), dodgeBonus = (_ -> 0), parryBonus = (_ -> 0);
    public Function<Integer, Integer> strengthPercent = (_ -> 0), constitutionPercent = (_ -> 0), agilityPercent = (_ -> 0), wisdomPercent = (_ -> 0);
    public Function<Integer, Integer> attackPercent = (_ -> 0), defensePercent = (_ -> 0), accuracyPercent = (_ -> 0), dodgePercent = (_ -> 0), parryPercent = (_ -> 0);
    public Function<Integer, Integer> maxManaBonus = (_ -> 0), healthBonus = (_ -> 0);
    public Function<Integer, Integer> maxManaPercent = (_ -> 0), healthPercent = (_ -> 0);

    public Map<String, Function<Integer, Integer>> getIntFieldMap() {
        Map<String, Function<Integer, Integer>> map = new HashMap<>();
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            Class<?> type = field.getType();
            if (type == Function.class) {
                try {
                    field.setAccessible(true);
                    Object value = field.get(this);
                    if (value instanceof Function<?, ?> function) {
                        @SuppressWarnings("unchecked")
                        Function<Integer, Integer> func = (Function<Integer, Integer>) function;
                        map.put(field.getName(), func);
                    }
                } catch (IllegalAccessException _) {}
            }
        }
        return map;
    }
}
