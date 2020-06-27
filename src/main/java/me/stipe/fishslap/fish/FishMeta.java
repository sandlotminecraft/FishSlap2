package me.stipe.fishslap.fish;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public interface FishMeta extends Listener {

    double getDamage();

    double getAttackSpeed();

    double getArmor();

    Enchantment getEnchant();

    int getEnchantLevel();

    PotionEffect getEquipEffect();

    PotionEffect getUseEffect();

    int getCooldownLength();

    ItemStack createItemStack(int level, int startXp);
}
