package cn.hycer.carpetbotmanager.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CarpetBotConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String CONFIG_FILE = "carpetbotmanager.json";
    private static CarpetBotConfig INSTANCE;

    @SerializedName("permission_level")
    private int permissionLevel = 0;

    @SerializedName("bot_name_prefix")
    private String botNamePrefix = "bot_";

    @SerializedName("auto_load_bots")
    private List<String> autoLoadBots = new ArrayList<>();

    @SerializedName("auto_load_groups")
    private List<String> autoLoadGroups = new ArrayList<>();

    public static CarpetBotConfig getInstance() {
        if (INSTANCE == null) {
            INSTANCE = load();
        }
        return INSTANCE;
    }

    public int getPermissionLevel() {
        return permissionLevel;
    }

    public void setPermissionLevel(int permissionLevel) {
        this.permissionLevel = permissionLevel;
    }

    public String getBotNamePrefix() {
        return botNamePrefix;
    }

    public void setBotNamePrefix(String botNamePrefix) {
        this.botNamePrefix = botNamePrefix;
    }

    public List<String> getAutoLoadBots() {
        return autoLoadBots;
    }

    public List<String> getAutoLoadGroups() {
        return autoLoadGroups;
    }

    public void save() {
        Path configPath = FabricLoader.getInstance().getConfigDir().resolve(CONFIG_FILE);
        try (Writer writer = new OutputStreamWriter(
                Files.newOutputStream(configPath), StandardCharsets.UTF_8)) {
            GSON.toJson(this, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static CarpetBotConfig load() {
        Path configPath = FabricLoader.getInstance().getConfigDir().resolve(CONFIG_FILE);
        if (Files.exists(configPath)) {
            try (Reader reader = new InputStreamReader(
                    Files.newInputStream(configPath), StandardCharsets.UTF_8)) {
                return GSON.fromJson(reader, CarpetBotConfig.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        CarpetBotConfig config = new CarpetBotConfig();
        config.save();
        return config;
    }
}
