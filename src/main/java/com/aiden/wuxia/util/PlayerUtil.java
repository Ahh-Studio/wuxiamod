package com.aiden.wuxia.util;

import com.aiden.wuxia.enums.Skill;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.Map;

public class PlayerUtil {
    public static void addAdditionalSkillsData(ValueOutput output, Map<Skill, Integer> skills) {
        ValueOutput wuxiaSkills = output.child("WuxiaSkills");
        wuxiaSkills.putInt("jibenquanjiao", skills.get(Skill.JIBENQUANJIAO)); // 基本拳脚
        wuxiaSkills.putInt("jibenneigong", skills.get(Skill.JIBENNEIGONG)); // 基本内功
        wuxiaSkills.putInt("jibenzhaojia", skills.get(Skill.JIBENZHAOJIA)); // 基本招架
        wuxiaSkills.putInt("jibenqinggong", skills.get(Skill.JIBENQINGGONG)); // 基本轻功
        wuxiaSkills.putInt("jibenjianfa", skills.get(Skill.JIBENJIANFA)); // 基本剑法
        wuxiaSkills.putInt("huashanjianfa", skills.get(Skill.HUASHANJIANFA)); // 华山剑法
        wuxiaSkills.putInt("hengshanjianfa", skills.get(Skill.HENGSHANJIANFA)); // 恒山剑法
        wuxiaSkills.putInt("songshanjianfa", skills.get(Skill.SONGSHANJIANFA)); // 嵩山剑法
        wuxiaSkills.putInt("huashanquanfa", skills.get(Skill.HUASHANQUANFA)); // 华山拳法
        wuxiaSkills.putInt("pishipoyuquan", skills.get(Skill.PISHIPOYUQUAN)); // 劈石破玉拳
    }

    public static Map<Skill, Integer> readAdditionalSkillsData(ValueInput input, Map<Skill, Integer> wuxiaSkills) {
        ValueInput skills = input.child("WuxiaSkills").orElseThrow();
        wuxiaSkills.replace(Skill.JIBENQUANJIAO, skills.getIntOr("jibenquanjiao", 0));
        wuxiaSkills.replace(Skill.JIBENNEIGONG, skills.getIntOr("jibenneigong", 0));
        wuxiaSkills.replace(Skill.JIBENZHAOJIA, skills.getIntOr("jibenzhaojia", 0));
        wuxiaSkills.replace(Skill.JIBENQINGGONG, skills.getIntOr("jibenqinggong", 0));
        wuxiaSkills.replace(Skill.JIBENJIANFA, skills.getIntOr("jibenjianfa", 0));
        wuxiaSkills.replace(Skill.HUASHANJIANFA, skills.getIntOr("huashanjianfa", 0));
        wuxiaSkills.replace(Skill.HENGSHANJIANFA, skills.getIntOr("hengshanjianfa", 0));
        wuxiaSkills.replace(Skill.SONGSHANJIANFA, skills.getIntOr("songshanjianfa", 0));
        wuxiaSkills.replace(Skill.HUASHANQUANFA, skills.getIntOr("huashanquanfa", 0));
        wuxiaSkills.replace(Skill.PISHIPOYUQUAN, skills.getIntOr("pishipoyuquan", 0));
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
}
