package me.stipe.fishslap.enchants;

import me.stipe.fishslap.types.CustomEnchantment;
import me.stipe.fishslap.types.Fish;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;

public class HealingTouch extends CustomEnchantment implements Listener {

    public static Enchantment HEALING_TOUCH = new HealingTouch();

    public HealingTouch() {
        super("healing_touch", 1, 1);
    }

    @NotNull
    @Override
    public String getName() {
        return "Healing Touch";
    }

    @EventHandler
    public void onHitPlayer(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof LivingEntity) {
            Fish fish = Fish.getFromItemStack(((Player) event.getDamager()).getInventory().getItemInMainHand(), (Player) event.getDamager());

            if (fish == null)
                return;

            if (fish.getFishMeta().getEnchantments().containsKey(HEALING_TOUCH)) {
                Player player = (Player) event.getDamager();
                LivingEntity target = (LivingEntity) event.getEntity();

                if (player.hasCooldown(fish.generateItem().getType())) {
                    event.setCancelled(true);
                    return;
                }

                double health = target.getHealth();
                health += fish.getFishMeta().getDamage();
                if (player.getAttribute(Attribute.GENERIC_MAX_HEALTH) == null)
                    return;
                double maxHealth = target.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
                if (health > maxHealth)
                    health = maxHealth;

                event.setCancelled(true);
                target.setHealth(health);
                double cooldown = (float) 1 / fish.getFishMeta().getAttackSpeed() * 20;

                player.setCooldown(fish.generateItem().getType(), (int) cooldown);

                target.getWorld().playSound(target.getLocation(), Sound.ENTITY_BOAT_PADDLE_WATER, 1F, 1F);
                target.getWorld().playSound(target.getLocation(), Sound.ENTITY_ILLUSIONER_CAST_SPELL, 0.5F, 5F);
                target.getWorld().spawnParticle(Particle.FALLING_WATER, target.getLocation().add(0, 1, 0), 200, 0.5, 1, 0.5);
            }
        }
    }

}
