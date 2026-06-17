package cn.hycer.carpetbotmanager.command;

import cn.hycer.carpetbotmanager.data.BotDataManager;
import cn.hycer.carpetbotmanager.model.BotGroup;
import cn.hycer.carpetbotmanager.model.BotPreset;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

import static cn.hycer.carpetbotmanager.command.CommandExceptions.*;

/**
 * Handlers for group commands: add, remove, load.
 */
public final class GroupHandlers {

    private GroupHandlers() {}

    static int addGroup(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
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

        source.sendSystemMessage(
                Component.translatableWithFallback("carpetbotmanager.command.group.add.success",
                        "Group '%s' created with %d bot(s).", groupName, validBots.size()));

        if (!notFound.isEmpty()) {
            source.sendSystemMessage(
                    Component.translatableWithFallback("carpetbotmanager.command.group.add.partial",
                            "Warning: Some bots were not found and skipped: %s",
                            String.join(", ", notFound)));
        }

        return 1;
    }

    static int removeGroup(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        String groupName = StringArgumentType.getString(context, "groupName");

        BotDataManager dataManager = BotDataManager.getInstance();

        if (!dataManager.hasBotGroup(groupName)) {
            throw GROUP_NOT_FOUND.create();
        }

        dataManager.removeBotGroup(groupName);

        source.sendSystemMessage(
                Component.translatableWithFallback("carpetbotmanager.command.group.remove.success",
                        "Group '%s' removed.", groupName));

        return 1;
    }

    static int loadGroup(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
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
                    source.sendSystemMessage(
                            Component.translatableWithFallback("carpetbotmanager.command.group.load.failed_item",
                                    "Failed to load bot '%s': %s", botName, e.getMessage()));
                }
            } else {
                failed++;
                source.sendSystemMessage(
                        Component.translatableWithFallback("carpetbotmanager.error.bot_not_found_item",
                                "Bot '%s' not found in saved presets.", botName));
            }
        }

        final int finalLoaded = loaded;
        final int finalFailed = failed;
        source.sendSystemMessage(
                Component.translatableWithFallback("carpetbotmanager.command.group.load.success",
                        "Group '%s' loaded: %d succeeded, %d failed.",
                        groupName, finalLoaded, finalFailed));

        return 1;
    }
}
