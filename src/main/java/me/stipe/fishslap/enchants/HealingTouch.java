package me.stipe.fishslap.enchants;

import me.stipe.fishslap.events.FishSlapEvent;
import me.stipe.fishslap.types.CustomEnchantment;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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
    public void onHitPlayer(FishSlapEvent event) {
        if (event.getFish().getFishMeta().getEnchantments().containsKey(HEALING_TOUCH)) {
            Player target = event.getTarget();
            Player player = event.getSlapper();

            double health = target.getHealth();
            health += event.getFish().getFishMeta().getDamage();
            if (player.getAttribute(Attribute.GENERIC_MAX_HEALTH) == null)
                return;
            double maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
            if (health > maxHealth)
                health = maxHealth;

            event.getDamageEvent().setDamage(health * -1);

            target.getWorld().playSound(target.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1F, 2F);
            target.getWorld().spawnParticle(Particle.FALLING_WATER, target.getLocation().add(0, 1, 0), 100, 0, 1, 0);
        }
    }

}
