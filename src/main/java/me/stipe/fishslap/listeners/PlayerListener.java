package me.stipe.fishslap.listeners;

import me.stipe.fishslap.FSApi;
import me.stipe.fishslap.FishSlap;
import me.stipe.fishslap.abilities.Cod;
import me.stipe.fishslap.events.ChangeOffhandFishEvent;
import me.stipe.fishslap.events.FishSlapEvent;
import me.stipe.fishslap.managers.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;

public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // send the spectator scoreboard
        FSApi.getPlayerManager().sendSpectatorScoreboard(event.getPlayer());

        Player p = event.getPlayer();
        Cod cod = new Cod(p);

        p.getInventory().addItem(cod.generateItem(1, 0));
        p.getInventory().addItem(cod.generateItem(5, 200));
        p.getInventory().addItem(cod.generateItem(10, 1900));

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
    }

    @EventHandler
    public void fishChangeByInventoryClick(InventoryClickEvent event) {
    }

    @EventHandler
    public void fishChangeByHotkey(PlayerSwapHandItemsEvent event) {
    }
}
