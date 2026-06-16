package cn.hycer.carpetbotmanager.command;

import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.text.Text;

/**
 * Centralized {@link SimpleCommandExceptionType} constants used by all command handlers.
 */
public final class CommandExceptions {

    public static final SimpleCommandExceptionType NOT_BOT_PREFIX =
            new SimpleCommandExceptionType(Text.translatable("carpetbotmanager.error.not_bot_prefix"));
    public static final SimpleCommandExceptionType BOT_NOT_FOUND =
            new SimpleCommandExceptionType(Text.translatable("carpetbotmanager.error.bot_not_found"));
    public static final SimpleCommandExceptionType BOT_ALREADY_EXISTS =
            new SimpleCommandExceptionType(Text.translatable("carpetbotmanager.error.bot_already_exists"));
    public static final SimpleCommandExceptionType GROUP_NOT_FOUND =
            new SimpleCommandExceptionType(Text.translatable("carpetbotmanager.error.group_not_found"));
    public static final SimpleCommandExceptionType GROUP_ALREADY_EXISTS =
            new SimpleCommandExceptionType(Text.translatable("carpetbotmanager.error.group_already_exists"));
    public static final SimpleCommandExceptionType BOTS_NOT_FOUND_FOR_GROUP =
            new SimpleCommandExceptionType(Text.translatable("carpetbotmanager.error.bots_not_found_for_group"));
    public static final SimpleCommandExceptionType ALREADY_IN_AUTOLOAD =
            new SimpleCommandExceptionType(Text.translatable("carpetbotmanager.error.already_in_autoload"));
    public static final SimpleCommandExceptionType NOT_IN_AUTOLOAD =
            new SimpleCommandExceptionType(Text.translatable("carpetbotmanager.error.not_in_autoload"));
    public static final SimpleCommandExceptionType GROUP_ALREADY_IN_AUTOLOAD =
            new SimpleCommandExceptionType(Text.translatable("carpetbotmanager.error.group_already_in_autoload"));
    public static final SimpleCommandExceptionType GROUP_NOT_IN_AUTOLOAD =
            new SimpleCommandExceptionType(Text.translatable("carpetbotmanager.error.group_not_in_autoload"));

    private CommandExceptions() {}
}
