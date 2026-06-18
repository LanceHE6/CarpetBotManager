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

public final class BotHandlers {

    private BotHandlers() {}

    static int showHelp(CommandContext<ServerCommandSource> ctx) {
        ServerCommandSource src = ctx.getSource();
        src.sendFeedback(() -> Text.translatableWithFallback(
                "carpetbotmanager.command.help.header", "=== /cbot Commands ==="), false);
        for (String line : new String[]{
                "/cbot add <player> [description]",
                "/cbot remove <name>", "/cbot load <name>",
                "/cbot list", "/cbot help", "/cbot ui",
                "/cbot autoload add <name>", "/cbot autoload remove <name>",
                "/cbot autoload list",
                "/cbot group add <name> <description> <bot1 bot2 ...>",
                "/cbot group remove <name>", "/cbot group load <name>",
                "/cbot group autoload add <name>",
                "/cbot group autoload remove <name>",
        }) { src.sendFeedback(() -> Text.literal("  " + line), false); }
        return 1;
    }

    static int addBot(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ServerCommandSource src = ctx.getSource();
        ServerPlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
        String botName = player.getGameProfile().name();
        String prefix = CarpetBotConfig.getInstance().getBotNamePrefix();

        if (!botName.toLowerCase(Locale.ROOT).startsWith(prefix.toLowerCase(Locale.ROOT)))
            throw NOT_BOT_PREFIX.create();

        BotDataManager dm = BotDataManager.getInstance();
        if (dm.hasBotPreset(botName)) throw BOT_ALREADY_EXISTS.create();

        String desc;
        try { desc = StringArgumentType.getString(ctx, "description"); }
        catch (IllegalArgumentException e) { desc = ""; }

        Vec3d look = player.getRotationVector();
        BotPreset preset = new BotPreset(botName, desc,
                player.getEntityWorld().getRegistryKey().getValue().toString(),
                player.getX(), player.getY(), player.getZ(),
                player.getYaw(), player.getPitch(),
                player.getX() + look.x, player.getEyeY() + look.y, player.getZ() + look.z);

        dm.addBotPreset(preset);
        src.sendFeedback(() -> Text.translatableWithFallback(
                "carpetbotmanager.command.add.success", "Bot preset '%s' saved successfully.", botName), true);
        return 1;
    }

    static int removeBot(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ServerCommandSource src = ctx.getSource();
        String name = StringArgumentType.getString(ctx, "name");
        if (!BotDataManager.getInstance().hasBotPreset(name)) throw BOT_NOT_FOUND.create();
        BotDataManager.getInstance().removeBotPreset(name);
        src.sendFeedback(() -> Text.translatableWithFallback(
                "carpetbotmanager.command.remove.success", "Bot preset '%s' removed.", name), true);
        return 1;
    }

    static int loadBot(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ServerCommandSource src = ctx.getSource();
        String name = StringArgumentType.getString(ctx, "name");
        BotPreset preset = BotDataManager.getInstance().getBotPreset(name)
                .orElseThrow(BOT_NOT_FOUND::create);
        BotSpawner.spawn(src, preset);
        src.sendFeedback(() -> Text.translatableWithFallback(
                "carpetbotmanager.command.load.success", "Bot '%s' spawned successfully.", name), true);
        return 1;
    }

    static int listBots(CommandContext<ServerCommandSource> ctx) {
        ServerCommandSource src = ctx.getSource();
        BotDataManager dm = BotDataManager.getInstance();

        src.sendFeedback(() -> Text.translatableWithFallback(
                "carpetbotmanager.command.list.header", "=== Carpet Bot Manager ==="), false);

        Collection<BotPreset> bots = dm.getAllBotPresets();
        if (bots.isEmpty()) {
            src.sendFeedback(() -> Text.translatableWithFallback(
                    "carpetbotmanager.command.list.no_bots", "No saved bot presets."), false);
        } else {
            src.sendFeedback(() -> Text.translatableWithFallback(
                    "carpetbotmanager.command.list.bots_title", "Saved Bots:"), false);
            for (BotPreset b : bots) {
                String d = b.getDescription() != null && !b.getDescription().isEmpty()
                        ? " - " + b.getDescription() : "";
                src.sendFeedback(() -> Text.literal(String.format(
                        "  - %s%s (%.0f, %.0f, %.0f in %s)",
                        b.getName(), d, b.getX(), b.getY(), b.getZ(), b.getDimension())), false);
            }
        }

        Collection<BotGroup> groups = dm.getAllBotGroups();
        if (groups.isEmpty()) {
            src.sendFeedback(() -> Text.translatableWithFallback(
                    "carpetbotmanager.command.list.no_groups", "No saved bot groups."), false);
        } else {
            src.sendFeedback(() -> Text.translatableWithFallback(
                    "carpetbotmanager.command.list.groups_title", "Saved Groups:"), false);
            for (BotGroup g : groups) {
                String d = g.getDescription() != null && !g.getDescription().isEmpty()
                        ? " - " + g.getDescription() : "";
                src.sendFeedback(() -> Text.literal(String.format(
                        "  - %s%s [%s]", g.getName(), d, String.join(", ", g.getBots()))), false);
            }
        }
        return 1;
    }
}
