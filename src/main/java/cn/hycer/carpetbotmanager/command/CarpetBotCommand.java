package cn.hycer.carpetbotmanager.command;

import cn.hycer.carpetbotmanager.config.CarpetBotConfig;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.permissions.PermissionCheck;

import static cn.hycer.carpetbotmanager.command.CommandSuggestions.*;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

/**
 * Root command registration for {@code /cbot}.
 * Delegates execution to handler classes.
 */
public final class CarpetBotCommand {

    private CarpetBotCommand() {}

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                buildTree(dispatcher));
    }

    private static PermissionCheck requiredPermission() {
        int level = CarpetBotConfig.getInstance().getPermissionLevel();
        return switch (level) {
            case 0 -> Commands.LEVEL_ALL;
            case 1 -> Commands.LEVEL_MODERATORS;
            case 2 -> Commands.LEVEL_GAMEMASTERS;
            case 3 -> Commands.LEVEL_ADMINS;
            case 4 -> Commands.LEVEL_OWNERS;
            default -> Commands.LEVEL_GAMEMASTERS;
        };
    }

    private static void buildTree(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            literal("cbot")
                .requires(Commands.hasPermission(requiredPermission()))

                // --- bot ---
                .then(literal("add")
                    .then(argument("player", EntityArgument.player())
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

                .then(literal("help")
                    .executes(BotHandlers::showHelp))

                .then(literal("list")
                    .executes(BotHandlers::listBots))

                // --- autoload ---
                .then(literal("autoload")
                    .then(literal("add")
                        .then(argument("name", StringArgumentType.word())
                            .suggests(BOT_NAME_SUGGESTIONS)
                            .executes(AutoLoadHandlers::addAutoLoadBot)))
                    .then(literal("remove")
                        .then(argument("name", StringArgumentType.word())
                            .suggests(AUTOLOAD_BOT_SUGGESTIONS)
                            .executes(AutoLoadHandlers::removeAutoLoadBot)))
                    .then(literal("list")
                        .executes(AutoLoadHandlers::listAutoLoad)))

                // --- group ---
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
