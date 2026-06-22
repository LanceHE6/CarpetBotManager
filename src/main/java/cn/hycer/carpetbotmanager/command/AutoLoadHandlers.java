package cn.hycer.carpetbotmanager.command;

import cn.hycer.carpetbotmanager.config.CarpetBotConfig;
import cn.hycer.carpetbotmanager.data.BotDataManager;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import static cn.hycer.carpetbotmanager.command.CommandExceptions.*;

public final class AutoLoadHandlers {

    private AutoLoadHandlers() {}

    static int addAutoLoadBot(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSourceStack src = ctx.getSource();
        String name = StringArgumentType.getString(ctx, "name");
        if (!BotDataManager.getInstance().hasBotPreset(name)) throw BOT_NOT_FOUND.create();
        CarpetBotConfig cfg = CarpetBotConfig.getInstance();
        if (cfg.getAutoLoadBots().contains(name)) throw ALREADY_IN_AUTOLOAD.create();
        cfg.getAutoLoadBots().add(name); cfg.save();
        src.sendSystemMessage(Component.translatableWithFallback(
                "carpetbotmanager.command.autoload.add.success", "已将 bot '%s' 添加到自动加载列表。", name));
        return 1;
    }

    static int removeAutoLoadBot(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSourceStack src = ctx.getSource();
        String name = StringArgumentType.getString(ctx, "name");
        CarpetBotConfig cfg = CarpetBotConfig.getInstance();
        if (!cfg.getAutoLoadBots().remove(name)) throw NOT_IN_AUTOLOAD.create();
        cfg.save();
        src.sendSystemMessage(Component.translatableWithFallback(
                "carpetbotmanager.command.autoload.remove.success", "已将 bot '%s' 从自动加载列表中移除。", name));
        return 1;
    }

    static int listAutoLoad(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack src = ctx.getSource();
        CarpetBotConfig cfg = CarpetBotConfig.getInstance();
        src.sendSystemMessage(Component.translatableWithFallback(
                "carpetbotmanager.command.autoload.list.header", "=== 自动加载设置 ==="));
        java.util.List<String> bots = cfg.getAutoLoadBots();
        if (bots.isEmpty()) src.sendSystemMessage(Component.translatableWithFallback(
                "carpetbotmanager.command.autoload.list.no_bots", "自动加载列表中没有 bot。"));
        else { src.sendSystemMessage(Component.translatableWithFallback(
                "carpetbotmanager.command.autoload.list.bots_title", "自动加载 Bot："));
            for (String b : bots) src.sendSystemMessage(Component.literal("  - " + b)); }
        java.util.List<String> groups = cfg.getAutoLoadGroups();
        if (groups.isEmpty()) src.sendSystemMessage(Component.translatableWithFallback(
                "carpetbotmanager.command.autoload.list.no_groups", "自动加载列表中没有组。"));
        else { src.sendSystemMessage(Component.translatableWithFallback(
                "carpetbotmanager.command.autoload.list.groups_title", "自动加载组："));
            for (String g : groups) src.sendSystemMessage(Component.literal("  - " + g)); }
        return 1;
    }

    static int addAutoLoadGroup(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSourceStack src = ctx.getSource();
        String name = StringArgumentType.getString(ctx, "groupName");
        if (!BotDataManager.getInstance().hasBotGroup(name)) throw GROUP_NOT_FOUND.create();
        CarpetBotConfig cfg = CarpetBotConfig.getInstance();
        if (cfg.getAutoLoadGroups().contains(name)) throw GROUP_ALREADY_IN_AUTOLOAD.create();
        cfg.getAutoLoadGroups().add(name); cfg.save();
        src.sendSystemMessage(Component.translatableWithFallback(
                "carpetbotmanager.command.group.autoload.add.success", "已将组 '%s' 添加到自动加载列表。", name));
        return 1;
    }

    static int removeAutoLoadGroup(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSourceStack src = ctx.getSource();
        String name = StringArgumentType.getString(ctx, "groupName");
        CarpetBotConfig cfg = CarpetBotConfig.getInstance();
        if (!cfg.getAutoLoadGroups().remove(name)) throw GROUP_NOT_IN_AUTOLOAD.create();
        cfg.save();
        src.sendSystemMessage(Component.translatableWithFallback(
                "carpetbotmanager.command.group.autoload.remove.success", "已将组 '%s' 从自动加载列表中移除。", name));
        return 1;
    }
}
