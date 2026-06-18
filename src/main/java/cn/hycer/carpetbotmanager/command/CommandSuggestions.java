package cn.hycer.carpetbotmanager.command;

import cn.hycer.carpetbotmanager.config.CarpetBotConfig;
import cn.hycer.carpetbotmanager.data.BotDataManager;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import java.util.Locale;

public final class CommandSuggestions {

    public static final SuggestionProvider<ServerCommandSource> BOT_NAME_SUGGESTIONS =
            (ctx, b) -> {
                String r = b.getRemaining().toLowerCase(Locale.ROOT);
                BotDataManager.getInstance().getAllBotPresets().stream()
                        .filter(p -> p.getName().toLowerCase(Locale.ROOT).contains(r))
                        .forEach(p -> b.suggest(p.getName(),
                                p.getDescription() != null && !p.getDescription().isEmpty()
                                        ? Text.literal(p.getDescription()) : null));
                return b.buildFuture();
            };

    public static final SuggestionProvider<ServerCommandSource> GROUP_NAME_SUGGESTIONS =
            (ctx, b) -> CommandSource.suggestMatching(
                    BotDataManager.getInstance().getAllBotGroups().stream()
                            .map(g -> g.getName()), b);

    public static final SuggestionProvider<ServerCommandSource> AUTOLOAD_BOT_SUGGESTIONS =
            (ctx, b) -> CommandSource.suggestMatching(
                    CarpetBotConfig.getInstance().getAutoLoadBots().stream(), b);

    public static final SuggestionProvider<ServerCommandSource> AUTOLOAD_GROUP_SUGGESTIONS =
            (ctx, b) -> CommandSource.suggestMatching(
                    CarpetBotConfig.getInstance().getAutoLoadGroups().stream(), b);

    private CommandSuggestions() {}
}
