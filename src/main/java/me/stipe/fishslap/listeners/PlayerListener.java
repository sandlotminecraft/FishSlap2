package me.stipe.fishslap.listeners;

import me.stipe.fishslap.FSApi;
import me.stipe.fishslap.abilities.Cod;
import me.stipe.fishslap.events.ChangeOffhandFishEvent;
import me.stipe.fishslap.events.FishSlapEvent;
import me.stipe.fishslap.managers.PlayerManager;
import me.stipe.fishslap.types.Fish;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // send the spectator scoreboard
        FSApi.getPlayerManager().sendSpectatorScoreboard(event.getPlayer());

        Player p = event.getPlayer();

        /* debug */
        {
            for (ItemStack item : p.getInventory().getContents()) {
                if (item != null && (item.getType() == Material.COD || item.getType() == Material.TROPICAL_FISH || item.getType() == Material.SALMON || item.getType() == Material.PUFFERFISH))
                    p.getInventory().removeItem(item);
            }

            for (ItemStack item : p.getInventory().getArmorContents()) {
                if (item != null && (item.getType() == Material.COD || item.getType() == Material.TROPICAL_FISH || item.getType() == Material.SALMON || item.getType() == Material.PUFFERFISH))
                    p.getInventory().removeItem(item);
            }

            ItemStack offhand = p.getInventory().getItemInOffHand();
            if (offhand != null && (offhand.getType() == Material.COD || offhand.getType() == Material.TROPICAL_FISH || offhand.getType() == Material.SALMON || offhand.getType() == Material.PUFFERFISH))
                p.getInventory().removeItem(offhand);

            p.getInventory().addItem(new Fish(Material.COD, 1, 25, p).generateItem());
            p.getInventory().addItem(new Fish(Material.COD, 5, 256, p).generateItem());
            p.getInventory().addItem(new Fish(Material.COD, 10, 1502, p).generateItem());
            p.getInventory().addItem(new Fish(Material.SALMON, 1, 25, p).generateItem());
            p.getInventory().addItem(new Fish(Material.SALMON, 5, 256, p).generateItem());
            p.getInventory().addItem(new Fish(Material.SALMON, 10, 1502, p).generateItem());
            p.getInventory().addItem(new Fish(Material.TROPICAL_FISH, 1, 25, p).generateItem());
            p.getInventory().addItem(new Fish(Material.TROPICAL_FISH, 5, 256, p).generateItem());
            p.getInventory().addItem(new Fish(Material.TROPICAL_FISH, 10, 1502, p).generateItem());
            p.getInventory().addItem(new Fish(Material.PUFFERFISH, 1, 25, p).generateItem());
            p.getInventory().addItem(new Fish(Material.PUFFERFISH, 5, 256, p).generateItem());
            p.getInventory().addItem(new Fish(Material.PUFFERFISH, 10, 1502, p).generateItem());
        }

    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {

    }

    @EventHandler
    public void cancelEatingFish(PlayerItemConsumeEvent event) {

    }

    @EventHandler
    public void noDropFish(PlayerDropItemEvent event) {

    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {

    }

    @EventHandler
    public void keepHungerFull(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            ((Player) event.getEntity()).setFoodLevel(20);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void cancelFallDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player && event.getCause() == EntityDamageEvent.DamageCause.FALL)
            event.setCancelled(true);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {

    }

    @EventHandler
    public void stopFlying(PlayerMoveEvent event) {
        Player p = event.getPlayer();
        PlayerManager pm = FSApi.getPlayerManager();

        // cancel flying if playing
        if (pm.isPlaying(p) && p.isFlying() && p.getGameMode() != GameMode.CREATIVE) {
            p.setFlying(false);
            p.setAllowFlight(false);
        }

    }

    @EventHandler
    public void onFishSlap(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            Player slapper = (Player) event.getDamager();

            if (!Fish.isSpecialFish(slapper.getInventory().getItemInMainHand(), slapper))
                return;

            Fish fish = Fish.getFromItemStack(slapper.getInventory().getItemInMainHand(), slapper);
            Player target = (Player) event.getEntity();
            PlayerManager pm = FSApi.getPlayerManager();

            if (fish == null || !pm.isPlaying(slapper) || !pm.isPlaying(target))
                return;

            FishSlapEvent fishSlapEvent = new FishSlapEvent(slapper, target, fish);
            Bukkit.getPluginManager().callEvent(fishSlapEvent);

            if (fishSlapEvent.isCancelled())
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void fishChangeByInventoryClick(InventoryClickEvent event) {
        if (event.getSlot() != 40 || !(event.getWhoClicked() instanceof Player))
            return;

        Player p = (Player) event.getWhoClicked();
        Fish oldFish = null;
        Fish newFish = null;

        if (event.getCurrentItem() != null)
            oldFish = Fish.getFromItemStack(event.getCurrentItem(), p);
        if (event.getCursor() != null)
            newFish = Fish.getFromItemStack(event.getCursor(), p);

        if (!(newFish == null && oldFish == null)) {
            ChangeOffhandFishEvent changeOffhandFishEvent = new ChangeOffhandFishEvent(p, oldFish, newFish);
            Bukkit.getPluginManager().callEvent(changeOffhandFishEvent);

            if (changeOffhandFishEvent.isCancelled())
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void fishChangeByHotkey(PlayerSwapHandItemsEvent event) {
        Player p = event.getPlayer();
        Fish oldFish = null;
        Fish newFish = null;

        if (event.getOffHandItem() != null)
            newFish = Fish.getFromItemStack(event.getOffHandItem(), p);
        if (event.getMainHandItem() != null)
            oldFish = Fish.getFromItemStack(event.getMainHandItem(), p);

        if (!(newFish == null && oldFish == null)) {
            ChangeOffhandFishEvent changeOffhandFishEvent = new ChangeOffhandFishEvent(p, oldFish, newFish);
            Bukkit.getPluginManager().callEvent(changeOffhandFishEvent);

            if (changeOffhandFishEvent.isCancelled())
                event.setCancelled(true);
        }
    }
}
