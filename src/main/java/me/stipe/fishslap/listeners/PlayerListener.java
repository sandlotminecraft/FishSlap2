package me.stipe.fishslap.listeners;

import me.stipe.fishslap.FSApi;
import me.stipe.fishslap.configs.Translations;
import me.stipe.fishslap.events.ChangeOffhandFishEvent;
import me.stipe.fishslap.events.FishSlapEvent;
import me.stipe.fishslap.managers.PlayerManager;
import me.stipe.fishslap.types.Fish;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // send the spectator scoreboard
        FSApi.getPlayerManager().sendSpectatorScoreboard(event.getPlayer());

        Player p = event.getPlayer();

        if (Fish.isSpecialFish(p.getInventory().getItemInOffHand(), p) && p.getInventory().firstEmpty() >= 0) {
            ItemStack fish = p.getInventory().getItemInOffHand();
            p.getInventory().setItemInOffHand(null);
            p.getInventory().addItem(fish);
            ChangeOffhandFishEvent changeEvent = new ChangeOffhandFishEvent(p, Fish.getFromItemStack(fish, p), null);
            changeEvent.callEvent();
        }

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
            if (offhand.getType() == Material.COD || offhand.getType() == Material.TROPICAL_FISH || offhand.getType() == Material.SALMON || offhand.getType() == Material.PUFFERFISH)
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
    public void updateXpBar(PlayerItemHeldEvent event) {
        Player p = event.getPlayer();
        ItemStack fishItem = p.getInventory().getItem(event.getNewSlot());

        if (fishItem != null) {
            Fish fish = Fish.getFromItemStack(fishItem, p);
            if (fish != null) {
                p.setLevel(fish.getLevel());
                p.setExp((float) fish.getXp() / fish.getFishMeta().getXp());
            }
        }
        else {
            p.setLevel(0);
            p.setExp(0F);
        }
    }

    @EventHandler
    public void onAoeCloud(AreaEffectCloudApplyEvent event) {
        ProjectileSource source = event.getEntity().getSource();
        PlayerManager pm = FSApi.getPlayerManager();

        if (!(source instanceof Player))
            return;

        Player p = (Player) source;

        if (!pm.isPlaying(p)) {
            List<LivingEntity> toRemove = new ArrayList<>();
            for (LivingEntity entity : event.getAffectedEntities()) {
                if (entity instanceof Player)
                    toRemove.add(entity);
            }
            event.getAffectedEntities().removeAll(toRemove);
        } else {
            List<LivingEntity> toRemove = new ArrayList<>();
            for (LivingEntity entity : event.getAffectedEntities()) {
                if (entity instanceof Player && !pm.isPlaying((Player) entity)) {
                    toRemove.add(entity);
                }
            }
            event.getAffectedEntities().removeAll(toRemove);
        }
    }

    @EventHandler //try to prevent splash potions affecting non-players
    public void onPotionSplash(PotionSplashEvent event) {
        ThrownPotion potion = event.getPotion();
        ProjectileSource source = potion.getShooter();
        PlayerManager pm = FSApi.getPlayerManager();

        if (!(source instanceof Player)) {
            return;
        }
        Player p = (Player) source;

        if (!pm.isPlaying(p)) {
            for (LivingEntity entity : event.getAffectedEntities()) {
                if (entity instanceof Player) {
                    event.setIntensity(entity, 0);
                    event.getAffectedEntities().remove(entity);
                }
            }
        } else {
            for (LivingEntity entity : event.getAffectedEntities()) {
                if (entity instanceof Player && !pm.isPlaying((Player) entity)) {
                    event.setIntensity(entity, 0);
                    event.getAffectedEntities().remove(entity);
                }
            }
        }
    }

    // prevent all health regeneration other than from this plugin
    @EventHandler
    public void onHealthRegen(EntityRegainHealthEvent event) {
        if (event.getEntity() instanceof Player)
            if (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.REGEN || event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED)
                event.setCancelled(true);
    }


    @EventHandler
    public void onPlayerDeath(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            Player target = (Player) event.getEntity();
            if (FSApi.getPlayerManager().isDead(target)) {
                event.setCancelled(true);
                return;
            }

            if (target.getHealth() - event.getFinalDamage() <= 0) {
                event.setCancelled(true);
                target.setHealth(0.1);
                target.setInvulnerable(true);
                FSApi.getPlayerManager().setDead(target, true);
                target.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 160, 3));
                target.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 200, 0));
                target.playEffect(EntityEffect.TOTEM_RESURRECT);
                target.getWorld().playSound(target.getLocation(), Sound.ENTITY_PLAYER_DEATH, 1F, 1F);

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
                        FSApi.getPlayerManager().setDead(target, false);
                    }
                }.runTaskLater(FSApi.getPlugin(), 200);

            }
        }
    }

    @EventHandler
    public void stopDeadDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            if (FSApi.getPlayerManager().isDead((Player) event.getDamager()))
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void stopDeadDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            if (FSApi.getPlayerManager().isDead((Player) event.getEntity()))
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void stopDeadInteract(PlayerInteractEvent event) {
        if (FSApi.getPlayerManager().isDead(event.getPlayer()))
            event.setCancelled(true);
    }

    @EventHandler
    public void stopDead(PlayerInteractAtEntityEvent event) {
        if (FSApi.getPlayerManager().isDead(event.getPlayer()))
            event.setCancelled(true);
    }

    @EventHandler
    public void cancelEatingFish(PlayerItemConsumeEvent event) {
        if (Fish.isSpecialFish(event.getItem(), event.getPlayer()))
            event.setCancelled(true);
    }

    @EventHandler
    public void noDropFish(PlayerDropItemEvent event) {
        if (Fish.isSpecialFish(event.getItemDrop().getItemStack(), event.getPlayer()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (Fish.isSpecialFish(event.getPlayer().getInventory().getItemInOffHand(), event.getPlayer())) {
            Player player = event.getPlayer();
            ItemStack fishItem = player.getInventory().getItemInOffHand();

            if (player.getInventory().firstEmpty() >= 0) {
                player.getInventory().setItemInOffHand(null);
                player.getInventory().addItem(fishItem);
                ChangeOffhandFishEvent changeEvent = new ChangeOffhandFishEvent(player, Fish.getFromItemStack(fishItem, player), null);
                changeEvent.callEvent();
            }


        }
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
            Fish fish = Fish.getFromItemStack(slapper.getInventory().getItemInMainHand(), slapper);

            if (fish == null)
                return;

            Player target = (Player) event.getEntity();
            PlayerManager pm = FSApi.getPlayerManager();
            Translations translations = FSApi.getConfigManager().getTranslations();

            if (!pm.isPlaying(slapper)) {
                event.setCancelled(true);
                slapper.sendMessage(ChatColor.translateAlternateColorCodes('&', translations.getMessagePlayerNotPlaying()));
                return;
            }

            if (!pm.isPlaying(target)) {
                event.setCancelled(true);
                slapper.sendMessage(String.format(ChatColor.translateAlternateColorCodes('&', translations.getMessageTargetNotPlaying()), target.getName()));
                return;
            }

            FishSlapEvent fishSlapEvent = new FishSlapEvent(slapper, target, fish, event);
            if (!fishSlapEvent.callEvent())
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void fishChangeByInventoryDrag(InventoryDragEvent event) {
        if (event.getInventorySlots().contains(40) || !(event.getWhoClicked() instanceof Player)) {
            return;
        }

        if (event.getCursor() == null)
            return;

        Player p = (Player) event.getWhoClicked();

        Fish newFish = Fish.getFromItemStack(event.getCursor(), p);

        if (newFish != null) {
            ChangeOffhandFishEvent changeOffhandFishEvent = new ChangeOffhandFishEvent(p, null, newFish);

            if (!changeOffhandFishEvent.callEvent())
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
