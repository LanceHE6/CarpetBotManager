package cn.hycer.carpetbotmanager.command;

import cn.hycer.carpetbotmanager.command.handler.ChatInterface;
import cn.hycer.carpetbotmanager.command.tree.*;
import cn.hycer.carpetbotmanager.config.CarpetBotConfig;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.literal;

public final class CarpetBotCommand {

    private CarpetBotCommand() {}

    public static void register() {
        CommandRegistrationCallback.EVENT.register((d, ra, env) -> buildTree(d));
    }

    private static int requiredPermission() {
        return CarpetBotConfig.getInstance().getPermissionLevel();
    }

    private static void buildTree(CommandDispatcher<ServerCommandSource> d) {
        var root = literal("cbot")
                .requires(src -> src.hasPermissionLevel(requiredPermission()))
                .executes(ChatInterface::showMainMenu);

        BotCommandTree.addNodes(root);
        BatchCommandTree.addNodes(root);
        UiCommandTree.addNodes(root);
        AutoLoadCommandTree.addNodes(root);
        GroupCommandTree.addNodes(root);

        d.register(root);
    }
}
