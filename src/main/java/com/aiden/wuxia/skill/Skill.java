package com.aiden.wuxia.skill;

import com.aiden.wuxia.enums.Rarity;
import com.aiden.wuxia.util.SkillProperty;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Skill {
    private String name;

    public final Type[] types;
    public String translationKey;
    public final Rarity rarity;
    public final SkillProperty[] properties;
    public final String desc;

    private Skill(Type[] types, Rarity rarity, String desc, Function<SkillProperty, SkillProperty>[] propertyFunctions) {
        this.types = types;
        this.rarity = rarity;
        this.desc = desc;
        List<SkillProperty> props = new ArrayList<>();
        for (Function<SkillProperty, SkillProperty> fn : propertyFunctions) {
            props.add(fn.apply(new SkillProperty()));
        }
        this.properties = props.toArray(new SkillProperty[0]);
    }

    void initName(String name) {
        this.name = name;
        this.translationKey = "skill." + name.toLowerCase();
    }

    public String name() {
        if (this.name == null) {
            throw new IllegalStateException("Skill name not initialized yet; ensure Skills class has finished static initialization");
        }
        return this.name;
    }

    public static Skill safeValueOf(String constantName) {
        Class<Skills> constCls = Skills.class;
        Field targetField;
        try {
            targetField = constCls.getDeclaredField(constantName);
        } catch (NoSuchFieldException e) {
            return null;
        }

        int mod = targetField.getModifiers();
        if (!Modifier.isPublic(mod) || !Modifier.isStatic(mod) || !Modifier.isFinal(mod)) return null;
        if (targetField.getType() != Skill.class) return null;

        try {
            return (Skill) targetField.get(null);
        } catch (IllegalAccessException e) {
            return null;
        }
    }

    public static Skill[] values() {
        Field[] allFields = Skills.class.getDeclaredFields();
        java.util.List<Skill> skillList = new java.util.ArrayList<>();

        for (Field field : allFields) {
            int mod = field.getModifiers();
            if (!Modifier.isPublic(mod) || !Modifier.isStatic(mod) || !Modifier.isFinal(mod)) {
                continue;
            }
            if (field.getType() != Skill.class) {
                continue;
            }
            try {
                Skill skill = (Skill) field.get(null);
                skillList.add(skill);
            } catch (IllegalAccessException ignored) {
            }
        }
        return skillList.toArray(new Skill[0]);
    }

    static Builder builder(Type... types) {
        return new Builder(types);
    }

    static class Builder {
        private final Type[] types;
        private Rarity rarity;
        private final List<Function<SkillProperty, SkillProperty>> propertyFunctions;
        private String desc;

        private Builder(Type... types) {
            this.rarity = Rarity.COMMON;
            this.propertyFunctions = new ArrayList<>();
            this.desc = "好的技能，不需要介绍。";
            this.types = types;
        }

        Builder rarity(Rarity rarity) {
            this.rarity = rarity;
            return this;
        }

        Builder desc(String desc) {
            this.desc = desc;
            return this;
        }

        Builder property(Function<SkillProperty, SkillProperty> propertyFunction) {
            this.propertyFunctions.add(propertyFunction);
            return this;
        }

        @SuppressWarnings("unchecked")
        Skill build() {
            return new Skill(types, rarity, desc, propertyFunctions.toArray(new Function[0]));
        }
    }

    public enum Type {
        QUANJIAO,
        NEIGONG,
        ZHAOJIA,
        QINGGONG,
        JIANFA;

        private Skill basicSkill;

        public Skill getBasicSkill() {
            if (basicSkill == null) {
                basicSkill = switch (this) {
                    case QUANJIAO -> Skills.JIBENQUANJIAO;
                    case NEIGONG -> Skills.JIBENNEIGONG;
                    case ZHAOJIA -> Skills.JIBENZHAOJIA;
                    case QINGGONG -> Skills.JIBENQINGGONG;
                    case JIANFA -> Skills.JIBENJIANFA;
                };
            }
            return basicSkill;
        }
    }
}
