package com.aiden.wuxia.util;

import com.aiden.wuxia.enums.Rarity;
import com.aiden.wuxia.enums.Skill;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

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

    public static int getDamageBonusFromSkill(Map<Skill, Integer> skills, Skill[] equippedSkills) {
        int damageBonus = 0;
        Skill[] allSkills = Skill.values();
        for (Skill skill : allSkills) {
            int lv = skills.get(skill);
            if (skills.get(skill) <= 0) {
                continue;
            }
            if (skill.rarity == Rarity.COMMON) {
                damageBonus += skill.property.attackBonus.apply(lv);
                continue;
            }

            boolean equipped = false;
            for (Skill skill1 : equippedSkills) {
                if (skill == skill1) {
                    equipped = true;
                    break;
                }
            }

            if (equipped) damageBonus += skill.property.attackBonus.apply(lv);
        }

        return damageBonus;
    }

    public static int getDefenseBonusFromSkill(Map<Skill, Integer> skills, Skill[] equippedSkills) {
        int defenseBonus = 0;
        Skill[] allSkills = Skill.values();
        for (Skill skill : allSkills) {
            int lv = skills.get(skill);
            if (skills.get(skill) <= 0) {
                continue;
            }
            if (skill.rarity == Rarity.COMMON) {
                defenseBonus += skill.property.defenseBonus.apply(lv);
                continue;
            }

            boolean equipped = false;
            for (Skill skill1 : equippedSkills) {
                if (skill == skill1) {
                    equipped = true;
                    break;
                }
            }

            if (equipped) defenseBonus += skill.property.defenseBonus.apply(lv);
        }

        return defenseBonus;
    }

    public static int getAccuracyBonusFromSkill(Map<Skill, Integer> skills, Skill[] equippedSkills) {
        int accuracyBonus = 0;
        Skill[] allSkills = Skill.values();
        for (Skill skill : allSkills) {
            int lv = skills.get(skill);
            if (skills.get(skill) <= 0) {
                continue;
            }
            if (skill.rarity == Rarity.COMMON) {
                accuracyBonus += skill.property.accuracyBonus.apply(lv);
                continue;
            }

            boolean equipped = false;
            for (Skill skill1 : equippedSkills) {
                if (skill == skill1) {
                    equipped = true;
                    break;
                }
            }

            if (equipped) accuracyBonus += skill.property.accuracyBonus.apply(lv);
        }

        return accuracyBonus;
    }

    public static int getDodgeBonusFromSkill(Map<Skill, Integer> skills, Skill[] equippedSkills) {
        int dodgeBonus = 0;
        Skill[] allSkills = Skill.values();
        for (Skill skill : allSkills) {
            int lv = skills.get(skill);
            if (skills.get(skill) <= 0) {
                continue;
            }
            if (skill.rarity == Rarity.COMMON) {
                dodgeBonus += skill.property.dodgeBonus.apply(lv);
                continue;
            }

            boolean equipped = false;
            for (Skill skill1 : equippedSkills) {
                if (skill == skill1) {
                    equipped = true;
                    break;
                }
            }

            if (equipped) dodgeBonus += skill.property.dodgeBonus.apply(lv);
        }

        return dodgeBonus;
    }

    public static int getParryBonusFromSkill(Map<Skill, Integer> skills, Skill[] equippedSkills) {
        int parryBonus = 0;
        Skill[] allSkills = Skill.values();
        for (Skill skill : allSkills) {
            int lv = skills.get(skill);
            if (skills.get(skill) <= 0) {
                continue;
            }
            if (skill.rarity == Rarity.COMMON) {
                parryBonus += skill.property.parryBonus.apply(lv);
                continue;
            }

            boolean equipped = false;
            for (Skill skill1 : equippedSkills) {
                if (skill == skill1) {
                    equipped = true;
                    break;
                }
            }

            if (equipped) parryBonus += skill.property.parryBonus.apply(lv);
        }

        return parryBonus;
    }

    public static int getDamagePercentFromSkill(Map<Skill, Integer> skills, Skill[] equippedSkills) {
        int damagePercent = 0;
        Skill[] allSkills = Skill.values();
        for (Skill skill : allSkills) {
            int lv = skills.get(skill);
            if (skills.get(skill) <= 0) {
                continue;
            }
            if (skill.rarity == Rarity.COMMON) {
                damagePercent += skill.property.attackPercent.apply(lv);
                continue;
            }

            boolean equipped = false;
            for (Skill skill1 : equippedSkills) {
                if (skill == skill1) {
                    equipped = true;
                    break;
                }
            }

            if (equipped) damagePercent += skill.property.attackPercent.apply(lv);
        }

        return damagePercent;
    }

    public static int getDefensePercentFromSkill(Map<Skill, Integer> skills, Skill[] equippedSkills) {
        int defensePercent = 0;
        Skill[] allSkills = Skill.values();
        for (Skill skill : allSkills) {
            int lv = skills.get(skill);
            if (skills.get(skill) <= 0) {
                continue;
            }
            if (skill.rarity == Rarity.COMMON) {
                defensePercent += skill.property.defensePercent.apply(lv);
                continue;
            }

            boolean equipped = false;
            for (Skill skill1 : equippedSkills) {
                if (skill == skill1) {
                    equipped = true;
                    break;
                }
            }

            if (equipped) defensePercent += skill.property.defensePercent.apply(lv);
        }

        return defensePercent;
    }

    public static int getAccuracyPercentFromSkill(Map<Skill, Integer> skills, Skill[] equippedSkills) {
        int accuracyPercent = 0;
        Skill[] allSkills = Skill.values();
        for (Skill skill : allSkills) {
            int lv = skills.get(skill);
            if (skills.get(skill) <= 0) {
                continue;
            }
            if (skill.rarity == Rarity.COMMON) {
                accuracyPercent += skill.property.accuracyPercent.apply(lv);
                continue;
            }

            boolean equipped = false;
            for (Skill skill1 : equippedSkills) {
                if (skill == skill1) {
                    equipped = true;
                    break;
                }
            }

            if (equipped) accuracyPercent += skill.property.accuracyPercent.apply(lv);
        }

        return accuracyPercent;
    }

    public static int getDodgePercentFromSkill(Map<Skill, Integer> skills, Skill[] equippedSkills) {
        int dodgePercent = 0;
        Skill[] allSkills = Skill.values();
        for (Skill skill : allSkills) {
            int lv = skills.get(skill);
            if (skills.get(skill) <= 0) {
                continue;
            }
            if (skill.rarity == Rarity.COMMON) {
                dodgePercent += skill.property.dodgePercent.apply(lv);
                continue;
            }

            boolean equipped = false;
            for (Skill skill1 : equippedSkills) {
                if (skill == skill1) {
                    equipped = true;
                    break;
                }
            }

            if (equipped) dodgePercent += skill.property.dodgePercent.apply(lv);
        }

        return dodgePercent;
    }

    public static int getParryPercentFromSkill(Map<Skill, Integer> skills, Skill[] equippedSkills) {
        int parryPercent = 0;
        Skill[] allSkills = Skill.values();
        for (Skill skill : allSkills) {
            int lv = skills.get(skill);
            if (skills.get(skill) <= 0) {
                continue;
            }
            if (skill.rarity == Rarity.COMMON) {
                parryPercent += skill.property.parryPercent.apply(lv);
                continue;
            }

            boolean equipped = false;
            for (Skill skill1 : equippedSkills) {
                if (skill == skill1) {
                    equipped = true;
                    break;
                }
            }

            if (equipped) parryPercent += skill.property.parryPercent.apply(lv);
        }

        return parryPercent;
    }
}
