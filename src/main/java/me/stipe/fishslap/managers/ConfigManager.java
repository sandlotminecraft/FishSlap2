package me.stipe.fishslap.managers;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class ConfigManager {
    private final int maxFishLevel = 10;
    private final int xpNeededPerLevel = 1000;

    private final String codDisplayName = "";
    private final double codBaseDamage = 0;
    private final double codBonusDamage = 0;
    private final double codBaseArmor = 0;
    private final double codBonusArmor = 0;

    public int getMaxFishLevel() {
        return maxFishLevel;
    }
}
