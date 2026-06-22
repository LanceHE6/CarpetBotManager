package cn.hycer.carpetbotmanager.command;

import cn.hycer.carpetbotmanager.config.CarpetBotConfig;
import cn.hycer.carpetbotmanager.data.BotDataManager;
import cn.hycer.carpetbotmanager.model.BotGroup;
import cn.hycer.carpetbotmanager.model.BotPreset;
import net.minecraft.commands.CommandSourceStack;
import java.util.Locale;

public final class BotSpawner {

    private BotSpawner() {}

    public static void spawn(CommandSourceStack src, BotPreset preset) {
        String cmd = String.format(Locale.ROOT,
                "player %s spawn at %.2f %.2f %.2f facing %.2f %.2f in %s in survival",
                preset.getName(), preset.getX(), preset.getY(), preset.getZ(),
                preset.getYaw(), preset.getPitch(), preset.getDimension());
        src.getServer().getCommands().performPrefixedCommand(
                src.getServer().createCommandSourceStack(), cmd);
    }

    public static void autoLoad(CommandSourceStack src) {
        CarpetBotConfig cfg = CarpetBotConfig.getInstance();
        BotDataManager dm = BotDataManager.getInstance();
        for (String n : cfg.getAutoLoadBots())
            dm.getBotPreset(n).ifPresent(p -> spawn(src, p));
        for (String gn : cfg.getAutoLoadGroups())
            dm.getBotGroup(gn).ifPresent(g ->
                g.getBots().forEach(bn -> dm.getBotPreset(bn).ifPresent(p -> spawn(src, p))));
    }
}
