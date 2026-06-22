package cn.hycer.carpetbotmanager.command.tree;

import cn.hycer.carpetbotmanager.command.handler.AutoLoadHandlers;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;

import static cn.hycer.carpetbotmanager.command.CommandSuggestions.*;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

/**
 * /cbot autoload add|remove|list 子命令树。
 */
public final class AutoLoadCommandTree {

    private AutoLoadCommandTree() {}

    public static LiteralArgumentBuilder<CommandSourceStack> addNodes(LiteralArgumentBuilder<CommandSourceStack> root) {
        return root.then(literal("autoload")
                .then(literal("add")
                        .then(argument("name", StringArgumentType.word())
                                .suggests(BOT_NAME_SUGGESTIONS)
                                .executes(AutoLoadHandlers::addAutoLoadBot)))
                .then(literal("remove")
                        .then(argument("name", StringArgumentType.word())
                                .suggests(AUTOLOAD_BOT_SUGGESTIONS)
                                .executes(AutoLoadHandlers::removeAutoLoadBot)))
                .then(literal("list").executes(AutoLoadHandlers::listAutoLoad)));
    }
}
