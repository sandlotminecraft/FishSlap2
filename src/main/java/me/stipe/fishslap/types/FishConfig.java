package me.stipe.fishslap.types;

import me.stipe.fishslap.FSApi;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class FishConfig {

    protected Map<Integer, FishMeta> fishStats = new HashMap<>();
    protected String displayName;
    protected String configFilename;
    protected YamlConfiguration config;
    protected int maxLevel;
    private final File configFile;

    public FishConfig(String configFilename) {
        this.configFilename = configFilename;
        configFile = new File(FSApi.getPlugin().getDataFolder(), configFilename + ".yml");
        config = new YamlConfiguration();
    }

    public void load() {
        if (!configFile.exists()) {
            if (configFile.getParentFile().exists() || configFile.getParentFile().mkdirs())
                save();
        }

        config = YamlConfiguration.loadConfiguration(configFile);

        displayName = config.getString("Display Name");

        ConfigurationSection levelDataSection = config.getConfigurationSection("Levels");
        if (levelDataSection == null) {
            System.out.println("[FishSlap] ERROR: Unable to load level data from file: " + configFilename + ".yml");
            return;
        }
        for (String level : levelDataSection.getKeys(false)) {
            ConfigurationSection levelData = levelDataSection.getConfigurationSection(level);

            if (levelData == null)
                continue;

            fishStats.put(Integer.parseInt(level), new FishMeta(levelData));
            maxLevel = Integer.parseInt(level);
        }
    }

    public void save() {
        try {
            if (!configFile.exists()) {
                configFile.getParentFile().mkdirs();
                configFile.createNewFile();
            }
            config.set("Display Name", displayName);
            config.createSection("Levels");
            for (int level : fishStats.keySet()) {
                fishStats.get(level).createConfigSection(config.createSection("Levels." + level));
            }
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FishMeta getLevelData(int level) {
        return fishStats.get(level);
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public String getDisplayName() {
        return ChatColor.translateAlternateColorCodes('&', displayName);
    }

}
