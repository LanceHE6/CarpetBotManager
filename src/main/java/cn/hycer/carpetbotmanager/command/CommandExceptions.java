package cn.hycer.carpetbotmanager.command;

import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.network.chat.Component;

public final class CommandExceptions {

    public static final SimpleCommandExceptionType NOT_BOT_PREFIX =
            new SimpleCommandExceptionType(Component.translatableWithFallback(
                    "carpetbotmanager.error.not_bot_prefix", "Player name must start with 'bot_' prefix."));
    public static final SimpleCommandExceptionType BOT_NOT_FOUND =
            new SimpleCommandExceptionType(Component.translatableWithFallback(
                    "carpetbotmanager.error.bot_not_found", "Bot preset not found."));
    public static final SimpleCommandExceptionType BOT_ALREADY_EXISTS =
            new SimpleCommandExceptionType(Component.translatableWithFallback(
                    "carpetbotmanager.error.bot_already_exists", "Bot preset already exists."));
    public static final SimpleCommandExceptionType GROUP_NOT_FOUND =
            new SimpleCommandExceptionType(Component.translatableWithFallback(
                    "carpetbotmanager.error.group_not_found", "Group not found."));
    public static final SimpleCommandExceptionType GROUP_ALREADY_EXISTS =
            new SimpleCommandExceptionType(Component.translatableWithFallback(
                    "carpetbotmanager.error.group_already_exists", "Group already exists."));
    public static final SimpleCommandExceptionType BOTS_NOT_FOUND_FOR_GROUP =
            new SimpleCommandExceptionType(Component.translatableWithFallback(
                    "carpetbotmanager.error.bots_not_found_for_group", "No valid bots for group."));
    public static final SimpleCommandExceptionType ALREADY_IN_AUTOLOAD =
            new SimpleCommandExceptionType(Component.translatableWithFallback(
                    "carpetbotmanager.error.already_in_autoload", "Already in auto-load list."));
    public static final SimpleCommandExceptionType NOT_IN_AUTOLOAD =
            new SimpleCommandExceptionType(Component.translatableWithFallback(
                    "carpetbotmanager.error.not_in_autoload", "Not in auto-load list."));
    public static final SimpleCommandExceptionType GROUP_ALREADY_IN_AUTOLOAD =
            new SimpleCommandExceptionType(Component.translatableWithFallback(
                    "carpetbotmanager.error.group_already_in_autoload", "Group already in auto-load list."));
    public static final SimpleCommandExceptionType GROUP_NOT_IN_AUTOLOAD =
            new SimpleCommandExceptionType(Component.translatableWithFallback(
                    "carpetbotmanager.error.group_not_in_autoload", "Group not in auto-load list."));

    private CommandExceptions() {}
}
