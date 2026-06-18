package cn.hycer.carpetbotmanager.command;

import cn.hycer.carpetbotmanager.config.CarpetBotConfig;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.permission.PermissionCheck;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import static cn.hycer.carpetbotmanager.command.CommandSuggestions.*;
import static net.minecraft.server.command.CommandManager.argument;
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
        d.register(literal("cbot")
                .requires(CommandManager.requirePermissionLevel(requiredPermission()))
                .executes(ChatInterface::showMainMenu)

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
                .then(literal("list").executes(BotHandlers::listBots))

                .then(literal("ui")
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
                        .then(literal("add").executes(ChatInterface::showAddHelp)))

                .then(literal("autoload")
                        .then(literal("add")
                                .then(argument("name", StringArgumentType.word())
                                        .suggests(BOT_NAME_SUGGESTIONS)
                                        .executes(AutoLoadHandlers::addAutoLoadBot)))
                        .then(literal("remove")
                                .then(argument("name", StringArgumentType.word())
                                        .suggests(AUTOLOAD_BOT_SUGGESTIONS)
                                        .executes(AutoLoadHandlers::removeAutoLoadBot)))
                        .then(literal("list").executes(AutoLoadHandlers::listAutoLoad)))

                .then(literal("group")
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
                                                .executes(AutoLoadHandlers::removeAutoLoadGroup)))))
        );
    }
}
