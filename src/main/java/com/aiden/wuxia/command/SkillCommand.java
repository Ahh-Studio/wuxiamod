package com.aiden.wuxia.command;

import com.aiden.wuxia.skill.Skill;
import com.aiden.wuxia.mixin_extension.PlayerMixinExtension;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.Commands;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.entity.player.Player;

public class SkillCommand {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, c, selection) -> {
            dispatcher.register(Commands.literal("skill")
                    .requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER) && source.isPlayer())
                    .then(Commands.argument("skill", StringArgumentType.string()).then(Commands.argument("lvl", IntegerArgumentType.integer())
                            .executes(context -> {
                                String string = StringArgumentType.getString(context, "skill");
                                int lvl = IntegerArgumentType.getInteger(context, "lvl");
                                Skill skill = Skill.safeValueOf(string);
                                if (skill != null) {
                                    Player player = context.getSource().getPlayer();
                                    PlayerMixinExtension playerMixinExtension = (PlayerMixinExtension) player;
                                    playerMixinExtension.wuxia$setSkill(skill, lvl);
                                    return 1;
                                }
                                return 0;
                            })
                    ))
                    .then(Commands.literal("equip").then(Commands.argument("type", StringArgumentType.string()).then(Commands.argument("skill", StringArgumentType.string())
                            .executes(context -> {
                                String type = StringArgumentType.getString(context, "type");
                                Skill skill = Skill.safeValueOf(StringArgumentType.getString(context, "skill"));
                                if (skill != null && (type.equals("QUANJIAO") ||
                                        type.equals("NEIGONG") ||
                                        type.equals("ZHAOJIA") ||
                                        type.equals("QINGGONG") ||
                                        type.equals("JIANFA")
                                )) {
                                    Player player = context.getSource().getPlayer();
                                    PlayerMixinExtension playerMixinExtension = (PlayerMixinExtension) player;
                                    playerMixinExtension.wuxia$equipSkill(Skill.Type.valueOf(type), skill);
                                    return 1;
                                }
                                return 0;
                            })
                    ))));
        });
    }
}
