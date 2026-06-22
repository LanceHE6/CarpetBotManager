package cn.hycer.carpetbotmanager.command.tree;

import cn.hycer.carpetbotmanager.command.handler.BatchHandlers;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

/**
 * /cbot batch &lt;prefix&gt; &lt;start&gt; &lt;end&gt; [spawn|save|kill|use|attack|sneak] 子命令树。
 */
public final class BatchCommandTree {

    private BatchCommandTree() {}

    public static LiteralArgumentBuilder<ServerCommandSource> addNodes(LiteralArgumentBuilder<ServerCommandSource> root) {
        // spawn at <x> <y> <z> [in <dim>]
        var spawnAt = argument("x", DoubleArgumentType.doubleArg())
                .then(argument("y", DoubleArgumentType.doubleArg())
                        .then(argument("z", DoubleArgumentType.doubleArg())
                                .executes(BatchHandlers::batchSpawn)
                                .then(literal("in")
                                        .then(argument("dim", StringArgumentType.word())
                                                .executes(BatchHandlers::batchSpawn)))));

        // spawn in <dim> [at <x> <y> <z>]
        var spawnIn = literal("in")
                .then(argument("dim", StringArgumentType.word())
                        .executes(BatchHandlers::batchSpawn)
                        .then(spawnAt));

        // spawn [at ... | in ...]
        var spawnNode = literal("spawn")
                .executes(BatchHandlers::batchSpawn)
                .then(literal("at").then(spawnAt))
                .then(spawnIn);

        // use [continuous | interval <ticks>]
        var useNode = literal("use")
                .executes(BatchHandlers::batchUse)
                .then(literal("continuous")
                        .executes(BatchHandlers::batchUseContinuous))
                .then(literal("interval")
                        .then(argument("ticks", IntegerArgumentType.integer(1))
                                .executes(BatchHandlers::batchUseInterval)));

        // attack [continuous | interval <ticks>]
        var attackNode = literal("attack")
                .executes(BatchHandlers::batchAttack)
                .then(literal("continuous")
                        .executes(BatchHandlers::batchAttackContinuous))
                .then(literal("interval")
                        .then(argument("ticks", IntegerArgumentType.integer(1))
                                .executes(BatchHandlers::batchAttackInterval)));

        return root.then(literal("batch")
                .then(argument("prefix", StringArgumentType.word())
                        .then(argument("start", IntegerArgumentType.integer(1))
                                .then(argument("end", IntegerArgumentType.integer(1))
                                        .then(spawnNode)
                                        .then(literal("save")
                                                .executes(BatchHandlers::batchSave))
                                        .then(literal("kill")
                                                .executes(BatchHandlers::batchKill))
                                        .then(useNode)
                                        .then(attackNode)
                                        .then(literal("sneak")
                                                .executes(BatchHandlers::batchSneak))))));
    }
}
