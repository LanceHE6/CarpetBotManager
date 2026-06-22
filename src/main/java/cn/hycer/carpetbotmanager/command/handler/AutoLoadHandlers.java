package cn.hycer.carpetbotmanager.command.handler;

import cn.hycer.carpetbotmanager.config.CarpetBotConfig;
import cn.hycer.carpetbotmanager.data.BotDataManager;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import static cn.hycer.carpetbotmanager.command.CommandExceptions.*;

public final class AutoLoadHandlers {

    private AutoLoadHandlers() {}

    public static int addAutoLoadBot(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ServerCommandSource src = ctx.getSource();
        String name = StringArgumentType.getString(ctx, "name");
        if (!BotDataManager.getInstance().hasBotPreset(name)) throw BOT_NOT_FOUND.create();
        CarpetBotConfig cfg = CarpetBotConfig.getInstance();
        if (cfg.getAutoLoadBots().contains(name)) throw ALREADY_IN_AUTOLOAD.create();
        cfg.getAutoLoadBots().add(name); cfg.save();
        src.sendFeedback(() -> Text.translatableWithFallback(
                "carpetbotmanager.command.autoload.add.success", "Bot '%s' added to auto-load.", name), true);
        return 1;
    }

    public static int removeAutoLoadBot(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ServerCommandSource src = ctx.getSource();
        String name = StringArgumentType.getString(ctx, "name");
        CarpetBotConfig cfg = CarpetBotConfig.getInstance();
        if (!cfg.getAutoLoadBots().remove(name)) throw NOT_IN_AUTOLOAD.create();
        cfg.save();
        src.sendFeedback(() -> Text.translatableWithFallback(
                "carpetbotmanager.command.autoload.remove.success", "Bot '%s' removed from auto-load.", name), true);
        return 1;
    }

    public static int listAutoLoad(CommandContext<ServerCommandSource> ctx) {
        ServerCommandSource src = ctx.getSource();
        CarpetBotConfig cfg = CarpetBotConfig.getInstance();
        src.sendFeedback(() -> Text.translatableWithFallback(
                "carpetbotmanager.command.autoload.list.header", "=== Auto-load Settings ==="), false);
        java.util.List<String> bots = cfg.getAutoLoadBots();
        if (bots.isEmpty()) src.sendFeedback(() -> Text.translatableWithFallback(
                "carpetbotmanager.command.autoload.list.no_bots", "No bots."), false);
        else { src.sendFeedback(() -> Text.translatableWithFallback(
                "carpetbotmanager.command.autoload.list.bots_title", "Auto-load Bots:"), false);
            for (String b : bots) src.sendFeedback(() -> Text.literal("  - " + b), false); }
        java.util.List<String> groups = cfg.getAutoLoadGroups();
        if (groups.isEmpty()) src.sendFeedback(() -> Text.translatableWithFallback(
                "carpetbotmanager.command.autoload.list.no_groups", "No groups."), false);
        else { src.sendFeedback(() -> Text.translatableWithFallback(
                "carpetbotmanager.command.autoload.list.groups_title", "Auto-load Groups:"), false);
            for (String g : groups) src.sendFeedback(() -> Text.literal("  - " + g), false); }
        return 1;
    }

    public static int addAutoLoadGroup(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ServerCommandSource src = ctx.getSource();
        String name = StringArgumentType.getString(ctx, "groupName");
        if (!BotDataManager.getInstance().hasBotGroup(name)) throw GROUP_NOT_FOUND.create();
        CarpetBotConfig cfg = CarpetBotConfig.getInstance();
        if (cfg.getAutoLoadGroups().contains(name)) throw GROUP_ALREADY_IN_AUTOLOAD.create();
        cfg.getAutoLoadGroups().add(name); cfg.save();
        src.sendFeedback(() -> Text.translatableWithFallback(
                "carpetbotmanager.command.group.autoload.add.success", "Group '%s' added to auto-load.", name), true);
        return 1;
    }

    public static int removeAutoLoadGroup(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ServerCommandSource src = ctx.getSource();
        String name = StringArgumentType.getString(ctx, "groupName");
        CarpetBotConfig cfg = CarpetBotConfig.getInstance();
        if (!cfg.getAutoLoadGroups().remove(name)) throw GROUP_NOT_IN_AUTOLOAD.create();
        cfg.save();
        src.sendFeedback(() -> Text.translatableWithFallback(
                "carpetbotmanager.command.group.autoload.remove.success", "Group '%s' removed from auto-load.", name), true);
        return 1;
    }
}
