package cn.hycer.carpetbotmanager.command;

import cn.hycer.carpetbotmanager.command.handler.ChatInterface;
import cn.hycer.carpetbotmanager.command.tree.*;
import cn.hycer.carpetbotmanager.config.CarpetBotConfig;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.literal;

public final class CarpetBotCommand {

    private CarpetBotCommand() {}

    public static void register() {
        CommandRegistrationCallback.EVENT.register(CarpetBotCommand::buildTree);
    }

    private static void buildTree(CommandDispatcher<ServerCommandSource> d,
                                   CommandRegistrationCallback.CommandRegistryAccess registryAccess,
                                   net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback.CommandEnvironment environment) {
        var root = literal("cbot")
                .requires(src -> src.hasPermissionLevel(CarpetBotConfig.getInstance().getPermissionLevel()))
                .executes(ChatInterface::showMainMenu);

        BotCommandTree.addNodes(root);
        BatchCommandTree.addNodes(root);
        UiCommandTree.addNodes(root);
        AutoLoadCommandTree.addNodes(root);
        GroupCommandTree.addNodes(root);

        d.register(root);
    }
}
