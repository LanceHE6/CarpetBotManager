package cn.hycer.carpetbotmanager.command;

import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.network.chat.Component;

/**
 * Centralized {@link SimpleCommandExceptionType} constants used by all command handlers.
 */
public final class CommandExceptions {

    public static final SimpleCommandExceptionType NOT_BOT_PREFIX =
            new SimpleCommandExceptionType(Component.translatableWithFallback("carpetbotmanager.error.not_bot_prefix",
                    "Player name must start with the configured bot prefix to be registered as a bot."));
    public static final SimpleCommandExceptionType BOT_NOT_FOUND =
            new SimpleCommandExceptionType(Component.translatableWithFallback("carpetbotmanager.error.bot_not_found",
                    "Bot preset not found."));
    public static final SimpleCommandExceptionType BOT_ALREADY_EXISTS =
            new SimpleCommandExceptionType(Component.translatableWithFallback("carpetbotmanager.error.bot_already_exists",
                    "A bot preset with this name already exists."));
    public static final SimpleCommandExceptionType GROUP_NOT_FOUND =
            new SimpleCommandExceptionType(Component.translatableWithFallback("carpetbotmanager.error.group_not_found",
                    "Bot group not found."));
    public static final SimpleCommandExceptionType GROUP_ALREADY_EXISTS =
            new SimpleCommandExceptionType(Component.translatableWithFallback("carpetbotmanager.error.group_already_exists",
                    "A bot group with this name already exists."));
    public static final SimpleCommandExceptionType BOTS_NOT_FOUND_FOR_GROUP =
            new SimpleCommandExceptionType(Component.translatableWithFallback("carpetbotmanager.error.bots_not_found_for_group",
                    "None of the specified bots were found. Add bot presets first."));
    public static final SimpleCommandExceptionType ALREADY_IN_AUTOLOAD =
            new SimpleCommandExceptionType(Component.translatableWithFallback("carpetbotmanager.error.already_in_autoload",
                    "This bot is already in the auto-load list."));
    public static final SimpleCommandExceptionType NOT_IN_AUTOLOAD =
            new SimpleCommandExceptionType(Component.translatableWithFallback("carpetbotmanager.error.not_in_autoload",
                    "This bot is not in the auto-load list."));
    public static final SimpleCommandExceptionType GROUP_ALREADY_IN_AUTOLOAD =
            new SimpleCommandExceptionType(Component.translatableWithFallback("carpetbotmanager.error.group_already_in_autoload",
                    "This group is already in the auto-load list."));
    public static final SimpleCommandExceptionType GROUP_NOT_IN_AUTOLOAD =
            new SimpleCommandExceptionType(Component.translatableWithFallback("carpetbotmanager.error.group_not_in_autoload",
                    "This group is not in the auto-load list."));

    private CommandExceptions() {}
}
