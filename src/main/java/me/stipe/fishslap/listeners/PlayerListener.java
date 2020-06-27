package me.stipe.fishslap.listeners;

import me.stipe.fishslap.FishSlap;
import me.stipe.fishslap.events.ChangeOffhandFishEvent;
import me.stipe.fishslap.events.FishSlapEvent;
import me.stipe.fishslap.fish.Fish;
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
        PlayerManager pm = FishSlap.getPlayerManager();

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
            Player target = (Player) event.getEntity();
            Fish fish = new Fish(slapper, slapper.getInventory().getItemInMainHand());
            PlayerManager pm = FishSlap.getPlayerManager();

            if (fish.getType() == Fish.FishType.INVALID || !pm.isPlaying(slapper) || !pm.isPlaying(target))
                return;

            FishSlapEvent slapEvent = new FishSlapEvent(slapper, target, fish, event);
            Bukkit.getPluginManager().callEvent(slapEvent);

            fish.addXp(slapEvent.getXp());
            pm.updateScores(slapEvent);
        }
    }

    @EventHandler
    public void fishChangeByInventoryClick(InventoryClickEvent event) {

    }

    @EventHandler
    public void fishChangeByHotkey(PlayerSwapHandItemsEvent event) {
        Player p = event.getPlayer();
        Fish newFish = new Fish(p, event.getOffHandItem());
        Fish oldFish = new Fish(p, event.getMainHandItem());

        if (oldFish.isOnCooldown()) {
            event.setCancelled(true);
            p.sendMessage("You cannot remove that while it is on cooldown.");
            return;
        }

        if (newFish.isValid() || oldFish.isValid()) {
            ChangeOffhandFishEvent changeOffhandFishEvent = new ChangeOffhandFishEvent(p, oldFish, newFish);
            Bukkit.getPluginManager().callEvent(changeOffhandFishEvent);
        }
    }
}
