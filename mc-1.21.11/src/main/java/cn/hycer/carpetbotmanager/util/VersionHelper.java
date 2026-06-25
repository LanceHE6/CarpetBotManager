package cn.hycer.carpetbotmanager.util;

import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public final class VersionHelper {

    private VersionHelper() {}

    public static Identifier getDimensionId(World world) {
        return world.getRegistryKey().getValue();
    }
}
