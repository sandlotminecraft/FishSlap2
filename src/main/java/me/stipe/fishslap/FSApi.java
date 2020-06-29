package me.stipe.fishslap;

import me.stipe.fishslap.managers.ConfigManager;
import me.stipe.fishslap.managers.PlayerManager;

public class FSApi {
    private static FishSlap fishSlap;
    private static PlayerManager playerManager;
    private static ConfigManager configManager;

    public static FishSlap getPlugin() {
        return fishSlap;
    }

    public static PlayerManager getPlayerManager() {
        return playerManager;
    }

    public static ConfigManager getConfigManager() {
        return configManager;
    }

    public static void initialize(FishSlap pluginInstance) {
        fishSlap = pluginInstance;
        playerManager = new PlayerManager();
        configManager = new ConfigManager();
        configManager.loadConfigs();
    }
}
