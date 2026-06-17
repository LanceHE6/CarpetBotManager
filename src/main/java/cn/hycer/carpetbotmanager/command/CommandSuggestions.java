package cn.hycer.carpetbotmanager.command;

import cn.hycer.carpetbotmanager.config.CarpetBotConfig;
import cn.hycer.carpetbotmanager.data.BotDataManager;
import cn.hycer.carpetbotmanager.model.BotGroup;
import cn.hycer.carpetbotmanager.model.BotPreset;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

import java.util.Locale;

/**
 * Suggestion providers for bot names, group names, and auto-load lists.
 */
public final class CommandSuggestions {

    public static final SuggestionProvider<CommandSourceStack> BOT_NAME_SUGGESTIONS =
            (context, builder) -> {
                String remaining = builder.getRemaining().toLowerCase(Locale.ROOT);
                BotDataManager.getInstance().getAllBotPresets().stream()
                        .filter(bot -> bot.getName().toLowerCase(Locale.ROOT).contains(remaining))
                        .forEach(bot -> {
                            if (bot.getDescription() != null && !bot.getDescription().isEmpty()) {
                                builder.suggest(bot.getName(), Component.literal(bot.getDescription()));
                            } else {
                                builder.suggest(bot.getName());
                            }
                        });
                return builder.buildFuture();
            };

    public static final SuggestionProvider<CommandSourceStack> GROUP_NAME_SUGGESTIONS =
            (context, builder) -> {
                String remaining = builder.getRemaining().toLowerCase(Locale.ROOT);
                BotDataManager.getInstance().getAllBotGroups().stream()
                        .filter(g -> g.getName().toLowerCase(Locale.ROOT).contains(remaining))
                        .forEach(g -> builder.suggest(g.getName()));
                return builder.buildFuture();
            };

    public static final SuggestionProvider<CommandSourceStack> AUTOLOAD_BOT_SUGGESTIONS =
            (context, builder) -> {
                String remaining = builder.getRemaining().toLowerCase(Locale.ROOT);
                CarpetBotConfig.getInstance().getAutoLoadBots().stream()
                        .filter(s -> s.toLowerCase(Locale.ROOT).contains(remaining))
                        .forEach(s -> builder.suggest(s));
                return builder.buildFuture();
            };

    public static final SuggestionProvider<CommandSourceStack> AUTOLOAD_GROUP_SUGGESTIONS =
            (context, builder) -> {
                String remaining = builder.getRemaining().toLowerCase(Locale.ROOT);
                CarpetBotConfig.getInstance().getAutoLoadGroups().stream()
                        .filter(s -> s.toLowerCase(Locale.ROOT).contains(remaining))
                        .forEach(s -> builder.suggest(s));
                return builder.buildFuture();
            };

    private CommandSuggestions() {}
}
