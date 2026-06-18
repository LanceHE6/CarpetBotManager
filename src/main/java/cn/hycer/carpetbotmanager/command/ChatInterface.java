package cn.hycer.carpetbotmanager.command;

import cn.hycer.carpetbotmanager.config.CarpetBotConfig;
import cn.hycer.carpetbotmanager.data.BotDataManager;
import cn.hycer.carpetbotmanager.model.BotGroup;
import cn.hycer.carpetbotmanager.model.BotPreset;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.*;
import java.util.Collection;

public final class ChatInterface {

    private static final String CBOT = "/cbot ";

    private ChatInterface() {}

    static int showMainMenu(CommandContext<ServerCommandSource> ctx) {
        ServerCommandSource src = ctx.getSource();
        src.sendFeedback(() -> Text.literal(""), false);
        src.sendFeedback(() -> title("CarpetBotManager"), false);
        src.sendFeedback(() -> Text.literal("")
                .append(btn(" [Bot list] ", CBOT + "ui bots"))
                .append(Text.literal("  "))
                .append(btn(" [Group list] ", CBOT + "ui groups"))
                .append(Text.literal("  "))
                .append(btn(" [Autoload] ", CBOT + "ui autoload")), false);
        src.sendFeedback(() -> Text.literal("")
                .append(btn(" [Add bot] ", CBOT + "ui add"))
                .append(Text.literal("  "))
                .append(btn(" [Help] ", CBOT + "help")), false);
        return 1;
    }

    static int showBotList(CommandContext<ServerCommandSource> ctx) {
        ServerCommandSource src = ctx.getSource();
        BotDataManager dm = BotDataManager.getInstance();
        src.sendFeedback(() -> Text.literal(""), false);
        src.sendFeedback(() -> title("Bot List"), false);
        if (dm.getAllBotPresets().isEmpty())
            src.sendFeedback(() -> Text.literal("  No saved bots. Use /cbot add <player>"), false);
        else for (BotPreset b : dm.getAllBotPresets()) {
            String d = b.getDescription() != null && !b.getDescription().isEmpty()
                    ? " - " + b.getDescription() : "";
            src.sendFeedback(() -> Text.literal("  " + b.getName() + d), false);
            src.sendFeedback(() -> Text.literal("    ")
                    .append(btn("[Spawn]", CBOT + "load " + b.getName()))
                    .append(Text.literal(" "))
                    .append(btn("[Delete]", CBOT + "remove " + b.getName())), false);
        }
        src.sendFeedback(() -> Text.literal("").append(back()), false);
        return 1;
    }

    static int showGroupList(CommandContext<ServerCommandSource> ctx) {
        ServerCommandSource src = ctx.getSource();
        BotDataManager dm = BotDataManager.getInstance();
        src.sendFeedback(() -> Text.literal(""), false);
        src.sendFeedback(() -> title("Group List"), false);
        if (dm.getAllBotGroups().isEmpty())
            src.sendFeedback(() -> Text.literal("  No groups. Use /cbot group add ..."), false);
        else for (BotGroup g : dm.getAllBotGroups()) {
            String d = g.getDescription() != null && !g.getDescription().isEmpty()
                    ? " - " + g.getDescription() : "";
            src.sendFeedback(() -> Text.literal("  " + g.getName() + d
                    + " [" + String.join(", ", g.getBots()) + "]"), false);
            src.sendFeedback(() -> Text.literal("    ")
                    .append(btn("[Load]", CBOT + "group load " + g.getName()))
                    .append(Text.literal(" "))
                    .append(btn("[Delete]", CBOT + "group remove " + g.getName())), false);
        }
        src.sendFeedback(() -> Text.literal("").append(back()), false);
        return 1;
    }

    static int showAutoLoad(CommandContext<ServerCommandSource> ctx) {
        ServerCommandSource src = ctx.getSource();
        CarpetBotConfig cfg = CarpetBotConfig.getInstance();
        src.sendFeedback(() -> Text.literal(""), false);
        src.sendFeedback(() -> title("Autoload Settings"), false);
        src.sendFeedback(() -> Text.literal("  Auto-load Bots:"), false);
        if (cfg.getAutoLoadBots().isEmpty()) src.sendFeedback(() -> Text.literal("    (none)"), false);
        else for (String n : cfg.getAutoLoadBots())
            src.sendFeedback(() -> Text.literal("    " + n + "  ")
                    .append(btn("[Remove]", CBOT + "autoload remove " + n)), false);
        src.sendFeedback(() -> Text.literal("  Auto-load Groups:"), false);
        if (cfg.getAutoLoadGroups().isEmpty()) src.sendFeedback(() -> Text.literal("    (none)"), false);
        else for (String n : cfg.getAutoLoadGroups())
            src.sendFeedback(() -> Text.literal("    " + n + "  ")
                    .append(btn("[Remove]", CBOT + "group autoload remove " + n)), false);
        src.sendFeedback(() -> Text.literal("").append(back()), false);
        return 1;
    }

    static int showAddHelp(CommandContext<ServerCommandSource> ctx) {
        ServerCommandSource src = ctx.getSource();
        src.sendFeedback(() -> Text.literal(""), false);
        src.sendFeedback(() -> title("Add Bot"), false);
        src.sendFeedback(() -> Text.literal("  Usage: /cbot add <player> [description]"), false);
        src.sendFeedback(() -> Text.literal("  - Player must be online"), false);
        src.sendFeedback(() -> Text.literal("  - Name must start with 'bot_'"), false);
        src.sendFeedback(() -> Text.literal("  Example: /cbot add bot_miner Mining bot"), false);
        src.sendFeedback(() -> Text.literal("").append(back()), false);
        return 1;
    }

    static int showAutoLoadAddBot(CommandContext<ServerCommandSource> ctx, String name) {
        ServerCommandSource src = ctx.getSource();
        src.sendFeedback(() -> Text.literal(""), false);
        src.sendFeedback(() -> Text.literal("  Add '" + name + "' to auto-load?"), false);
        src.sendFeedback(() -> Text.literal("")
                .append(btn(" [YES] ", CBOT + "autoload add " + name))
                .append(Text.literal("  "))
                .append(btn(" [NO] ", CBOT + "ui bots")), false);
        return 1;
    }

    static int showAutoLoadAddGroup(CommandContext<ServerCommandSource> ctx, String name) {
        ServerCommandSource src = ctx.getSource();
        src.sendFeedback(() -> Text.literal(""), false);
        src.sendFeedback(() -> Text.literal("  Add group '" + name + "' to auto-load?"), false);
        src.sendFeedback(() -> Text.literal("")
                .append(btn(" [YES] ", CBOT + "group autoload add " + name))
                .append(Text.literal("  "))
                .append(btn(" [NO] ", CBOT + "ui groups")), false);
        return 1;
    }

    private static MutableText title(String t) {
        return Text.literal("  ==== " + t + " ====")
                .styled(s -> s.withColor(TextColor.fromRgb(0xFFAA00)).withBold(true));
    }

    private static MutableText btn(String label, String cmd) {
        return Text.literal(label)
                .styled(s -> s
                        .withClickEvent(new ClickEvent.RunCommand(cmd))
                        .withHoverEvent(new HoverEvent.ShowText(Text.literal(cmd)))
                        .withColor(TextColor.fromRgb(0x55FFFF)));
    }

    private static MutableText back() { return btn(" [Back] ", CBOT + "ui"); }
}
