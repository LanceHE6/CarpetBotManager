package cn.hycer.carpetbotmanager.command;

import cn.hycer.carpetbotmanager.command.handler.ChatInterface;
import cn.hycer.carpetbotmanager.command.tree.*;
import cn.hycer.carpetbotmanager.config.CarpetBotConfig;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.permissions.PermissionCheck;

import static net.minecraft.commands.Commands.literal;

public final class CarpetBotCommand {

    private CarpetBotCommand() {}

    public static void register() {
        CommandRegistrationCallback.EVENT.register((d, ra, env) -> buildTree(d));
    }

    private static PermissionCheck requiredPermission() {
        return switch (CarpetBotConfig.getInstance().getPermissionLevel()) {
            case 0 -> Commands.LEVEL_ALL;
            case 1 -> Commands.LEVEL_MODERATORS;
            case 2 -> Commands.LEVEL_GAMEMASTERS;
            case 3 -> Commands.LEVEL_ADMINS;
            case 4 -> Commands.LEVEL_OWNERS;
            default -> Commands.LEVEL_GAMEMASTERS;
        };
    }

    private static void buildTree(CommandDispatcher<CommandSourceStack> d) {
        var root = literal("cbot")
                .requires(Commands.hasPermission(requiredPermission()))
                .executes(ChatInterface::showMainMenu);

        BotCommandTree.addNodes(root);
        BatchCommandTree.addNodes(root);
        UiCommandTree.addNodes(root);
        AutoLoadCommandTree.addNodes(root);
        GroupCommandTree.addNodes(root);

        d.register(root);
    }
}
