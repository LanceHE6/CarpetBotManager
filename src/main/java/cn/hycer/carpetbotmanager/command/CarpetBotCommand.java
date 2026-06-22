package cn.hycer.carpetbotmanager.command;

import cn.hycer.carpetbotmanager.command.handler.ChatInterface;
import cn.hycer.carpetbotmanager.command.tree.*;
import cn.hycer.carpetbotmanager.config.CarpetBotConfig;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.permission.PermissionCheck;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.literal;

public final class CarpetBotCommand {

    private CarpetBotCommand() {}

    public static void register() {
        CommandRegistrationCallback.EVENT.register((d, ra, env) -> buildTree(d));
    }

    private static PermissionCheck requiredPermission() {
        return switch (CarpetBotConfig.getInstance().getPermissionLevel()) {
            case 0 -> CommandManager.ALWAYS_PASS_CHECK;
            case 1 -> CommandManager.MODERATORS_CHECK;
            case 2 -> CommandManager.GAMEMASTERS_CHECK;
            case 3 -> CommandManager.ADMINS_CHECK;
            case 4 -> CommandManager.OWNERS_CHECK;
            default -> CommandManager.GAMEMASTERS_CHECK;
        };
    }

    private static void buildTree(CommandDispatcher<ServerCommandSource> d) {
        var root = literal("cbot")
                .requires(CommandManager.requirePermissionLevel(requiredPermission()))
                .executes(ChatInterface::showMainMenu);

        BotCommandTree.addNodes(root);
        BatchCommandTree.addNodes(root);
        UiCommandTree.addNodes(root);
        AutoLoadCommandTree.addNodes(root);
        GroupCommandTree.addNodes(root);

        d.register(root);
    }
}
