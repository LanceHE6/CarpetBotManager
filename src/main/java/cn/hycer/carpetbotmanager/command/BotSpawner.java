package cn.hycer.carpetbotmanager.command;

import cn.hycer.carpetbotmanager.config.CarpetBotConfig;
import cn.hycer.carpetbotmanager.data.BotDataManager;
import cn.hycer.carpetbotmanager.model.BotGroup;
import cn.hycer.carpetbotmanager.model.BotPreset;
import net.minecraft.server.command.ServerCommandSource;

import java.util.Locale;

/**
 * Builds and dispatches Carpet {@code /player spawn} commands,
 * and executes the server-start auto-load sequence.
 */
public final class BotSpawner {

    private BotSpawner() {}

    /**
     * Spawns a single bot via the Carpet {@code /player} command.
     */
    public static void spawn(ServerCommandSource source, BotPreset preset) {
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

    /**
     * Called on server start. Loads all bots and groups listed in the
     * auto-load config.
     */
    public static void autoLoad(ServerCommandSource source) {
        CarpetBotConfig config = CarpetBotConfig.getInstance();
        BotDataManager dataManager = BotDataManager.getInstance();

        for (String botName : config.getAutoLoadBots()) {
            dataManager.getBotPreset(botName).ifPresent(preset -> spawn(source, preset));
        }

        for (String groupName : config.getAutoLoadGroups()) {
            BotGroup group = dataManager.getBotGroup(groupName).orElse(null);
            if (group != null) {
                for (String botName : group.getBots()) {
                    dataManager.getBotPreset(botName).ifPresent(preset -> spawn(source, preset));
                }
            }
        }
    }
}
