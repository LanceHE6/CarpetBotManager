package cn.hycer.carpetbotmanager;

import cn.hycer.carpetbotmanager.command.BotSpawner;
import cn.hycer.carpetbotmanager.command.CarpetBotCommand;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Carpetbotmanager implements ModInitializer {

    public static final String MOD_ID = "carpetbotmanager";
    private static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        CarpetBotCommand.register();
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            LOGGER.info("[CarpetBotManager] Auto-loading bots...");
            BotSpawner.autoLoad(server.getCommandSource());
        });
    }
}
