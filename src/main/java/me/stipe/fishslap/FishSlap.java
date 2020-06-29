package me.stipe.fishslap;

import me.stipe.fishslap.events.GameTickEvent;
import me.stipe.fishslap.listeners.FishingHandler;
import me.stipe.fishslap.listeners.PlayerListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class FishSlap extends JavaPlugin {

    @Override
    public void onEnable() {
        FSApi.initialize(this);

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

}
