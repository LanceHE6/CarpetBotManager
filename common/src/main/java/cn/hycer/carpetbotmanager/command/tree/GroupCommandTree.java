package cn.hycer.carpetbotmanager.command.tree;

import cn.hycer.carpetbotmanager.command.handler.AutoLoadHandlers;
import cn.hycer.carpetbotmanager.command.handler.GroupHandlers;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;

import static cn.hycer.carpetbotmanager.command.CommandSuggestions.*;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

/**
 * /cbot group add|remove|load|autoload 子命令树。
 */
public final class GroupCommandTree {

    private GroupCommandTree() {}

    public static LiteralArgumentBuilder<CommandSourceStack> addNodes(LiteralArgumentBuilder<CommandSourceStack> root) {
        return root.then(literal("group")
                .then(literal("add")
                        .then(argument("groupName", StringArgumentType.word())
                                .then(argument("description", StringArgumentType.string())
                                        .then(argument("bots", StringArgumentType.greedyString())
                                                .suggests(BOT_NAME_SUGGESTIONS)
                                                .executes(GroupHandlers::addGroup)))))
                .then(literal("remove")
                        .then(argument("groupName", StringArgumentType.word())
                                .suggests(GROUP_NAME_SUGGESTIONS)
                                .executes(GroupHandlers::removeGroup)))
                .then(literal("load")
                        .then(argument("groupName", StringArgumentType.word())
                                .suggests(GROUP_NAME_SUGGESTIONS)
                                .executes(GroupHandlers::loadGroup)))
                .then(literal("autoload")
                        .then(literal("add")
                                .then(argument("groupName", StringArgumentType.word())
                                        .suggests(GROUP_NAME_SUGGESTIONS)
                                        .executes(AutoLoadHandlers::addAutoLoadGroup)))
                        .then(literal("remove")
                                .then(argument("groupName", StringArgumentType.word())
                                        .suggests(AUTOLOAD_GROUP_SUGGESTIONS)
                                        .executes(AutoLoadHandlers::removeAutoLoadGroup)))));
    }
}
