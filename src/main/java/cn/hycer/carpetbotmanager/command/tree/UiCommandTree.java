package cn.hycer.carpetbotmanager.command.tree;

import cn.hycer.carpetbotmanager.command.handler.ChatInterface;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;

import static cn.hycer.carpetbotmanager.command.CommandSuggestions.*;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

/**
 * /cbot ui [bots|groups|autoload|add] 交互菜单子命令树。
 */
public final class UiCommandTree {

    private UiCommandTree() {}

    public static LiteralArgumentBuilder<CommandSourceStack> addNodes(LiteralArgumentBuilder<CommandSourceStack> root) {
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
                .then(literal("add").executes(ChatInterface::showAddHelp)));
    }
}
