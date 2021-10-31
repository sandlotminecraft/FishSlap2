package me.stipe.fishslap;

import me.stipe.fishslap.configs.Translations;
import me.stipe.fishslap.enchants.HealingTouch;
import me.stipe.fishslap.enchants.Poison;
import me.stipe.fishslap.events.GameTickEvent;
import me.stipe.fishslap.listeners.FishingHandler;
import me.stipe.fishslap.listeners.PlayerListener;
import me.stipe.fishslap.types.Fish;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.Material;
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
        Bukkit.getServer().getPluginManager().registerEvents(new HealingTouch(), this);

        try {
            Field f = Enchantment.class.getDeclaredField("acceptingNew");
            f.setAccessible(true);
            f.set(null, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // TODO load all enchantments in the package automatically and register their listeners
        Enchantment.registerEnchantment(new Poison());
        Enchantment.registerEnchantment(new HealingTouch());
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

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("givefish")) {
            Translations trans = FSApi.getConfigManager().getTranslations();
            Player p = null;
            if (sender instanceof Player) {
                p = (Player) sender;
            }
            if (args.length > 3 || args.length < 2) {
                sender.sendMessage(String.format(trans.getGiveFishFailed(), "must provide 2 or 3 arguments."));
                return false;
            }

            Material type = Material.COD;

            switch (args[0].toLowerCase()) {
                case "cod":
                    type = Material.COD;
                    break;
                case "salmon":
                    type = Material.SALMON;
                    break;
                case "tropical":
                case "clownfish":
                case "clown":
                    type = Material.TROPICAL_FISH;
                    break;
                case "puffer":
                case "pufferfish":
                    type = Material.PUFFERFISH;
                default:
                    sender.sendMessage(String.format(trans.getGiveFishFailed(), "no valid fish type provided."));
                    return false;
            }

            //level from arguments
            int level;
            if (args[1] == null) {
                sender.sendMessage(String.format(trans.getGiveFishFailed(), "no arguments were provided."));
                return false;
            }
            try {
                level = Integer.parseInt(args[1]);
            } catch (NumberFormatException nfe) {
                sender.sendMessage(String.format(trans.getGiveFishFailed(), "level must be an integer."));
                return false;
            }
            if (level < 1 || level > 10) {
                sender.sendMessage(String.format(trans.getGiveFishFailed(), "level must be 1-10."));
                return false;
            }

            //handle the optional player argument (required if sent from console)
            if (sender instanceof ConsoleCommandSender && args.length != 3) {
                sender.sendMessage(String.format(trans.getGiveFishFailed(), "command issued on console, and no target player specified."));
                return false;
            }
            Player t;
            if (sender instanceof Player && args.length == 2) {
                t = p;
            } else {
                t = Bukkit.getPlayerExact(args[2]);
            }
            if (t == null) {
                sender.sendMessage(String.format(trans.getGiveFishFailed(), "target player not found"));
                return false;
            }

            if (t.getInventory().firstEmpty() == -1) {
                sender.sendMessage(String.format(trans.getActionBarPowerupCooldown(), t.getName()));
                return false;
            } else {
                int xp = level * 15;
                t.getInventory().addItem(new Fish(type, level, xp, t).generateItem());
                return true;
            }
        }
        return false;
    }
}
