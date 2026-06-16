package cn.hycer.carpetbotmanager.command;

import cn.hycer.carpetbotmanager.data.BotDataManager;
import cn.hycer.carpetbotmanager.model.BotGroup;
import cn.hycer.carpetbotmanager.model.BotPreset;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

import static cn.hycer.carpetbotmanager.command.CommandExceptions.*;

/**
 * Handlers for group commands: add, remove, load.
 */
public final class GroupHandlers {

    private GroupHandlers() {}

    static int addGroup(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        String groupName = StringArgumentType.getString(context, "groupName");
        String description = StringArgumentType.getString(context, "description");
        String botsStr = StringArgumentType.getString(context, "bots");

        BotDataManager dataManager = BotDataManager.getInstance();

        if (dataManager.hasBotGroup(groupName)) {
            throw GROUP_ALREADY_EXISTS.create();
        }

        String[] botNames = botsStr.split("\\s+");
        List<String> validBots = new ArrayList<>();
        List<String> notFound = new ArrayList<>();

        for (String botName : botNames) {
            if (dataManager.hasBotPreset(botName)) {
                validBots.add(botName);
            } else {
                notFound.add(botName);
            }
        }

        if (validBots.isEmpty()) {
            throw BOTS_NOT_FOUND_FOR_GROUP.create();
        }

        BotGroup group = new BotGroup(groupName, description, validBots);
        dataManager.addBotGroup(group);

        source.sendFeedback(
                () -> Text.translatable("carpetbotmanager.command.group.add.success", groupName, validBots.size()),
                true);

        if (!notFound.isEmpty()) {
            source.sendFeedback(
                    () -> Text.translatable("carpetbotmanager.command.group.add.partial",
                            String.join(", ", notFound)),
                    true);
        }

        return 1;
    }

    static int removeGroup(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        String groupName = StringArgumentType.getString(context, "groupName");

        BotDataManager dataManager = BotDataManager.getInstance();

        if (!dataManager.hasBotGroup(groupName)) {
            throw GROUP_NOT_FOUND.create();
        }

        dataManager.removeBotGroup(groupName);

        source.sendFeedback(
                () -> Text.translatable("carpetbotmanager.command.group.remove.success", groupName),
                true);

        return 1;
    }

    static int loadGroup(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        String groupName = StringArgumentType.getString(context, "groupName");

        BotDataManager dataManager = BotDataManager.getInstance();
        BotGroup group = dataManager.getBotGroup(groupName)
                .orElseThrow(GROUP_NOT_FOUND::create);

        int loaded = 0;
        int failed = 0;

        for (String botName : group.getBots()) {
            BotPreset preset = dataManager.getBotPreset(botName).orElse(null);
            if (preset != null) {
                try {
                    BotSpawner.spawn(source, preset);
                    loaded++;
                } catch (Exception e) {
                    failed++;
                    source.sendFeedback(
                            () -> Text.translatable("carpetbotmanager.command.group.load.failed_item",
                                    botName, e.getMessage()),
                            true);
                }
            } else {
                failed++;
                source.sendFeedback(
                        () -> Text.translatable("carpetbotmanager.error.bot_not_found_item", botName),
                        true);
            }
        }

        final int finalLoaded = loaded;
        final int finalFailed = failed;
        source.sendFeedback(
                () -> Text.translatable("carpetbotmanager.command.group.load.success",
                        groupName, finalLoaded, finalFailed),
                true);

        return 1;
    }
}
