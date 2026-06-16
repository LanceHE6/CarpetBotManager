package cn.hycer.carpetbotmanager.command;

import cn.hycer.carpetbotmanager.config.CarpetBotConfig;
import cn.hycer.carpetbotmanager.data.BotDataManager;
import cn.hycer.carpetbotmanager.model.BotGroup;
import cn.hycer.carpetbotmanager.model.BotPreset;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.permission.PermissionCheck;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

import java.util.Collection;
import java.util.Locale;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class CarpetBotCommand {

    private static final SimpleCommandExceptionType NOT_BOT_PREFIX =
            new SimpleCommandExceptionType(Text.translatable("carpetbotmanager.error.not_bot_prefix"));
    private static final SimpleCommandExceptionType BOT_NOT_FOUND =
            new SimpleCommandExceptionType(Text.translatable("carpetbotmanager.error.bot_not_found"));
    private static final SimpleCommandExceptionType BOT_ALREADY_EXISTS =
            new SimpleCommandExceptionType(Text.translatable("carpetbotmanager.error.bot_already_exists"));
    private static final SimpleCommandExceptionType GROUP_NOT_FOUND =
            new SimpleCommandExceptionType(Text.translatable("carpetbotmanager.error.group_not_found"));
    private static final SimpleCommandExceptionType GROUP_ALREADY_EXISTS =
            new SimpleCommandExceptionType(Text.translatable("carpetbotmanager.error.group_already_exists"));
    private static final SimpleCommandExceptionType BOTS_NOT_FOUND_FOR_GROUP =
            new SimpleCommandExceptionType(Text.translatable("carpetbotmanager.error.bots_not_found_for_group"));
    private static final SimpleCommandExceptionType ALREADY_IN_AUTOLOAD =
            new SimpleCommandExceptionType(Text.translatable("carpetbotmanager.error.already_in_autoload"));
    private static final SimpleCommandExceptionType NOT_IN_AUTOLOAD =
            new SimpleCommandExceptionType(Text.translatable("carpetbotmanager.error.not_in_autoload"));
    private static final SimpleCommandExceptionType GROUP_ALREADY_IN_AUTOLOAD =
            new SimpleCommandExceptionType(Text.translatable("carpetbotmanager.error.group_already_in_autoload"));
    private static final SimpleCommandExceptionType GROUP_NOT_IN_AUTOLOAD =
            new SimpleCommandExceptionType(Text.translatable("carpetbotmanager.error.group_not_in_autoload"));

    private static final SuggestionProvider<ServerCommandSource> BOT_NAME_SUGGESTIONS =
            (context, builder) -> CommandSource.suggestMatching(
                    BotDataManager.getInstance().getAllBotPresets().stream().map(BotPreset::getName),
                    builder);

    private static final SuggestionProvider<ServerCommandSource> GROUP_NAME_SUGGESTIONS =
            (context, builder) -> CommandSource.suggestMatching(
                    BotDataManager.getInstance().getAllBotGroups().stream().map(BotGroup::getName),
                    builder);

    private static final SuggestionProvider<ServerCommandSource> AUTOLOAD_BOT_SUGGESTIONS =
            (context, builder) -> CommandSource.suggestMatching(
                    CarpetBotConfig.getInstance().getAutoLoadBots().stream(),
                    builder);

    private static final SuggestionProvider<ServerCommandSource> AUTOLOAD_GROUP_SUGGESTIONS =
            (context, builder) -> CommandSource.suggestMatching(
                    CarpetBotConfig.getInstance().getAutoLoadGroups().stream(),
                    builder);

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            registerCommands(dispatcher);
        });
    }

    /**
     * Maps the configured permission level (0–4) to the corresponding
     * {@link PermissionCheck} constant from {@link CommandManager}.
     */
    private static PermissionCheck getRequiredPermissionCheck() {
        int level = CarpetBotConfig.getInstance().getPermissionLevel();
        return switch (level) {
            case 0 -> CommandManager.ALWAYS_PASS_CHECK;
            case 1 -> CommandManager.MODERATORS_CHECK;
            case 2 -> CommandManager.GAMEMASTERS_CHECK;
            case 3 -> CommandManager.ADMINS_CHECK;
            case 4 -> CommandManager.OWNERS_CHECK;
            default -> CommandManager.GAMEMASTERS_CHECK;
        };
    }

    private static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                literal("cbot")
                        .requires(CommandManager.requirePermissionLevel(
                                getRequiredPermissionCheck()))
                        // /cbot add <player> [description]
                        .then(literal("add")
                                .then(argument("player", EntityArgumentType.player())
                                        .executes(CarpetBotCommand::addBot)
                                        .then(argument("description", StringArgumentType.greedyString())
                                                .executes(CarpetBotCommand::addBot))))
                        // /cbot remove <name>
                        .then(literal("remove")
                                .then(argument("name", StringArgumentType.word())
                                        .suggests(BOT_NAME_SUGGESTIONS)
                                        .executes(CarpetBotCommand::removeBot)))
                        // /cbot load <name>
                        .then(literal("load")
                                .then(argument("name", StringArgumentType.word())
                                        .suggests(BOT_NAME_SUGGESTIONS)
                                        .executes(CarpetBotCommand::loadBot)))
                        // /cbot help
                        .then(literal("help")
                                .executes(CarpetBotCommand::showHelp))
                        // /cbot list
                        .then(literal("list")
                                .executes(CarpetBotCommand::listBots))
                        // /cbot autoload ...
                        .then(literal("autoload")
                                // /cbot autoload add <name>
                                .then(literal("add")
                                        .then(argument("name", StringArgumentType.word())
                                                .suggests(BOT_NAME_SUGGESTIONS)
                                                .executes(CarpetBotCommand::addAutoLoadBot)))
                                // /cbot autoload remove <name>
                                .then(literal("remove")
                                        .then(argument("name", StringArgumentType.word())
                                                .suggests(AUTOLOAD_BOT_SUGGESTIONS)
                                                .executes(CarpetBotCommand::removeAutoLoadBot)))
                                // /cbot autoload list
                                .then(literal("list")
                                        .executes(CarpetBotCommand::listAutoLoad)))
                        // /cbot group ...
                        .then(literal("group")
                                // /cbot group add <groupName> <description> <bot1> <bot2> ...
                                .then(literal("add")
                                        .then(argument("groupName", StringArgumentType.word())
                                                .then(argument("description", StringArgumentType.word())
                                                        .then(argument("bots", StringArgumentType.greedyString())
                                                                .executes(CarpetBotCommand::addGroup)))))
                                // /cbot group remove <groupName>
                                .then(literal("remove")
                                        .then(argument("groupName", StringArgumentType.word())
                                                .suggests(GROUP_NAME_SUGGESTIONS)
                                                .executes(CarpetBotCommand::removeGroup)))
                                // /cbot group load <groupName>
                                .then(literal("load")
                                        .then(argument("groupName", StringArgumentType.word())
                                                .suggests(GROUP_NAME_SUGGESTIONS)
                                                .executes(CarpetBotCommand::loadGroup)))
                                // /cbot group autoload ...
                                .then(literal("autoload")
                                        // /cbot group autoload add <groupName>
                                        .then(literal("add")
                                                .then(argument("groupName", StringArgumentType.word())
                                                        .suggests(GROUP_NAME_SUGGESTIONS)
                                                        .executes(CarpetBotCommand::addAutoLoadGroup)))
                                        // /cbot group autoload remove <groupName>
                                        .then(literal("remove")
                                                .then(argument("groupName", StringArgumentType.word())
                                                        .suggests(AUTOLOAD_GROUP_SUGGESTIONS)
                                                        .executes(CarpetBotCommand::removeAutoLoadGroup)))))
        );
    }

    // === Command Handlers ===

    private static int showHelp(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();

        source.sendFeedback(
                () -> Text.translatable("carpetbotmanager.command.help.header"), false);

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

    private static int addBot(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "player");
        String botName = player.getGameProfile().name();
        String prefix = CarpetBotConfig.getInstance().getBotNamePrefix();

        // Check bot name prefix
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

        // Calculate looking-at coordinates (1 block ahead of where the player is looking)
        Vec3d lookVec = player.getRotationVector();
        double lookX = player.getX() + lookVec.x;
        double lookY = player.getEyeY() + lookVec.y;
        double lookZ = player.getZ() + lookVec.z;

        BotPreset preset = new BotPreset(
                botName,
                description,
                player.getEntityWorld().getRegistryKey().getValue().toString(),
                player.getX(),
                player.getY(),
                player.getZ(),
                player.getYaw(),
                player.getPitch(),
                lookX,
                lookY,
                lookZ
        );

        dataManager.addBotPreset(preset);

        source.sendFeedback(
                () -> Text.translatable("carpetbotmanager.command.add.success", botName),
                true);

        return 1;
    }

    private static int removeBot(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        String name = StringArgumentType.getString(context, "name");

        BotDataManager dataManager = BotDataManager.getInstance();
        if (!dataManager.hasBotPreset(name)) {
            throw BOT_NOT_FOUND.create();
        }

        dataManager.removeBotPreset(name);

        source.sendFeedback(
                () -> Text.translatable("carpetbotmanager.command.remove.success", name),
                true);

        return 1;
    }

    private static int loadBot(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        String name = StringArgumentType.getString(context, "name");

        BotDataManager dataManager = BotDataManager.getInstance();
        BotPreset preset = dataManager.getBotPreset(name)
                .orElseThrow(BOT_NOT_FOUND::create);

        spawnBot(source, preset);

        source.sendFeedback(
                () -> Text.translatable("carpetbotmanager.command.load.success", name),
                true);

        return 1;
    }

    private static int listBots(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        BotDataManager dataManager = BotDataManager.getInstance();

        Collection<BotPreset> bots = dataManager.getAllBotPresets();
        Collection<BotGroup> groups = dataManager.getAllBotGroups();

        source.sendFeedback(
                () -> Text.translatable("carpetbotmanager.command.list.header"), false);

        if (bots.isEmpty()) {
            source.sendFeedback(
                    () -> Text.translatable("carpetbotmanager.command.list.no_bots"), false);
        } else {
            source.sendFeedback(
                    () -> Text.translatable("carpetbotmanager.command.list.bots_title"), false);
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
                    () -> Text.translatable("carpetbotmanager.command.list.no_groups"), false);
        } else {
            source.sendFeedback(
                    () -> Text.translatable("carpetbotmanager.command.list.groups_title"), false);
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

    // === Group Handlers ===

    private static int addGroup(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        String groupName = StringArgumentType.getString(context, "groupName");
        String description = StringArgumentType.getString(context, "description");
        String botsStr = StringArgumentType.getString(context, "bots");

        BotDataManager dataManager = BotDataManager.getInstance();

        if (dataManager.hasBotGroup(groupName)) {
            throw GROUP_ALREADY_EXISTS.create();
        }

        String[] botNames = botsStr.split("\\s+");
        java.util.List<String> validBots = new java.util.ArrayList<>();
        java.util.List<String> notFound = new java.util.ArrayList<>();

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

        source.sendFeedback(
                () -> Text.translatable("carpetbotmanager.command.group.add.success", groupName, validBots.size()),
                true);

        if (!notFound.isEmpty()) {
            source.sendFeedback(
                    () -> Text.translatable("carpetbotmanager.command.group.add.partial",
                            String.join(", ", notFound)),
                    true);
        }

        return 1;
    }

    private static int removeGroup(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        String groupName = StringArgumentType.getString(context, "groupName");

        BotDataManager dataManager = BotDataManager.getInstance();

        if (!dataManager.hasBotGroup(groupName)) {
            throw GROUP_NOT_FOUND.create();
        }

        dataManager.removeBotGroup(groupName);

        source.sendFeedback(
                () -> Text.translatable("carpetbotmanager.command.group.remove.success", groupName),
                true);

        return 1;
    }

    private static int loadGroup(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
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
                    spawnBot(source, preset);
                    loaded++;
                } catch (Exception e) {
                    failed++;
                    source.sendFeedback(
                            () -> Text.translatable("carpetbotmanager.command.group.load.failed_item",
                                    botName, e.getMessage()),
                            true);
                }
            } else {
                failed++;
                source.sendFeedback(
                        () -> Text.translatable("carpetbotmanager.error.bot_not_found_item", botName),
                        true);
            }
        }

        final int finalLoaded = loaded;
        final int finalFailed = failed;
        source.sendFeedback(
                () -> Text.translatable("carpetbotmanager.command.group.load.success",
                        groupName, finalLoaded, finalFailed),
                true);

        return 1;
    }

    // === Auto-load Handlers ===

    private static int addAutoLoadBot(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        String name = StringArgumentType.getString(context, "name");

        BotDataManager dataManager = BotDataManager.getInstance();
        if (!dataManager.hasBotPreset(name)) {
            throw BOT_NOT_FOUND.create();
        }

        CarpetBotConfig config = CarpetBotConfig.getInstance();
        if (config.getAutoLoadBots().contains(name)) {
            throw ALREADY_IN_AUTOLOAD.create();
        }

        config.getAutoLoadBots().add(name);
        config.save();

        source.sendFeedback(
                () -> Text.translatable("carpetbotmanager.command.autoload.add.success", name),
                true);
        return 1;
    }

    private static int removeAutoLoadBot(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        String name = StringArgumentType.getString(context, "name");

        CarpetBotConfig config = CarpetBotConfig.getInstance();
        if (!config.getAutoLoadBots().remove(name)) {
            throw NOT_IN_AUTOLOAD.create();
        }

        config.save();

        source.sendFeedback(
                () -> Text.translatable("carpetbotmanager.command.autoload.remove.success", name),
                true);
        return 1;
    }

    private static int listAutoLoad(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        CarpetBotConfig config = CarpetBotConfig.getInstance();

        source.sendFeedback(
                () -> Text.translatable("carpetbotmanager.command.autoload.list.header"), false);

        java.util.List<String> bots = config.getAutoLoadBots();
        if (bots.isEmpty()) {
            source.sendFeedback(
                    () -> Text.translatable("carpetbotmanager.command.autoload.list.no_bots"), false);
        } else {
            source.sendFeedback(
                    () -> Text.translatable("carpetbotmanager.command.autoload.list.bots_title"), false);
            for (String bot : bots) {
                source.sendFeedback(
                        () -> Text.literal("  - " + bot), false);
            }
        }

        java.util.List<String> groups = config.getAutoLoadGroups();
        if (groups.isEmpty()) {
            source.sendFeedback(
                    () -> Text.translatable("carpetbotmanager.command.autoload.list.no_groups"), false);
        } else {
            source.sendFeedback(
                    () -> Text.translatable("carpetbotmanager.command.autoload.list.groups_title"), false);
            for (String group : groups) {
                source.sendFeedback(
                        () -> Text.literal("  - " + group), false);
            }
        }

        return 1;
    }

    private static int addAutoLoadGroup(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        String groupName = StringArgumentType.getString(context, "groupName");

        BotDataManager dataManager = BotDataManager.getInstance();
        if (!dataManager.hasBotGroup(groupName)) {
            throw GROUP_NOT_FOUND.create();
        }

        CarpetBotConfig config = CarpetBotConfig.getInstance();
        if (config.getAutoLoadGroups().contains(groupName)) {
            throw GROUP_ALREADY_IN_AUTOLOAD.create();
        }

        config.getAutoLoadGroups().add(groupName);
        config.save();

        source.sendFeedback(
                () -> Text.translatable("carpetbotmanager.command.group.autoload.add.success", groupName),
                true);
        return 1;
    }

    private static int removeAutoLoadGroup(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        String groupName = StringArgumentType.getString(context, "groupName");

        CarpetBotConfig config = CarpetBotConfig.getInstance();
        if (!config.getAutoLoadGroups().remove(groupName)) {
            throw GROUP_NOT_IN_AUTOLOAD.create();
        }

        config.save();

        source.sendFeedback(
                () -> Text.translatable("carpetbotmanager.command.group.autoload.remove.success", groupName),
                true);
        return 1;
    }

    // === Carpet Player Spawn ===

    public static void spawnBot(ServerCommandSource source, BotPreset preset) {
        // Build the carpet player spawn command
        String command = String.format(Locale.ROOT,
                "player %s spawn at %.2f %.2f %.2f facing %.2f %.2f in %s",
                preset.getName(),
                preset.getX(),
                preset.getY(),
                preset.getZ(),
                preset.getYaw(),
                preset.getPitch(),
                preset.getDimension());

        source.getServer().getCommandManager().parseAndExecute(
                source.getServer().getCommandSource(), command);
    }

    // === Auto-load ===

    public static void autoLoadBots(ServerCommandSource source) {
        CarpetBotConfig config = CarpetBotConfig.getInstance();
        BotDataManager dataManager = BotDataManager.getInstance();

        // Auto-load individual bots
        for (String botName : config.getAutoLoadBots()) {
            BotPreset preset = dataManager.getBotPreset(botName).orElse(null);
            if (preset != null) {
                spawnBot(source, preset);
            }
        }

        // Auto-load groups
        for (String groupName : config.getAutoLoadGroups()) {
            BotGroup group = dataManager.getBotGroup(groupName).orElse(null);
            if (group != null) {
                for (String botName : group.getBots()) {
                    BotPreset preset = dataManager.getBotPreset(botName).orElse(null);
                    if (preset != null) {
                        spawnBot(source, preset);
                    }
                }
            }
        }
    }
}
