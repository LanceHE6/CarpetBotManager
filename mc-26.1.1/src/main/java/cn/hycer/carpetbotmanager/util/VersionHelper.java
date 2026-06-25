package cn.hycer.carpetbotmanager.util;

import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;

public final class VersionHelper {

    private VersionHelper() {}

    public static Identifier getDimensionId(Level level) {
        return level.dimension().identifier();
    }
}
