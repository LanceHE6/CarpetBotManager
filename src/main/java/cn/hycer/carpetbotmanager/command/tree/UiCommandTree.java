package cn.hycer.carpetbotmanager.command.tree;

import cn.hycer.carpetbotmanager.command.handler.ChatInterface;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.ServerCommandSource;

import static cn.hycer.carpetbotmanager.command.CommandSuggestions.*;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

/**
 * /cbot ui [bots|groups|autoload|add|batch] 交互菜单子命令树。
 */
public final class UiCommandTree {

    private UiCommandTree() {}

    public static LiteralArgumentBuilder<ServerCommandSource> addNodes(LiteralArgumentBuilder<ServerCommandSource> root) {
        return root.then(literal("ui")
                .executes(ChatInterface::showMainMenu)
                .then(literal("bots").executes(ChatInterface::showBotList))
                .then(literal("groups").executes(ChatInterface::showGroupList))
                .then(literal("autoload")
                        .executes(ChatInterface::showAutoLoad)
                        .then(literal("add")
                                .then(argument("name", StringArgumentType.word())
                                        .suggests(BOT_NAME_SUGGESTIONS)
                                        .executes(ctx -> ChatInterface.showAutoLoadAddBot(
                                                ctx, StringArgumentType.getString(ctx, "name")))))
                        .then(literal("group").then(literal("add")
                                .then(argument("name", StringArgumentType.word())
                                        .suggests(GROUP_NAME_SUGGESTIONS)
                                        .executes(ctx -> ChatInterface.showAutoLoadAddGroup(
                                                ctx, StringArgumentType.getString(ctx, "name")))))))
                .then(literal("add").executes(ChatInterface::showAddHelp))
                .then(literal("batch").executes(ChatInterface::showBatchMenu)));
    }
}
