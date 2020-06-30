package me.stipe.fishslap;

import me.stipe.fishslap.managers.ConfigManager;
import me.stipe.fishslap.managers.PlayerManager;
import org.bukkit.Bukkit;

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
        configManager = new ConfigManager();
        configManager.loadConfigs();
        playerManager = new PlayerManager();

        Bukkit.getPluginManager().registerEvents(playerManager, fishSlap);
    }
}
