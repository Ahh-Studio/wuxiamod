package com.aiden.wuxia.util;

import com.aiden.wuxia.enums.Rarity;
import com.aiden.wuxia.skill.Skill;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.List;
import java.util.Map;

public class PlayerUtil {
    public static void addAdditionalSkillsData(ValueOutput output, Map<Skill, Integer> skills) {
        ValueOutput wuxiaSkills = output.child("WuxiaSkills");
        for (Skill skill : Skill.values()) {
            wuxiaSkills.putInt(skill.name().toLowerCase(), skills.get(skill));
        }
    }

    public static Map<Skill, Integer> readAdditionalSkillsData(ValueInput input, Map<Skill, Integer> wuxiaSkills) {
        ValueInput skills = input.child("WuxiaSkills").orElseThrow();
        for (Skill skill : Skill.values()) {
            wuxiaSkills.replace(skill, skills.getIntOr(skill.name().toLowerCase(), 0));
        }
        return wuxiaSkills;
    }

    public static void addAdditionalEquippedSkillsData(ValueOutput output, Skill[] equippedSkills) {
        ValueOutput wuxiaEquippedSkills = output.child("WuxiaEquippedSkills");
        wuxiaEquippedSkills.putString("quanjiao", equippedSkills[0].name().toLowerCase());
        wuxiaEquippedSkills.putString("neigong", equippedSkills[1].name().toLowerCase());
        wuxiaEquippedSkills.putString("zhaojia", equippedSkills[2].name().toLowerCase());
        wuxiaEquippedSkills.putString("qinggong", equippedSkills[3].name().toLowerCase());
        wuxiaEquippedSkills.putString("jianfa", equippedSkills[4].name().toLowerCase());
    }

    public static Skill[] readAdditionalEquippedSkillsData(ValueInput input, Skill[] wuxiaEquippedSkills) {
        ValueInput equippedSkills = input.child("WuxiaEquippedSkills").orElseThrow();
        wuxiaEquippedSkills[0] = Skill.safeValueOf(equippedSkills.getStringOr("quanjiao", "jibenquanjiao").toUpperCase());
        wuxiaEquippedSkills[1] = Skill.safeValueOf(equippedSkills.getStringOr("neigong", "jibenneigong").toUpperCase());
        wuxiaEquippedSkills[2] = Skill.safeValueOf(equippedSkills.getStringOr("zhaojia", "jibenzhaojia").toUpperCase());
        wuxiaEquippedSkills[3] = Skill.safeValueOf(equippedSkills.getStringOr("qinggong", "jibenqinggong").toUpperCase());
        wuxiaEquippedSkills[4] = Skill.safeValueOf(equippedSkills.getStringOr("jianfa", "jibenjianfa").toUpperCase());
        return wuxiaEquippedSkills;
    }

    public static void addAdditionalAttributesData(ValueOutput output, int[] wuxiaAttributes) {
        ValueOutput attributes = output.child("WuxiaAttributes");
        attributes.putInt("mana", wuxiaAttributes[0]); // 内力
        attributes.putInt("max_mana", wuxiaAttributes[1]); // 内力上限
        attributes.putInt("innate_strength", wuxiaAttributes[2]); // 臂力
        attributes.putInt("acquired_strength", wuxiaAttributes[3]);
        attributes.putInt("innate_constitution", wuxiaAttributes[4]); // 根骨
        attributes.putInt("acquired_constitution", wuxiaAttributes[5]);
        attributes.putInt("innate_agility", wuxiaAttributes[6]); // 身法
        attributes.putInt("acquired_agility", wuxiaAttributes[7]);
        attributes.putInt("innate_wisdom", wuxiaAttributes[8]); // 悟性
        attributes.putInt("acquired_wisdom", wuxiaAttributes[9]);
        attributes.putInt("attack", wuxiaAttributes[10]);
        attributes.putInt("defense", wuxiaAttributes[11]);
        attributes.putInt("accuracy", wuxiaAttributes[12]);
        attributes.putInt("dodge", wuxiaAttributes[13]);
        attributes.putInt("parry", wuxiaAttributes[14]);
        attributes.putInt("delta_attack", wuxiaAttributes[15]);
        attributes.putInt("delta_defense", wuxiaAttributes[16]);
        attributes.putInt("delta_accuracy", wuxiaAttributes[17]);
        attributes.putInt("delta_dodge", wuxiaAttributes[18]);
        attributes.putInt("delta_parry", wuxiaAttributes[19]);
        attributes.putInt("attack_percent", wuxiaAttributes[20]);
        attributes.putInt("defense_percent", wuxiaAttributes[21]);
        attributes.putInt("accuracy_percent", wuxiaAttributes[22]);
        attributes.putInt("dodge_percent", wuxiaAttributes[23]);
        attributes.putInt("parry_percent", wuxiaAttributes[24]);
    }

