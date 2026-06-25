package cn.hycer.carpetbotmanager.data;

import cn.hycer.carpetbotmanager.model.BotGroup;
import cn.hycer.carpetbotmanager.model.BotPreset;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class BotDataManager {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String BOTS_FILE = "carpetbotmanager_bots.json";
    private static final String GROUPS_FILE = "carpetbotmanager_groups.json";
    private static BotDataManager INSTANCE;

    private final Map<String, BotPreset> bots = new LinkedHashMap<>();
    private final Map<String, BotGroup> groups = new LinkedHashMap<>();

    private final Path botsPath;
    private final Path groupsPath;

    private BotDataManager() {
        Path configDir = FabricLoader.getInstance().getConfigDir();
        this.botsPath = configDir.resolve(BOTS_FILE);
        this.groupsPath = configDir.resolve(GROUPS_FILE);
        loadBots();
        loadGroups();
    }

    public static BotDataManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new BotDataManager();
        }
        return INSTANCE;
    }

    // === Bot Presets ===

    public void addBotPreset(BotPreset preset) {
        bots.put(preset.getName(), preset);
        saveBots();
    }

    public void removeBotPreset(String name) {
        bots.remove(name);
        // Also remove from any groups that reference this bot
        for (BotGroup group : groups.values()) {
            group.getBots().remove(name);
        }
        saveBots();
        saveGroups();
    }

    public Optional<BotPreset> getBotPreset(String name) {
        return Optional.ofNullable(bots.get(name));
    }

    public Collection<BotPreset> getAllBotPresets() {
        return Collections.unmodifiableCollection(bots.values());
    }

    public boolean hasBotPreset(String name) {
        return bots.containsKey(name);
    }

    // === Bot Groups ===

    public void addBotGroup(BotGroup group) {
        groups.put(group.getName(), group);
        saveGroups();
    }

    public void removeBotGroup(String name) {
        groups.remove(name);
        saveGroups();
    }

    public Optional<BotGroup> getBotGroup(String name) {
        return Optional.ofNullable(groups.get(name));
    }

    public Collection<BotGroup> getAllBotGroups() {
        return Collections.unmodifiableCollection(groups.values());
    }

    public boolean hasBotGroup(String name) {
        return groups.containsKey(name);
    }

    // === Persistence ===

    private void saveBots() {
        saveJson(botsPath, bots);
    }

    private void saveGroups() {
        saveJson(groupsPath, groups);
    }

    private void loadBots() {
        Map<String, BotPreset> loaded = loadJson(botsPath, new TypeToken<Map<String, BotPreset>>(){}.getType());
        if (loaded != null) {
            bots.putAll(loaded);
        }
    }

    private void loadGroups() {
        Map<String, BotGroup> loaded = loadJson(groupsPath, new TypeToken<Map<String, BotGroup>>(){}.getType());
        if (loaded != null) {
            groups.putAll(loaded);
        }
    }

    private void saveJson(Path path, Object data) {
        try (Writer writer = new OutputStreamWriter(
                Files.newOutputStream(path), StandardCharsets.UTF_8)) {
            GSON.toJson(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private <T> T loadJson(Path path, Type type) {
        if (Files.exists(path)) {
            try (Reader reader = new InputStreamReader(
                    Files.newInputStream(path), StandardCharsets.UTF_8)) {
                return GSON.fromJson(reader, type);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
