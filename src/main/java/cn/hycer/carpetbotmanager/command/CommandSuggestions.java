package cn.hycer.carpetbotmanager.command;

import cn.hycer.carpetbotmanager.config.CarpetBotConfig;
import cn.hycer.carpetbotmanager.data.BotDataManager;
import cn.hycer.carpetbotmanager.model.BotGroup;
import cn.hycer.carpetbotmanager.model.BotPreset;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.Locale;

/**
 * Suggestion providers for bot names, group names, and auto-load lists.
 */
public final class CommandSuggestions {

    public static final SuggestionProvider<ServerCommandSource> BOT_NAME_SUGGESTIONS =
            (context, builder) -> {
                String remaining = builder.getRemaining().toLowerCase(Locale.ROOT);
                BotDataManager.getInstance().getAllBotPresets().stream()
                        .filter(bot -> bot.getName().toLowerCase(Locale.ROOT).contains(remaining))
                        .forEach(bot -> {
                            String desc = bot.getDescription() != null && !bot.getDescription().isEmpty()
                                    ? bot.getDescription() : null;
                            if (desc != null) {
                                builder.suggest(bot.getName(), Text.literal(desc));
                            } else {
                                builder.suggest(bot.getName());
                            }
                        });
                return builder.buildFuture();
            };

    public static final SuggestionProvider<ServerCommandSource> GROUP_NAME_SUGGESTIONS =
            (context, builder) -> CommandSource.suggestMatching(
                    BotDataManager.getInstance().getAllBotGroups().stream().map(BotGroup::getName),
                    builder);

    public static final SuggestionProvider<ServerCommandSource> AUTOLOAD_BOT_SUGGESTIONS =
            (context, builder) -> CommandSource.suggestMatching(
                    CarpetBotConfig.getInstance().getAutoLoadBots().stream(),
                    builder);

    public static final SuggestionProvider<ServerCommandSource> AUTOLOAD_GROUP_SUGGESTIONS =
            (context, builder) -> CommandSource.suggestMatching(
                    CarpetBotConfig.getInstance().getAutoLoadGroups().stream(),
                    builder);

    private CommandSuggestions() {}
}