    public static int[] readAdditionalAttributesData(ValueInput input, int[] wuxiaAttributes) {
        ValueInput attributes = input.child("WuxiaAttributes").orElseThrow();
        wuxiaAttributes[0] = attributes.getIntOr("mana", 0); // 内力
        wuxiaAttributes[1] = attributes.getIntOr("max_mana", 0); // 内力上限
        wuxiaAttributes[2] = attributes.getIntOr("innate_strength", 0); // 臂力
        wuxiaAttributes[3] = attributes.getIntOr("acquired_strength", 0);
        wuxiaAttributes[4] = attributes.getIntOr("innate_constitution", 0); // 根骨
        wuxiaAttributes[5] = attributes.getIntOr("acquired_constitution", 0);
        wuxiaAttributes[6] = attributes.getIntOr("innate_agility", 0); // 身法
        wuxiaAttributes[7] = attributes.getIntOr("acquired_agility", 0);
        wuxiaAttributes[8] = attributes.getIntOr("innate_wisdom", 0); // 悟性
        wuxiaAttributes[9] = attributes.getIntOr("acquired_wisdom", 0);
        wuxiaAttributes[10] = attributes.getIntOr("attack", 0);
        wuxiaAttributes[11] = attributes.getIntOr("defense", 0);
        wuxiaAttributes[12] = attributes.getIntOr("accuracy", 0);
        wuxiaAttributes[13] = attributes.getIntOr("dodge", 0);
        wuxiaAttributes[14] = attributes.getIntOr("parry", 0);
        wuxiaAttributes[15] = attributes.getIntOr("delta_attack", 0);
        wuxiaAttributes[16] = attributes.getIntOr("delta_defense", 0);
        wuxiaAttributes[17] = attributes.getIntOr("delta_accuracy", 0);
        wuxiaAttributes[18] = attributes.getIntOr("delta_dodge", 0);
        wuxiaAttributes[19] = attributes.getIntOr("delta_parry", 0);
        wuxiaAttributes[20] = attributes.getIntOr("attack_percent", 0);
        wuxiaAttributes[21] = attributes.getIntOr("defense_percent", 0);
        wuxiaAttributes[22] = attributes.getIntOr("accuracy_percent", 0);
        wuxiaAttributes[23] = attributes.getIntOr("dodge_percent", 0);
        wuxiaAttributes[24] = attributes.getIntOr("parry_percent", 0);
        return wuxiaAttributes;
    }

    private static SkillProperty getPropertyForSlot(Skill skill, int slotIndex) {
        Skill.Type slotType = switch (slotIndex) {
            case 0 -> Skill.Type.QUANJIAO;
            case 1 -> Skill.Type.NEIGONG;
            case 2 -> Skill.Type.ZHAOJIA;
            case 3 -> Skill.Type.QINGGONG;
            case 4 -> Skill.Type.JIANFA;
            default -> skill.types[0];
        };
        for (int i = 0; i < skill.types.length; i++) {
            if (skill.types[i] == slotType) {
                return skill.properties[i];
            }
        }
        return null;
    }

    /**
     * 获取技能的有效属性列表<br>
     * COMMON（白色）技能直接生效，无需装备<br>
     * 其他稀有度技能需要装备后才生效
     */
    private static List<SkillProperty> getEffectiveProperties(Skill skill, Skill[] equippedSkills) {
        List<SkillProperty> props = new java.util.ArrayList<>();

        // COMMON 技能直接生效
        if (skill.rarity == Rarity.COMMON) {
            props.add(skill.properties[0]);
            return props;
        }

        // 其他稀有度技能需要装备后才生效
        for (int i = 0; i < equippedSkills.length; i++) {
            if (skill == equippedSkills[i]) {
                SkillProperty prop = getPropertyForSlot(skill, i);
                if (prop != null) {
                    props.add(prop);
                }
            }
        }
        return props;
    }

