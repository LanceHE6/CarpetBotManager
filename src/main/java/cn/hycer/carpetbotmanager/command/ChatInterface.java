package cn.hycer.carpetbotmanager.command;

import cn.hycer.carpetbotmanager.config.CarpetBotConfig;
import cn.hycer.carpetbotmanager.data.BotDataManager;
import cn.hycer.carpetbotmanager.model.BotGroup;
import cn.hycer.carpetbotmanager.model.BotPreset;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.*;
import java.util.Collection;

public final class ChatInterface {

    private static final String CBOT = "/cbot ";

    private ChatInterface() {}

    static int showMainMenu(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack src = ctx.getSource();
        src.sendSystemMessage(Component.literal(""));
        src.sendSystemMessage(title("CarpetBotManager"));
        src.sendSystemMessage(Component.literal("")
                .append(btn(" [Bot list] ", CBOT + "ui bots"))
                .append(Component.literal("  "))
                .append(btn(" [Group list] ", CBOT + "ui groups"))
                .append(Component.literal("  "))
                .append(btn(" [Autoload] ", CBOT + "ui autoload")));
        src.sendSystemMessage(Component.literal("")
                .append(btn(" [Add bot] ", CBOT + "ui add"))
                .append(Component.literal("  "))
                .append(btn(" [Help] ", CBOT + "help")));
        return 1;
    }

    static int showBotList(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack src = ctx.getSource();
        BotDataManager dm = BotDataManager.getInstance();
        src.sendSystemMessage(Component.literal(""));
        src.sendSystemMessage(title("Bot List"));
        if (dm.getAllBotPresets().isEmpty()) {
            src.sendSystemMessage(Component.literal("  No saved bots. Use /cbot add <player>"));
        } else for (BotPreset b : dm.getAllBotPresets()) {
            String d = b.getDescription() != null && !b.getDescription().isEmpty()
                    ? " - " + b.getDescription() : "";
            src.sendSystemMessage(Component.literal("  " + b.getName() + d));
            src.sendSystemMessage(Component.literal("    ")
                    .append(btn("[Spawn]", CBOT + "load " + b.getName()))
                    .append(Component.literal(" "))
                    .append(btn("[Delete]", CBOT + "remove " + b.getName())));
        }
        src.sendSystemMessage(Component.literal("").append(back()));
        return 1;
    }

    static int showGroupList(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack src = ctx.getSource();
        BotDataManager dm = BotDataManager.getInstance();
        src.sendSystemMessage(Component.literal(""));
        src.sendSystemMessage(title("Group List"));
        if (dm.getAllBotGroups().isEmpty()) {
            src.sendSystemMessage(Component.literal("  No groups. Use /cbot group add ..."));
        } else for (BotGroup g : dm.getAllBotGroups()) {
            String d = g.getDescription() != null && !g.getDescription().isEmpty()
                    ? " - " + g.getDescription() : "";
            src.sendSystemMessage(Component.literal("  " + g.getName() + d
                    + " [" + String.join(", ", g.getBots()) + "]"));
            src.sendSystemMessage(Component.literal("    ")
                    .append(btn("[Load]", CBOT + "group load " + g.getName()))
                    .append(Component.literal(" "))
                    .append(btn("[Delete]", CBOT + "group remove " + g.getName())));
        }
        src.sendSystemMessage(Component.literal("").append(back()));
        return 1;
    }

    static int showAutoLoad(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack src = ctx.getSource();
        CarpetBotConfig cfg = CarpetBotConfig.getInstance();
        src.sendSystemMessage(Component.literal(""));
        src.sendSystemMessage(title("Autoload Settings"));
        src.sendSystemMessage(Component.literal("  Auto-load Bots:"));
        if (cfg.getAutoLoadBots().isEmpty()) {
            src.sendSystemMessage(Component.literal("    (none)"));
        } else for (String n : cfg.getAutoLoadBots()) {
            src.sendSystemMessage(Component.literal("    " + n + "  ")
                    .append(btn("[Remove]", CBOT + "autoload remove " + n)));
        }
        src.sendSystemMessage(Component.literal("  Auto-load Groups:"));
        if (cfg.getAutoLoadGroups().isEmpty()) {
            src.sendSystemMessage(Component.literal("    (none)"));
        } else for (String n : cfg.getAutoLoadGroups()) {
            src.sendSystemMessage(Component.literal("    " + n + "  ")
                    .append(btn("[Remove]", CBOT + "group autoload remove " + n)));
        }
        src.sendSystemMessage(Component.literal("").append(back()));
        return 1;
    }

    static int showAddHelp(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack src = ctx.getSource();
        src.sendSystemMessage(Component.literal(""));
        src.sendSystemMessage(title("Add Bot"));
        src.sendSystemMessage(Component.literal("  Usage: /cbot add <player> [description]"));
        src.sendSystemMessage(Component.literal("  - Player must be online"));
        src.sendSystemMessage(Component.literal("  - Name must start with 'bot_'"));
        src.sendSystemMessage(Component.literal("  Example: /cbot add bot_miner Mining bot"));
        src.sendSystemMessage(Component.literal("").append(back()));
        return 1;
    }

    static int showAutoLoadAddBot(CommandContext<CommandSourceStack> ctx, String name) {
        CommandSourceStack src = ctx.getSource();
        src.sendSystemMessage(Component.literal(""));
        src.sendSystemMessage(Component.literal("  Add '" + name + "' to auto-load?"));
        src.sendSystemMessage(Component.literal("")
                .append(btn(" [YES] ", CBOT + "autoload add " + name))
                .append(Component.literal("  "))
                .append(btn(" [NO] ", CBOT + "ui bots")));
        return 1;
    }

    static int showAutoLoadAddGroup(CommandContext<CommandSourceStack> ctx, String name) {
        CommandSourceStack src = ctx.getSource();
        src.sendSystemMessage(Component.literal(""));
        src.sendSystemMessage(Component.literal("  Add group '" + name + "' to auto-load?"));
        src.sendSystemMessage(Component.literal("")
                .append(btn(" [YES] ", CBOT + "group autoload add " + name))
                .append(Component.literal("  "))
                .append(btn(" [NO] ", CBOT + "ui groups")));
        return 1;
    }

    private static MutableComponent title(String t) {
        return Component.literal("  ==== " + t + " ====")
                .withStyle(s -> s.withColor(TextColor.fromRgb(0xFFAA00)).withBold(true));
    }

    private static MutableComponent btn(String label, String cmd) {
        return Component.literal(label)
                .withStyle(s -> s
                        .withClickEvent(new ClickEvent.RunCommand(cmd))
                        .withHoverEvent(new HoverEvent.ShowText(Component.literal(cmd)))
                        .withColor(TextColor.fromRgb(0x55FFFF)));
    }

    private static MutableComponent back() {
        return btn(" [Back] ", CBOT + "ui");
    }
}
