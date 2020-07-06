package me.stipe.fishslap.listeners;

import me.stipe.fishslap.FSApi;
import me.stipe.fishslap.events.ChangeOffhandFishEvent;
import me.stipe.fishslap.events.FishSlapEvent;
import me.stipe.fishslap.managers.PlayerManager;
import me.stipe.fishslap.types.Fish;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
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
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.concurrent.ThreadLocalRandom;

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
    public void onPlayerDeath(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            Player damager = (Player) event.getDamager();
            Player target = (Player) event.getEntity();
            if (target.isInvulnerable()) {
                event.setCancelled(true);
                return;
            }

            if (target.getHealth() - event.getFinalDamage() <= 0) {
                event.setCancelled(true);
                target.setHealth(target.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
                target.setInvulnerable(true);

                Vector launchVector = damager.getLocation().getDirection().add(new Vector(0,6,0)).normalize().multiply(5);
                new BukkitRunnable() {
                    int count = 0;
                    long last = System.currentTimeMillis();
                    @Override
                    public void run() {
                        if (System.currentTimeMillis() < last)
                            return;
                        if (count < 2) {
                            target.playSound(target.getLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 1F, 1.5F);
                            count++;
                            last = System.currentTimeMillis() + 500;
                            return;
                        }
                        if (count < 7) {
                            target.playSound(target.getLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 1F, 1.5F);
                            count++;
                            last = System.currentTimeMillis() + 300;
                            return;
                        }
                        if (count < 12) {
                            target.playSound(target.getLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 1F, 1.5F);
                            count++;
                            last = System.currentTimeMillis() + 100;
                            return;
                        }
                        if (count < 18) {
                            target.playSound(target.getLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 1F, 1.5F);
                            count++;
                            last = System.currentTimeMillis() + 50;
                            return;
                        }
                        count++;

                        if (count > 25) {

                            // TODO teleport player above if they are under a tree, etc.

                            target.setVelocity(new Vector(0, 6, 0));
                            target.getWorld().spawnParticle(Particle.SLIME, target.getLocation().add(0, 0.25, 0), 200, 0.7, 0.5, 0.7);
                            target.getWorld().spawnParticle(Particle.EXPLOSION_NORMAL, target.getLocation(), 50, 0.5, 0.25, 0.5);
                            target.playSound(target.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1F, 1F);
                            target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 400, 0));
                            target.removePotionEffect(PotionEffectType.LEVITATION);

                            cancel();
                        }
                    }
                }.runTaskTimer(FSApi.getPlugin(), 0, 1);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        target.setInvulnerable(false);
                    }
                }.runTaskLater(FSApi.getPlugin(), 200);

                target.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 200, 0));
            }
        }
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

            FishSlapEvent fishSlapEvent = new FishSlapEvent(slapper, target, fish, event);
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