    public static int getDamageBonusFromSkill(Map<Skill, Integer> skills, Skill[] equippedSkills) {
        int damageBonus = 0;
        for (Skill skill : Skill.values()) {
            int lv = skills.get(skill);
            if (lv <= 0) continue;
            for (SkillProperty prop : getEffectiveProperties(skill, equippedSkills)) {
                damageBonus += prop.attackBonus.apply(lv);
            }
        }
        return damageBonus;
    }

    public static int getDefenseBonusFromSkill(Map<Skill, Integer> skills, Skill[] equippedSkills) {
        int defenseBonus = 0;
        for (Skill skill : Skill.values()) {
            int lv = skills.get(skill);
            if (lv <= 0) continue;
            for (SkillProperty prop : getEffectiveProperties(skill, equippedSkills)) {
                defenseBonus += prop.defenseBonus.apply(lv);
            }
        }
        return defenseBonus;
    }

    public static int getAccuracyBonusFromSkill(Map<Skill, Integer> skills, Skill[] equippedSkills) {
        int accuracyBonus = 0;
        for (Skill skill : Skill.values()) {
            int lv = skills.get(skill);
            if (lv <= 0) continue;
            for (SkillProperty prop : getEffectiveProperties(skill, equippedSkills)) {
                accuracyBonus += prop.accuracyBonus.apply(lv);
            }
        }
        return accuracyBonus;
    }

    public static int getDodgeBonusFromSkill(Map<Skill, Integer> skills, Skill[] equippedSkills) {
        int dodgeBonus = 0;
        for (Skill skill : Skill.values()) {
            int lv = skills.get(skill);
            if (lv <= 0) continue;
            for (SkillProperty prop : getEffectiveProperties(skill, equippedSkills)) {
                dodgeBonus += prop.dodgeBonus.apply(lv);
            }
        }
        return dodgeBonus;
    }

    public static int getParryBonusFromSkill(Map<Skill, Integer> skills, Skill[] equippedSkills) {
        int parryBonus = 0;
        for (Skill skill : Skill.values()) {
            int lv = skills.get(skill);
            if (lv <= 0) continue;
            for (SkillProperty prop : getEffectiveProperties(skill, equippedSkills)) {
                parryBonus += prop.parryBonus.apply(lv);
            }
        }
        return parryBonus;
    }

    public static int getDamagePercentFromSkill(Map<Skill, Integer> skills, Skill[] equippedSkills) {
        int damagePercent = 0;
        for (Skill skill : Skill.values()) {
            int lv = skills.get(skill);
            if (lv <= 0) continue;
            for (SkillProperty prop : getEffectiveProperties(skill, equippedSkills)) {
                damagePercent += prop.attackPercent.apply(lv);
            }
        }
        return damagePercent;
    }

    public static int getDefensePercentFromSkill(Map<Skill, Integer> skills, Skill[] equippedSkills) {
        int defensePercent = 0;
        for (Skill skill : Skill.values()) {
            int lv = skills.get(skill);
            if (lv <= 0) continue;
            for (SkillProperty prop : getEffectiveProperties(skill, equippedSkills)) {
                defensePercent += prop.defensePercent.apply(lv);
            }
        }
        return defensePercent;
    }

    public static int getAccuracyPercentFromSkill(Map<Skill, Integer> skills, Skill[] equippedSkills) {
        int accuracyPercent = 0;
        for (Skill skill : Skill.values()) {
            int lv = skills.get(skill);
            if (lv <= 0) continue;
            for (SkillProperty prop : getEffectiveProperties(skill, equippedSkills)) {
                accuracyPercent += prop.accuracyPercent.apply(lv);
            }
        }
        return accuracyPercent;
    }

    public static int getDodgePercentFromSkill(Map<Skill, Integer> skills, Skill[] equippedSkills) {
        int dodgePercent = 0;
        for (Skill skill : Skill.values()) {
            int lv = skills.get(skill);
            if (lv <= 0) continue;
            for (SkillProperty prop : getEffectiveProperties(skill, equippedSkills)) {
                dodgePercent += prop.dodgePercent.apply(lv);
            }
        }
        return dodgePercent;
    }

