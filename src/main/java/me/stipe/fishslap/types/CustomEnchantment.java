package me.stipe.fishslap.types;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public abstract class CustomEnchantment extends Enchantment {

    private final String name;
    private final int startLevel;
    private final int maxLevel;

    public CustomEnchantment(String name, int startLevel, int maxLevel) {
        super(NamespacedKey.minecraft(name.toLowerCase()));
        this.name = name;
        this.startLevel = startLevel;
        this.maxLevel = maxLevel;
    }

    @Override
    public NamespacedKey getKey() {
        return NamespacedKey.minecraft(name.toLowerCase());
    }

    @NotNull
    public String getName() {
        return name;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public int getStartLevel() {
        return startLevel;
    }

    @NotNull
    public EnchantmentTarget getItemTarget() {
        //EnchantmentTarget.ALL was deprecated.  Trying any dummy value.
        return EnchantmentTarget.VANISHABLE;
    }

    public boolean isTreasure() {
        return false;
    }

    public boolean isCursed() {
        return false;
    }

    public boolean conflictsWith(@NotNull Enchantment enchantment) {
        return false;
    }

    public boolean canEnchantItem(@NotNull ItemStack itemStack) {
        Material type = itemStack.getType();

        return type == Material.COD || type == Material.SALMON || type == Material.TROPICAL_FISH || type == Material.PUFFERFISH;
    }

    public String getLore(int level) {
        return ChatColor.GRAY + getName() + " " + Fish.integerToRomanNumeral(level);
    }
}
