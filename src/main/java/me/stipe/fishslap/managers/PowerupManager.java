package me.stipe.fishslap.managers;

import me.stipe.fishslap.FSApi;
import me.stipe.fishslap.FishSlap;
import me.stipe.fishslap.events.GameTickEvent;
import me.stipe.fishslap.types.Fish;
import me.stipe.fishslap.types.Powerup;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PowerupManager implements Listener {
    private final Map<Powerup, Fish> activePowerups;
    private final Map<NamespacedKey, Powerup> availablePowerups;

    public PowerupManager() {
        activePowerups = new HashMap<>();
        availablePowerups = new HashMap<>();

        load();

        for (Player p : Bukkit.getOnlinePlayers()) {
            Fish fish = Fish.getFromItemStack(p.getInventory().getItemInOffHand(), p);

            // check the offhand for a fish
            if (fish != null && fish.getFishItem() != null && fish.getFishItem().getItemMeta() != null) {
                if (!fish.getFishItem().getItemMeta().getPersistentDataContainer().isEmpty()) {
                    for (NamespacedKey key : fish.getFishItem().getItemMeta().getPersistentDataContainer().getKeys()) {
                        if (availablePowerups.containsKey(key))
                            activePowerups.put(availablePowerups.get(key), fish);
                    }
                }
            }

            // iterate through the inventory looking for fish
            for (ItemStack item : p.getInventory().getContents()) {
                if (item != null) {
                    fish = Fish.getFromItemStack(item, p);

                    if (fish != null && fish.getFishItem() != null && fish.getFishItem().getItemMeta() != null) {
                        if (!fish.getFishItem().getItemMeta().getPersistentDataContainer().isEmpty()) {
                            for (NamespacedKey key : fish.getFishItem().getItemMeta().getPersistentDataContainer().getKeys()) {
                                if (availablePowerups.containsKey(key))
                                    activePowerups.put(availablePowerups.get(key), fish);
                            }
                        }
                    }
                }
            }
        }
    }

    public void removeActivePowerup(Powerup powerup, Fish fish) {
        activePowerups.remove(powerup, fish);
    }

    public Map<Powerup, Fish> getActivePowerups() {
        return activePowerups;
    }

    public Map<NamespacedKey, Powerup> getAvailablePowerups() {
        return availablePowerups;
    }

    private void load() {
        FishSlap plugin = FSApi.getPlugin();
        File file = new File(plugin.getDataFolder().toString() + "/powerups.yml");

        if (!file.exists()) {
            if (file.getParentFile().exists() || file.getParentFile().mkdirs()) {
                plugin.saveResource("powerups.yml", false);
            }
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        for (String s : config.getKeys(false)) {
            Powerup powerup = new Powerup(config.getConfigurationSection(s));
            availablePowerups.put(powerup.getKey(), powerup);
        }
    }

    @EventHandler
    public void onGameTick(GameTickEvent event) {
        List<Powerup> toRemove = new ArrayList<>();
        for (Powerup powerup : activePowerups.keySet()) {
            if (!activePowerups.get(powerup).getOwner().isOnline())
                return;

            if (powerup.tick()) {
                powerup.removePowerup(activePowerups.get(powerup));
                activePowerups.get(powerup).updateLore();
                toRemove.add(powerup);
            }

            if (activePowerups.get(powerup).isInMainHand() || activePowerups.get(powerup).isEquipped())
                powerup.showCooldownOnActionBar(activePowerups.get(powerup));
        }
        for (Powerup pup : toRemove) {
            activePowerups.remove(pup);
        }
    }

    @EventHandler
    public void onShiftClick(InventoryClickEvent event) {

        if (event.getWhoClicked() instanceof Player && (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.CREATIVE)) {
            Player p = (Player) event.getWhoClicked();
            ItemStack powerupItem = event.getCursor();

            if (powerupItem == null || event.getCurrentItem() == null || powerupItem.getItemMeta() == null || powerupItem.getItemMeta().getPersistentDataContainer().isEmpty())
                return;
            Fish fish = Fish.getFromItemStack(event.getCurrentItem(), p);

            if (fish == null)
                return;

            Powerup powerup = null;
            for (Powerup pup : FSApi.getPowerupManager().getAvailablePowerups().values()) {
                for (NamespacedKey key : powerupItem.getItemMeta().getPersistentDataContainer().getKeys())
                    if (key.equals(pup.getKey())) {
                        powerup = pup;
                        break;
                    }
            }

            if (powerup != null) {
                event.setCancelled(true);
                powerup.applyPowerup(fish);
                p.setItemOnCursor(null);
                activePowerups.put(powerup, fish);
                p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_USE, 1F, 1F);
            }
        }
    }
}
