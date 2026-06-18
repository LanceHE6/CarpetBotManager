package cn.hycer.carpetbotmanager.command;

import cn.hycer.carpetbotmanager.config.CarpetBotConfig;
import cn.hycer.carpetbotmanager.data.BotDataManager;
import cn.hycer.carpetbotmanager.model.BotGroup;
import cn.hycer.carpetbotmanager.model.BotPreset;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;

import java.util.Collection;
import java.util.Locale;

import static cn.hycer.carpetbotmanager.command.CommandExceptions.*;

public final class BotHandlers {

    private BotHandlers() {}

    static int showHelp(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack src = ctx.getSource();
        src.sendSystemMessage(Component.translatableWithFallback(
                "carpetbotmanager.command.help.header", "=== /cbot Commands ==="));
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
        }) { src.sendSystemMessage(Component.literal("  " + line)); }
        return 1;
    }

    static int addBot(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSourceStack src = ctx.getSource();
        ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
        String botName = player.getGameProfile().name();
        String prefix = CarpetBotConfig.getInstance().getBotNamePrefix();

        if (!botName.toLowerCase(Locale.ROOT).startsWith(prefix.toLowerCase(Locale.ROOT)))
            throw NOT_BOT_PREFIX.create();

        BotDataManager dm = BotDataManager.getInstance();
        if (dm.hasBotPreset(botName)) throw BOT_ALREADY_EXISTS.create();

        String desc;
        try { desc = StringArgumentType.getString(ctx, "description"); }
        catch (IllegalArgumentException e) { desc = ""; }

        Vec3 look = player.getLookAngle();
        BotPreset preset = new BotPreset(botName, desc,
                player.level().dimension().identifier().toString(),
                player.getX(), player.getY(), player.getZ(),
                player.getYRot(), player.getXRot(),
                player.getX() + look.x, player.getEyeY() + look.y, player.getZ() + look.z);

        dm.addBotPreset(preset);
        src.sendSystemMessage(Component.translatableWithFallback(
                "carpetbotmanager.command.add.success", "Bot preset '%s' saved successfully.", botName));
        return 1;
    }

    static int removeBot(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSourceStack src = ctx.getSource();
        String name = StringArgumentType.getString(ctx, "name");
        if (!BotDataManager.getInstance().hasBotPreset(name)) throw BOT_NOT_FOUND.create();
        BotDataManager.getInstance().removeBotPreset(name);
        src.sendSystemMessage(Component.translatableWithFallback(
                "carpetbotmanager.command.remove.success", "Bot preset '%s' removed.", name));
        return 1;
    }

    static int loadBot(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSourceStack src = ctx.getSource();
        String name = StringArgumentType.getString(ctx, "name");
        BotPreset preset = BotDataManager.getInstance().getBotPreset(name)
                .orElseThrow(BOT_NOT_FOUND::create);
        BotSpawner.spawn(src, preset);
        src.sendSystemMessage(Component.translatableWithFallback(
                "carpetbotmanager.command.load.success", "Bot '%s' spawned successfully.", name));
        return 1;
    }

    static int listBots(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack src = ctx.getSource();
        BotDataManager dm = BotDataManager.getInstance();

        src.sendSystemMessage(Component.translatableWithFallback(
                "carpetbotmanager.command.list.header", "=== Carpet Bot Manager ==="));

        Collection<BotPreset> bots = dm.getAllBotPresets();
        if (bots.isEmpty()) {
            src.sendSystemMessage(Component.translatableWithFallback(
                    "carpetbotmanager.command.list.no_bots", "No saved bot presets."));
        } else {
            src.sendSystemMessage(Component.translatableWithFallback(
                    "carpetbotmanager.command.list.bots_title", "Saved Bots:"));
            for (BotPreset b : bots) {
                String d = b.getDescription() != null && !b.getDescription().isEmpty()
                        ? " - " + b.getDescription() : "";
                src.sendSystemMessage(Component.literal(String.format(
                        "  - %s%s (%.0f, %.0f, %.0f in %s)",
                        b.getName(), d, b.getX(), b.getY(), b.getZ(), b.getDimension())));
            }
        }

        Collection<BotGroup> groups = dm.getAllBotGroups();
        if (groups.isEmpty()) {
            src.sendSystemMessage(Component.translatableWithFallback(
                    "carpetbotmanager.command.list.no_groups", "No saved bot groups."));
        } else {
            src.sendSystemMessage(Component.translatableWithFallback(
                    "carpetbotmanager.command.list.groups_title", "Saved Groups:"));
            for (BotGroup g : groups) {
                String d = g.getDescription() != null && !g.getDescription().isEmpty()
                        ? " - " + g.getDescription() : "";
                src.sendSystemMessage(Component.literal(String.format(
                        "  - %s%s [%s]", g.getName(), d, String.join(", ", g.getBots()))));
            }
        }
        return 1;
    }
}
