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
            config.options().header(commentBlock().concat("\n========================================================================"));
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

    protected String commentBlock() {
        return  "========================================================================\n" +
                "  [" + configFilename + ".yml]\n" +
                "\nThese settings configure the stats for each level of the fish.\n" +
                "You can add more levels by appending more to the end. Make sure\n" +
                "you follow correct YAML syntax or you will get errors and the\n" +
                "plugin will not load.\n" +
                "\n" +
                "damage: the raw damage done by this fish each hit (reduced by armor, etc.)\n" +
                "attackSpeed: the 'attack cooldown' of the fish\n" +
                "\n" +
                "These effects are all granted when the fish is held in the off hand:\n" +
                " armor: the armor granted by this fish\n" +
                " toughness: normally on diamond/netherite, toughness makes armor more effective\n" +
                " knockbackResistance: percent chance the player will resist knockback effects\n" +
                " luck: increases chance of catching/finding better fish/loot\n" +
                " health: increases max health. 2 health = 1 heart\n" +
                " speedBonus: increases the default walk speed by this (percent increase)\n" +
                " equipEffects: potion effects that will be added when equipped\n" +
                "\n" +
                "useEffects: potion effects granted when the fish is used (right click effect)\n" +
                "useEffectDuration: how long the useEffects last (in seconds)\n" +
                "useEffectCooldown: how long the fish will go on cooldown when used (in seconds)\n" +
                "xp: the amount of xp needed for this fish to level up\n" +
                "abilities: the special abilities this fish has (see documentation)\n" +
                "\n";
    }

}
