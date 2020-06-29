package me.stipe.fishslap;

import me.stipe.fishslap.enchants.Poison;
import me.stipe.fishslap.events.GameTickEvent;
import me.stipe.fishslap.listeners.FishingHandler;
import me.stipe.fishslap.listeners.PlayerListener;
import me.stipe.fishslap.types.Fish;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;

public class FishSlap extends JavaPlugin {

    @Override
    public void onEnable() {
        FSApi.initialize(this);

        Bukkit.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new FishingHandler(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new Fish(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new Poison(), this);

        try {
            Field f = Enchantment.class.getDeclaredField("acceptingNew");
            f.setAccessible(true);
            f.set(null, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Enchantment.registerEnchantment(new Poison());
        Enchantment.stopAcceptingRegistrations();

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
