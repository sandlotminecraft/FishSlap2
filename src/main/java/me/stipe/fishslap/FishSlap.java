package me.stipe.fishslap;

import me.stipe.fishslap.events.GameTickEvent;
import me.stipe.fishslap.fish.FishMeta;
import me.stipe.fishslap.listeners.FishingHandler;
import me.stipe.fishslap.listeners.PlayerListener;
import me.stipe.fishslap.managers.ConfigManager;
import me.stipe.fishslap.managers.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class FishSlap extends JavaPlugin {

    private static PlayerManager pm;
    private static ConfigManager config;

    @Override
    public void onEnable() {
        // load managers
        pm = new PlayerManager();
        config = new ConfigManager();

        Bukkit.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new FishingHandler(), this);

        new BukkitRunnable() {
            private long time = 0;

            @Override
            public void run() {
                if (System.currentTimeMillis() >= time) {
                    time = System.currentTimeMillis() + 1000;
                    Bukkit.getPluginManager().callEvent(new GameTickEvent());
                }
            }
        }.runTaskTimer(this, 20L, 1L);
    }

    public static PlayerManager getPlayerManager() {
        return pm;
    }

    public static ConfigManager getConfigManager() {
        return config;
    }

}
