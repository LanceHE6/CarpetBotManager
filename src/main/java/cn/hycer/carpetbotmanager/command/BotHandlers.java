package cn.hycer.carpetbotmanager.command;

import cn.hycer.carpetbotmanager.config.CarpetBotConfig;
import cn.hycer.carpetbotmanager.data.BotDataManager;
import cn.hycer.carpetbotmanager.model.BotGroup;
import cn.hycer.carpetbotmanager.model.BotPreset;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

import java.util.Collection;
import java.util.Locale;

import static cn.hycer.carpetbotmanager.command.CommandExceptions.*;

/**
 * Handlers for single-bot commands:
 * add, remove, load, list, help.
 */
public final class BotHandlers {

    private BotHandlers() {}

    static int showHelp(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();

        source.sendFeedback(
                () -> Text.translatableWithFallback("carpetbotmanager.command.help.header",
                        "=== /cbot Commands ==="), false);

        String[] lines = {
                "/cbot add <player> [description]",
                "/cbot remove <name>",
                "/cbot load <name>",
                "/cbot list",
                "/cbot help",
                "/cbot autoload add <name>",
                "/cbot autoload remove <name>",
                "/cbot autoload list",
                "/cbot group add <name> <description> <bot1 bot2 ...>",
                "/cbot group remove <name>",
                "/cbot group load <name>",
                "/cbot group autoload add <name>",
                "/cbot group autoload remove <name>",
        };

        for (String line : lines) {
            source.sendFeedback(() -> Text.literal("  " + line), false);
        }

        return 1;
    }

    static int addBot(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "player");
        String botName = player.getGameProfile().name();
        String prefix = CarpetBotConfig.getInstance().getBotNamePrefix();

        if (!botName.toLowerCase(Locale.ROOT).startsWith(prefix.toLowerCase(Locale.ROOT))) {
            throw NOT_BOT_PREFIX.create();
        }

        BotDataManager dataManager = BotDataManager.getInstance();
        if (dataManager.hasBotPreset(botName)) {
            throw BOT_ALREADY_EXISTS.create();
        }

        // Read optional description
        String description;
        try {
            description = StringArgumentType.getString(context, "description");
        } catch (IllegalArgumentException e) {
            description = "";
        }

        Vec3d lookVec = player.getRotationVector();
        BotPreset preset = new BotPreset(
                botName,
                description,
                player.getEntityWorld().getRegistryKey().getValue().toString(),
                player.getX(),
                player.getY(),
                player.getZ(),
                player.getYaw(),
                player.getPitch(),
                player.getX() + lookVec.x,
                player.getEyeY() + lookVec.y,
                player.getZ() + lookVec.z
        );

        dataManager.addBotPreset(preset);

        source.sendFeedback(
                () -> Text.translatableWithFallback("carpetbotmanager.command.add.success",
                        "Bot preset '%s' saved successfully.", botName),
                true);

        return 1;
    }

    static int removeBot(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        String name = StringArgumentType.getString(context, "name");

        BotDataManager dataManager = BotDataManager.getInstance();
        if (!dataManager.hasBotPreset(name)) {
            throw BOT_NOT_FOUND.create();
        }

        dataManager.removeBotPreset(name);

        source.sendFeedback(
                () -> Text.translatableWithFallback("carpetbotmanager.command.remove.success",
                        "Bot preset '%s' removed.", name),
                true);

        return 1;
    }

    static int loadBot(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        String name = StringArgumentType.getString(context, "name");

        BotPreset preset = BotDataManager.getInstance().getBotPreset(name)
                .orElseThrow(BOT_NOT_FOUND::create);

        BotSpawner.spawn(source, preset);

        source.sendFeedback(
                () -> Text.translatableWithFallback("carpetbotmanager.command.load.success",
                        "Bot '%s' spawned successfully.", name),
                true);

        return 1;
    }

    static int listBots(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        BotDataManager dataManager = BotDataManager.getInstance();

        Collection<BotPreset> bots = dataManager.getAllBotPresets();
        Collection<BotGroup> groups = dataManager.getAllBotGroups();

        source.sendFeedback(
                () -> Text.translatableWithFallback("carpetbotmanager.command.list.header",
                        "=== Carpet Bot Manager ==="), false);

        if (bots.isEmpty()) {
            source.sendFeedback(
                    () -> Text.translatableWithFallback("carpetbotmanager.command.list.no_bots",
                            "No saved bot presets."), false);
        } else {
            source.sendFeedback(
                    () -> Text.translatableWithFallback("carpetbotmanager.command.list.bots_title",
                            "Saved Bots:"), false);
            for (BotPreset bot : bots) {
                String desc = bot.getDescription() != null && !bot.getDescription().isEmpty()
                        ? " - " + bot.getDescription() : "";
                source.sendFeedback(
                        () -> Text.literal(String.format("  - %s%s (%.0f, %.0f, %.0f in %s)",
                                bot.getName(), desc, bot.getX(), bot.getY(), bot.getZ(),
                                bot.getDimension())), false);
            }
        }

        if (groups.isEmpty()) {
            source.sendFeedback(
                    () -> Text.translatableWithFallback("carpetbotmanager.command.list.no_groups",
                            "No saved bot groups."), false);
        } else {
            source.sendFeedback(
                    () -> Text.translatableWithFallback("carpetbotmanager.command.list.groups_title",
                            "Saved Groups:"), false);
            for (BotGroup group : groups) {
                String desc = group.getDescription() != null && !group.getDescription().isEmpty()
                        ? " - " + group.getDescription() : "";
                source.sendFeedback(
                        () -> Text.literal(String.format("  - %s%s [%s]",
                                group.getName(), desc, String.join(", ", group.getBots()))), false);
            }
        }

        return 1;
    }
}