    public static int getParryPercentFromSkill(Map<Skill, Integer> skills, Skill[] equippedSkills) {
        int parryPercent = 0;
        for (Skill skill : Skill.values()) {
            int lv = skills.get(skill);
            if (lv <= 0) continue;
            for (SkillProperty prop : getEffectiveProperties(skill, equippedSkills)) {
                parryPercent += prop.parryPercent.apply(lv);
            }
        }
        return parryPercent;
    }

    public static int getStrengthBonusFromSkill(Map<Skill, Integer> skills, Skill[] equippedSkills) {
        int strengthBonus = 0;
        for (Skill skill : Skill.values()) {
            int lv = skills.get(skill);
            if (lv <= 0) continue;
            for (SkillProperty prop : getEffectiveProperties(skill, equippedSkills)) {
                strengthBonus += prop.strengthBonus.apply(lv);
            }
        }
        return strengthBonus;
    }

    public static int getConstitutionBonusFromSkill(Map<Skill, Integer> skills, Skill[] equippedSkills) {
        int constitutionBonus = 0;
        for (Skill skill : Skill.values()) {
            int lv = skills.get(skill);
            if (lv <= 0) continue;
            for (SkillProperty prop : getEffectiveProperties(skill, equippedSkills)) {
                constitutionBonus += prop.constitutionBonus.apply(lv);
            }
        }
        return constitutionBonus;
    }

    public static int getAgilityBonusFromSkill(Map<Skill, Integer> skills, Skill[] equippedSkills) {
        int agilityBonus = 0;
        for (Skill skill : Skill.values()) {
            int lv = skills.get(skill);
            if (lv <= 0) continue;
            for (SkillProperty prop : getEffectiveProperties(skill, equippedSkills)) {
                agilityBonus += prop.agilityBonus.apply(lv);
            }
        }
        return agilityBonus;
    }

    public static int getWisdomBonusFromSkill(Map<Skill, Integer> skills, Skill[] equippedSkills) {
        int wisdomBonus = 0;
        for (Skill skill : Skill.values()) {
            int lv = skills.get(skill);
            if (lv <= 0) continue;
            for (SkillProperty prop : getEffectiveProperties(skill, equippedSkills)) {
                wisdomBonus += prop.wisdomBonus.apply(lv);
            }
        }
        return wisdomBonus;
    }

    public static int getMaxManaBonusFromSkill(Map<Skill, Integer> skills, Skill[] equippedSkills) {
        int maxManaBonus = 0;
        for (Skill skill : Skill.values()) {
            int lv = skills.get(skill);
            if (lv <= 0) continue;
            for (SkillProperty prop : getEffectiveProperties(skill, equippedSkills)) {
                maxManaBonus += prop.maxManaBonus.apply(lv);
            }
        }
        return maxManaBonus;
    }

    public static int getMaxManaPercentFromSkill(Map<Skill, Integer> skills, Skill[] equippedSkills) {
        int maxManaPercent = 0;
        for (Skill skill : Skill.values()) {
            int lv = skills.get(skill);
            if (lv <= 0) continue;
            for (SkillProperty prop : getEffectiveProperties(skill, equippedSkills)) {
                maxManaPercent += prop.maxManaPercent.apply(lv);
            }
        }
        return maxManaPercent;
    }

    public static int getHealthBonusFromSkill(Map<Skill, Integer> skills, Skill[] equippedSkills) {
        int healthBonus = 0;
        for (Skill skill : Skill.values()) {
            int lv = skills.get(skill);
            if (lv <= 0) continue;
            for (SkillProperty prop : getEffectiveProperties(skill, equippedSkills)) {
                healthBonus += prop.healthBonus.apply(lv);
            }
        }
        return healthBonus;
    }

    public static int getHealthPercentFromSkill(Map<Skill, Integer> skills, Skill[] equippedSkills) {
        int healthPercent = 0;
        for (Skill skill : Skill.values()) {
            int lv = skills.get(skill);
            if (lv <= 0) continue;
            for (SkillProperty prop : getEffectiveProperties(skill, equippedSkills)) {
                healthPercent += prop.healthPercent.apply(lv);
            }
        }
        return healthPercent;
    }
    public static int transferMana2Health(int maxMana, Skill[] equippedSkills) {
        int healthBonus = 0;
        SkillProperty[] skillProperties = equippedSkills[1].properties;
        for (SkillProperty prop : skillProperties) {
            healthBonus += maxMana * prop.mana2HealthRate / 100;
        }
        return healthBonus;
    }
}
