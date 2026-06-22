package cn.hycer.carpetbotmanager.command.handler;

import cn.hycer.carpetbotmanager.data.BotDataManager;
import cn.hycer.carpetbotmanager.model.BotGroup;
import cn.hycer.carpetbotmanager.model.BotPreset;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class BatchHandlers {

    private static final SimpleCommandExceptionType RANGE_INVALID =
            new SimpleCommandExceptionType(Text.translatableWithFallback(
                    "carpetbotmanager.error.batch_range", "Start must be <= end."));

    private BatchHandlers() {}

    public static int batchSpawn(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ServerCommandSource src = ctx.getSource();

        String prefix = StringArgumentType.getString(ctx, "prefix");
        int start = IntegerArgumentType.getInteger(ctx, "start");
        int end = IntegerArgumentType.getInteger(ctx, "end");

        if (start > end) throw RANGE_INVALID.create();

        double x, y, z;
        float yaw, pitch;
        try {
            x = DoubleArgumentType.getDouble(ctx, "x");
            y = DoubleArgumentType.getDouble(ctx, "y");
            z = DoubleArgumentType.getDouble(ctx, "z");
            yaw = 0; pitch = 0;
        } catch (IllegalArgumentException e) {
            ServerPlayerEntity player = src.getPlayerOrThrow();
            x = player.getX(); y = player.getY(); z = player.getZ();
            yaw = player.getYaw(); pitch = player.getPitch();
        }

        String dim;
        try {
            dim = StringArgumentType.getString(ctx, "dim");
        } catch (IllegalArgumentException e) {
            try {
                dim = src.getPlayerOrThrow().getEntityWorld().getRegistryKey().getValue().toString();
            } catch (CommandSyntaxException ex) {
                dim = "minecraft:overworld";
            }
        }

        for (int i = start; i <= end; i++) {
            String name = prefix + "_" + i;
            String cmd = String.format(Locale.ROOT,
                    "player %s spawn at %.2f %.2f %.2f facing %.2f %.2f in %s",
                    name, x, y, z, yaw, pitch, dim);
            src.getServer().getCommandManager().parseAndExecute(
                    src.getServer().getCommandSource(), cmd);
        }

        int count = end - start + 1;
        src.sendFeedback(() -> Text.translatableWithFallback(
                "carpetbotmanager.command.batch.spawn.success",
                "Spawned %d bots: %s_%d ~ %s_%d.", count, prefix, start, prefix, end), true);
        return 1;
    }

    public static int batchSave(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ServerCommandSource src = ctx.getSource();
        String prefix = StringArgumentType.getString(ctx, "prefix");
        int start = IntegerArgumentType.getInteger(ctx, "start");
        int end = IntegerArgumentType.getInteger(ctx, "end");

        if (start > end) throw RANGE_INVALID.create();

        List<String> missing = new ArrayList<>();
        for (int i = start; i <= end; i++) {
            String name = prefix + "_" + i;
            if (src.getServer().getPlayerManager().getPlayer(name) == null) {
                missing.add(name);
            }
        }
        if (!missing.isEmpty()) {
            throw new SimpleCommandExceptionType(Text.translatableWithFallback(
                    "carpetbotmanager.error.batch_not_all_online",
                    "All bots must be online. Missing: %s",
                    String.join(", ", missing))).create();
        }

        BotDataManager dm = BotDataManager.getInstance();
        List<String> names = new ArrayList<>();
        for (int i = start; i <= end; i++) {
            String name = prefix + "_" + i;
            ServerPlayerEntity bot = src.getServer().getPlayerManager().getPlayer(name);
            dm.addBotPreset(new BotPreset(
                    name, "",
                    bot.getEntityWorld().getRegistryKey().getValue().toString(),
                    bot.getX(), bot.getY(), bot.getZ(),
                    bot.getYaw(), bot.getPitch(),
                    bot.getX(), bot.getEyeY(), bot.getZ()));
            names.add(name);
        }

        dm.addBotGroup(new BotGroup(prefix, "", names));

        int count = names.size();
        src.sendFeedback(() -> Text.translatableWithFallback(
                "carpetbotmanager.command.batch.save.success",
                "Saved %d bots to group '%s': %s_%d ~ %s_%d.",
                count, prefix, prefix, start, prefix, end), true);
        return 1;
    }

    // ---- batch action helpers ----

    private static int runBatchAction(CommandContext<ServerCommandSource> ctx,
                                      String playerAction, String desc) throws CommandSyntaxException {
        ServerCommandSource src = ctx.getSource();
        String prefix = StringArgumentType.getString(ctx, "prefix");
        int start = IntegerArgumentType.getInteger(ctx, "start");
        int end = IntegerArgumentType.getInteger(ctx, "end");

        if (start > end) throw RANGE_INVALID.create();

        int done = 0, miss = 0;
        for (int i = start; i <= end; i++) {
            String name = prefix + "_" + i;
            if (src.getServer().getPlayerManager().getPlayer(name) != null) {
                String cmd = "player " + name + " " + playerAction;
                src.getServer().getCommandManager().parseAndExecute(
                        src.getServer().getCommandSource(), cmd);
                done++;
            } else { miss++; }
        }

        int count = end - start + 1;
        final int finalDone = done, finalMiss = miss;
        src.sendFeedback(() -> Text.translatableWithFallback(
                "carpetbotmanager.command.batch.action.success",
                "%s: %d/%d bots %s.", desc, finalDone, count, desc), true);
        if (finalMiss > 0) src.sendFeedback(() -> Text.translatableWithFallback(
                "carpetbotmanager.command.batch.action.missing",
                "Note: %d bots offline, skipped.", finalMiss), true);
        return 1;
    }

    // ---- batch actions ----

    public static int batchKill(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        return runBatchAction(ctx, "kill", "Killed");
    }

    public static int batchUse(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        return runBatchAction(ctx, "use once", "Used");
    }

    public static int batchUseContinuous(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        return runBatchAction(ctx, "use continuous", "Using continuously");
    }

    public static int batchUseInterval(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        int ticks = IntegerArgumentType.getInteger(ctx, "ticks");
        return runBatchAction(ctx, "use interval " + ticks, "Using every " + ticks + "t");
    }

    public static int batchAttack(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        return runBatchAction(ctx, "attack once", "Attacked");
    }

    public static int batchAttackContinuous(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        return runBatchAction(ctx, "attack continuous", "Attacking continuously");
    }

    public static int batchAttackInterval(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        int ticks = IntegerArgumentType.getInteger(ctx, "ticks");
        return runBatchAction(ctx, "attack interval " + ticks, "Attacking every " + ticks + "t");
    }

    public static int batchSneak(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        return runBatchAction(ctx, "sneak", "Sneak toggled");
    }
}
