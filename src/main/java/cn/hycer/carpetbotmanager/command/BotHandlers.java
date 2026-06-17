package cn.hycer.carpetbotmanager.command;

import cn.hycer.carpetbotmanager.config.CarpetBotConfig;
import cn.hycer.carpetbotmanager.data.BotDataManager;
import cn.hycer.carpetbotmanager.model.BotGroup;
import cn.hycer.carpetbotmanager.model.BotPreset;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

import java.util.Collection;
import java.util.Locale;

import static cn.hycer.carpetbotmanager.command.CommandExceptions.*;

/**
 * Handlers for single-bot commands:
 * add, remove, load, list, help.
 */
public final class BotHandlers {

    private BotHandlers() {}

    static int showHelp(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();

        source.sendSystemMessage(
                Component.translatableWithFallback("carpetbotmanager.command.help.header",
                        "=== /cbot Commands ==="));

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
            source.sendSystemMessage(Component.literal("  " + line));
        }

        return 1;
    }

    static int addBot(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        ServerPlayer player = EntityArgument.getPlayer(context, "player");
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

        Vec3 lookVec = player.getLookAngle();
        BotPreset preset = new BotPreset(
                botName,
                description,
                player.level().dimension().identifier().toString(),
                player.getX(),
                player.getY(),
                player.getZ(),
                player.getYRot(),
                player.getXRot(),
                player.getX() + lookVec.x,
                player.getEyeY() + lookVec.y,
                player.getZ() + lookVec.z
        );

        dataManager.addBotPreset(preset);

        source.sendSystemMessage(
                Component.translatableWithFallback("carpetbotmanager.command.add.success",
                        "Bot preset '%s' saved successfully.", botName));

        return 1;
    }

    static int removeBot(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        String name = StringArgumentType.getString(context, "name");

        BotDataManager dataManager = BotDataManager.getInstance();
        if (!dataManager.hasBotPreset(name)) {
            throw BOT_NOT_FOUND.create();
        }

        dataManager.removeBotPreset(name);

        source.sendSystemMessage(
                Component.translatableWithFallback("carpetbotmanager.command.remove.success",
                        "Bot preset '%s' removed.", name));

        return 1;
    }

    static int loadBot(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        String name = StringArgumentType.getString(context, "name");

        BotPreset preset = BotDataManager.getInstance().getBotPreset(name)
                .orElseThrow(BOT_NOT_FOUND::create);

        BotSpawner.spawn(source, preset);

        source.sendSystemMessage(
                Component.translatableWithFallback("carpetbotmanager.command.load.success",
                        "Bot '%s' spawned successfully.", name));

        return 1;
    }

    static int listBots(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        BotDataManager dataManager = BotDataManager.getInstance();

        Collection<BotPreset> bots = dataManager.getAllBotPresets();
        Collection<BotGroup> groups = dataManager.getAllBotGroups();

        source.sendSystemMessage(
                Component.translatableWithFallback("carpetbotmanager.command.list.header",
                        "=== Carpet Bot Manager ==="));

        if (bots.isEmpty()) {
            source.sendSystemMessage(
                    Component.translatableWithFallback("carpetbotmanager.command.list.no_bots",
                            "No saved bot presets."));
        } else {
            source.sendSystemMessage(
                    Component.translatableWithFallback("carpetbotmanager.command.list.bots_title",
                            "Saved Bots:"));
            for (BotPreset bot : bots) {
                String desc = bot.getDescription() != null && !bot.getDescription().isEmpty()
                        ? " - " + bot.getDescription() : "";
                source.sendSystemMessage(
                        Component.literal(String.format("  - %s%s (%.0f, %.0f, %.0f in %s)",
                                bot.getName(), desc, bot.getX(), bot.getY(), bot.getZ(),
                                bot.getDimension())));
            }
        }

        if (groups.isEmpty()) {
            source.sendSystemMessage(
                    Component.translatableWithFallback("carpetbotmanager.command.list.no_groups",
                            "No saved bot groups."));
        } else {
            source.sendSystemMessage(
                    Component.translatableWithFallback("carpetbotmanager.command.list.groups_title",
                            "Saved Groups:"));
            for (BotGroup group : groups) {
                String desc = group.getDescription() != null && !group.getDescription().isEmpty()
                        ? " - " + group.getDescription() : "";
                source.sendSystemMessage(
                        Component.literal(String.format("  - %s%s [%s]",
                                group.getName(), desc, String.join(", ", group.getBots()))));
            }
        }

        return 1;
    }
}
