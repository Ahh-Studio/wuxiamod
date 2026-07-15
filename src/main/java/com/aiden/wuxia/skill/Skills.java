package com.aiden.wuxia.skill;

import com.aiden.wuxia.enums.Rarity;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class Skills {
    public static final Skill JIBENQUANJIAO = Skill.builder(Skill.Type.QUANJIAO)
            .rarity(Rarity.COMMON)
            .desc("武林中通用的拳脚功夫，凡练武之人都或多或少懂得一点。")
            .property(property -> {
                property.strengthBonus = lv -> (int) (lv * 0.1);
                return property;
            })
            .build();
    public static final Skill JIBENNEIGONG = Skill.builder(Skill.Type.NEIGONG)
            .rarity(Rarity.COMMON)
            .desc("武林中通用的内功心法，凡练武之人都或多或少懂得一点。")
            .property(property -> {
                property.constitutionBonus = lv -> (int) (lv * 0.1);
                return property;
            })
            .build();
    public static final Skill JIBENZHAOJIA = Skill.builder(Skill.Type.ZHAOJIA)
            .rarity(Rarity.COMMON)
            .desc("武林中通用的招架功法，凡练武之人都或多或少懂得一点。")
            .property(property -> {
                property.parryBonus = lv -> lv;
                return property;
            })
            .build();
    public static final Skill JIBENQINGGONG = Skill.builder(Skill.Type.QINGGONG)
            .rarity(Rarity.COMMON)
            .desc("武林中通用的轻功步法，凡练武之人都或多或少懂得一点。")
            .property(property -> {
                property.agilityBonus = lv -> (int) (lv * 0.1);
                return property;
            })
            .build();
    public static final Skill JIBENJIANFA = Skill.builder(Skill.Type.JIANFA)
            .rarity(Rarity.COMMON)
            .desc("武林中通用的剑法功夫，凡练武之人都或多或少懂得一点。")
            .property(property -> {
                property.accuracyBonus = lv -> lv;
                return property;
            })
            .build();

    public static final Skill HUASHANJIANFA = Skill.builder(Skill.Type.JIANFA)
            .rarity(Rarity.UNCOMMON)
            .desc("华山气宗正统基础剑术，剑招沉稳绵长，重内力运剑攻守均衡，为华山入门核心武学。")
            .property(property -> {
                property.attackBonus = lv -> lv + 10;
                return property;
            })
            .build();
    public static final Skill HENGSHANJIANFA = Skill.builder(Skill.Type.JIANFA)
            .rarity(Rarity.EPIC)
            .desc("恒山禅门专属剑招，圆转绵密以守为主，柔中暗藏杀招，不露锋芒，破绽稀少极难被破。")
            .property(property -> {
                property.attackBonus = lv -> lv + 20;
                property.accuracyBonus = lv -> (int) (lv * 1.5) + 200;
                return property;
            })
            .build();
    public static final Skill SONGSHANJIANFA = Skill.builder(Skill.Type.JIANFA)
            .rarity(Rarity.EPIC)
            .desc("嵩山十七路正统剑法，招式雄浑霸道，大开大合势如千军压境，强攻凌厉，尽显五岳盟主气魄。")
            .property(property -> {
                property.attackBonus = lv -> lv * 2 + 10;
                property.accuracyBonus = lv -> lv + 20;
                property.strengthBonus = lv -> (int) (lv * 0.125) + 2;
                return property;
            })
            .build();
    public static final Skill HUASHANQUANFA = Skill.builder(Skill.Type.QUANJIAO)
            .rarity(Rarity.UNCOMMON)
            .desc("华山气宗配套基础拳脚，贴合本门心法运劲，招式沉稳扎实，近身缠斗弥补剑招短板。")
            .property(property -> {
                property.attackBonus = lv -> (int) (lv * 1.1) + 20;
                return property;
            })
            .build();
    public static final Skill PISHIPOYUQUAN = Skill.builder(Skill.Type.QUANJIAO, Skill.Type.ZHAOJIA)
            .rarity(Rarity.UNCOMMON)
            .desc("华山气宗刚猛重拳，拳力厚重刚烈，原为劈石和破玉两式拳法，现合并为了一路。")
            .property(property -> {
                property.attackBonus = lv -> lv + 20;
                return property;
            })
            .property(property -> {
                property.parryBonus = lv -> lv + 20;
                return property;
            })
            .build();
    public static final Skill HUASHANXINFA = Skill.builder(Skill.Type.NEIGONG)
            .rarity(Rarity.UNCOMMON)
            .desc("华山道家正统内功，调和内息固本培元，是气宗武学根基，紫霞神功由此演化而来。")
            .property(property -> {
                property.healthBonus = lv -> lv;
                property.maxManaBonus = lv -> lv * 10;
                return property;
            })
            .build();
    public static final Skill FEIYANHUIXIANG = Skill.builder(Skill.Type.QINGGONG)
            .rarity(Rarity.UNCOMMON)
            .desc("华山气宗独门轻身功法，踏空旋身形如飞燕盘旋，身法迅捷，闪避腾挪。")
            .property(property -> {
                property.dodgeBonus = lv -> lv + 5;
                return property;
            })
            .build();

    static {
        for (Field field : Skills.class.getDeclaredFields()) {
            int mod = field.getModifiers();
            if (!Modifier.isPublic(mod) || !Modifier.isStatic(mod) || !Modifier.isFinal(mod)) {
                continue;
            }
            if (field.getType() != Skill.class) {
                continue;
            }
            try {
                Skill skill = (Skill) field.get(null);
                if (skill != null) {
                    skill.initName(field.getName());
                }
            } catch (IllegalAccessException ignored) {
            }
        }
    }
}
