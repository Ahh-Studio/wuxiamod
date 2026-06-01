package com.aiden.wuxia.command;

import com.aiden.wuxia.mixin_extension.PlayerMixinExtension;
import com.mojang.brigadier.arguments.BoolArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;

public class AwakenCommand {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, c, selection) -> {
            dispatcher.register(Commands.literal("awaken")
                    .requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
                    .then(Commands.argument("awakened", BoolArgumentType.bool())
                    .executes(context -> {
                        boolean awakened = BoolArgumentType.getBool(context, "awakened");
                        CommandSourceStack source = context.getSource();
                        if (source.isPlayer() && source.getPlayer() != null) {
                            ServerPlayer player = source.getPlayer();
                            PlayerMixinExtension playerMixinExtension = (PlayerMixinExtension) player;
                            playerMixinExtension.wuxia$setAwakened(awakened);
                            source.sendSuccess(() -> Component.literal(awakened ? "You've awakened! " : "Your abilities have been removed..."), false);
                            return 1;
                        }
                        return 0;
                    })));
        });
    }
}
