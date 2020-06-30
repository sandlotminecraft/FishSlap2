package me.stipe.fishslap.enchants;

import me.stipe.fishslap.types.CustomEnchantment;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

public class Poison extends CustomEnchantment implements Listener {

    public static Enchantment POISON = new Poison();

    public Poison() {
        super("poison", 1, 6);
    }

    @NotNull
    @Override
    public String getName() {
        return "Stinging Poison";
    }

    @Override
    public boolean conflictsWith(@NotNull Enchantment enchantment) {
        return enchantment.equals(Enchantment.FIRE_ASPECT);
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof LivingEntity) {
            ItemStack weapon = ((Player) event.getDamager()).getInventory().getItemInMainHand();

            if (weapon.getEnchantments().containsKey(POISON)) {
                int level = weapon.getEnchantmentLevel(POISON);

                if (10 * level > ThreadLocalRandom.current().nextInt(100)) {
                    int duration = (8 + (2 * level)) * 20;
                    int amplifier = Math.round((float) level / POISON.getMaxLevel() * 3);
                    PotionEffect poison = new PotionEffect(PotionEffectType.POISON, duration, amplifier, true, true, true);
                    LivingEntity target = (LivingEntity) event.getEntity();

                    target.addPotionEffect(poison);
                }
            }
        }
    }
}
