package cn.hycer.carpetbotmanager.command;

import cn.hycer.carpetbotmanager.config.CarpetBotConfig;
import cn.hycer.carpetbotmanager.data.BotDataManager;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static cn.hycer.carpetbotmanager.command.CommandExceptions.*;

/**
 * Handlers for auto-load management commands.
 */
public final class AutoLoadHandlers {

    private AutoLoadHandlers() {}

    // --- Bot auto-load ---

    static int addAutoLoadBot(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        String name = StringArgumentType.getString(context, "name");

        BotDataManager dataManager = BotDataManager.getInstance();
        if (!dataManager.hasBotPreset(name)) {
            throw BOT_NOT_FOUND.create();
        }

        CarpetBotConfig config = CarpetBotConfig.getInstance();
        if (config.getAutoLoadBots().contains(name)) {
            throw ALREADY_IN_AUTOLOAD.create();
        }

        config.getAutoLoadBots().add(name);
        config.save();

        source.sendFeedback(
                () -> Text.translatableWithFallback("carpetbotmanager.command.autoload.add.success",
                        "Bot '%s' added to auto-load list.", name),
                true);
        return 1;
    }

    static int removeAutoLoadBot(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        String name = StringArgumentType.getString(context, "name");

        CarpetBotConfig config = CarpetBotConfig.getInstance();
        if (!config.getAutoLoadBots().remove(name)) {
            throw NOT_IN_AUTOLOAD.create();
        }

        config.save();

        source.sendFeedback(
                () -> Text.translatableWithFallback("carpetbotmanager.command.autoload.remove.success",
                        "Bot '%s' removed from auto-load list.", name),
                true);
        return 1;
    }

    static int listAutoLoad(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        CarpetBotConfig config = CarpetBotConfig.getInstance();

        source.sendFeedback(
                () -> Text.translatableWithFallback("carpetbotmanager.command.autoload.list.header",
                        "=== Auto-load Settings ==="), false);

        java.util.List<String> bots = config.getAutoLoadBots();
        if (bots.isEmpty()) {
            source.sendFeedback(
                    () -> Text.translatableWithFallback("carpetbotmanager.command.autoload.list.no_bots",
                            "No bots in auto-load list."), false);
        } else {
            source.sendFeedback(
                    () -> Text.translatableWithFallback("carpetbotmanager.command.autoload.list.bots_title",
                            "Auto-load Bots:"), false);
            for (String bot : bots) {
                source.sendFeedback(() -> Text.literal("  - " + bot), false);
            }
        }

        java.util.List<String> groups = config.getAutoLoadGroups();
        if (groups.isEmpty()) {
            source.sendFeedback(
                    () -> Text.translatableWithFallback("carpetbotmanager.command.autoload.list.no_groups",
                            "No groups in auto-load list."), false);
        } else {
            source.sendFeedback(
                    () -> Text.translatableWithFallback("carpetbotmanager.command.autoload.list.groups_title",
                            "Auto-load Groups:"), false);
            for (String group : groups) {
                source.sendFeedback(() -> Text.literal("  - " + group), false);
            }
        }

        return 1;
    }

    // --- Group auto-load ---

    static int addAutoLoadGroup(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        String groupName = StringArgumentType.getString(context, "groupName");

        BotDataManager dataManager = BotDataManager.getInstance();
        if (!dataManager.hasBotGroup(groupName)) {
            throw GROUP_NOT_FOUND.create();
        }

        CarpetBotConfig config = CarpetBotConfig.getInstance();
        if (config.getAutoLoadGroups().contains(groupName)) {
            throw GROUP_ALREADY_IN_AUTOLOAD.create();
        }

        config.getAutoLoadGroups().add(groupName);
        config.save();

        source.sendFeedback(
                () -> Text.translatableWithFallback("carpetbotmanager.command.group.autoload.add.success",
                        "Group '%s' added to auto-load list.", groupName),
                true);
        return 1;
    }

    static int removeAutoLoadGroup(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        String groupName = StringArgumentType.getString(context, "groupName");

        CarpetBotConfig config = CarpetBotConfig.getInstance();
        if (!config.getAutoLoadGroups().remove(groupName)) {
            throw GROUP_NOT_IN_AUTOLOAD.create();
        }

        config.save();

        source.sendFeedback(
                () -> Text.translatableWithFallback("carpetbotmanager.command.group.autoload.remove.success",
                        "Group '%s' removed from auto-load list.", groupName),
                true);
        return 1;
    }
}
