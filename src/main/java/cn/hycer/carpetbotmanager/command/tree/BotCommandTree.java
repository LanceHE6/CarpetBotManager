package cn.hycer.carpetbotmanager.command.tree;

import cn.hycer.carpetbotmanager.command.handler.BotHandlers;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;

import static cn.hycer.carpetbotmanager.command.CommandSuggestions.*;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

/**
 * /cbot add|remove|load|help|list 子命令树。
 */
public final class BotCommandTree {

    private BotCommandTree() {}

    public static LiteralArgumentBuilder<ServerCommandSource> addNodes(LiteralArgumentBuilder<ServerCommandSource> root) {
        return root
                .then(literal("add")
                        .then(argument("player", EntityArgumentType.player())
                                .executes(BotHandlers::addBot)
                                .then(argument("description", StringArgumentType.greedyString())
                                        .executes(BotHandlers::addBot))))
                .then(literal("remove")
                        .then(argument("name", StringArgumentType.word())
                                .suggests(BOT_NAME_SUGGESTIONS)
                                .executes(BotHandlers::removeBot)))
                .then(literal("load")
                        .then(argument("name", StringArgumentType.word())
                                .suggests(BOT_NAME_SUGGESTIONS)
                                .executes(BotHandlers::loadBot)))
                .then(literal("help").executes(BotHandlers::showHelp))
                .then(literal("list").executes(BotHandlers::listBots));
    }
}
