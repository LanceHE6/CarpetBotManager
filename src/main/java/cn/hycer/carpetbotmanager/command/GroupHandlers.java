package cn.hycer.carpetbotmanager.command;

import cn.hycer.carpetbotmanager.data.BotDataManager;
import cn.hycer.carpetbotmanager.model.BotGroup;
import cn.hycer.carpetbotmanager.model.BotPreset;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import java.util.*;
import static cn.hycer.carpetbotmanager.command.CommandExceptions.*;

public final class GroupHandlers {

    private GroupHandlers() {}

    static int addGroup(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSourceStack src = ctx.getSource();
        String groupName = StringArgumentType.getString(ctx, "groupName");
        String desc = StringArgumentType.getString(ctx, "description");
        String botsStr = StringArgumentType.getString(ctx, "bots");
        BotDataManager dm = BotDataManager.getInstance();
        if (dm.hasBotGroup(groupName)) throw GROUP_ALREADY_EXISTS.create();
        List<String> v = new ArrayList<>(), nf = new ArrayList<>();
        for (String n : botsStr.split("\\s+"))
            if (dm.hasBotPreset(n)) v.add(n); else nf.add(n);
        if (v.isEmpty()) throw BOTS_NOT_FOUND_FOR_GROUP.create();
        dm.addBotGroup(new BotGroup(groupName, desc, v));
        src.sendSystemMessage(Component.translatableWithFallback(
                "carpetbotmanager.command.group.add.success", "Group '%s' created with %d bot(s).", groupName, v.size()));
        if (!nf.isEmpty()) src.sendSystemMessage(Component.translatableWithFallback(
                "carpetbotmanager.command.group.add.partial", "Warning: skipped: %s", String.join(", ", nf)));
        return 1;
    }

    static int removeGroup(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSourceStack src = ctx.getSource();
        String name = StringArgumentType.getString(ctx, "groupName");
        if (!BotDataManager.getInstance().hasBotGroup(name)) throw GROUP_NOT_FOUND.create();
        BotDataManager.getInstance().removeBotGroup(name);
        src.sendSystemMessage(Component.translatableWithFallback(
                "carpetbotmanager.command.group.remove.success", "Group '%s' removed.", name));
        return 1;
    }

    static int loadGroup(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSourceStack src = ctx.getSource();
        String name = StringArgumentType.getString(ctx, "groupName");
        BotDataManager dm = BotDataManager.getInstance();
        BotGroup g = dm.getBotGroup(name).orElseThrow(GROUP_NOT_FOUND::create);
        int ok = 0, fail = 0;
        for (String bn : g.getBots()) {
            BotPreset p = dm.getBotPreset(bn).orElse(null);
            if (p != null) { try { BotSpawner.spawn(src, p); ok++; } catch (Exception e) { fail++;
                src.sendSystemMessage(Component.translatableWithFallback(
                        "carpetbotmanager.command.group.load.failed_item", "Failed: %s: %s", bn, e.getMessage())); }
            } else { fail++; src.sendSystemMessage(Component.translatableWithFallback(
                    "carpetbotmanager.error.bot_not_found_item", "Bot '%s' not found.", bn)); }
        }
        final int finalOk = ok, finalFail = fail;
        src.sendSystemMessage(Component.translatableWithFallback(
                "carpetbotmanager.command.group.load.success", "Group '%s': %d ok, %d fail.", name, finalOk, finalFail));
        return 1;
    }
}
