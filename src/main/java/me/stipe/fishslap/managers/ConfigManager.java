package me.stipe.fishslap.managers;

import me.stipe.fishslap.FishSlap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigManager {
    private final int maxFishLevel;

    private final String codDisplayName;
    private final double codBaseDamage;
    private final double codDamagePerLevel;
    private final double codBaseArmor;
    private final double codArmorPerLevel;
    private final Enchantment codEnchant;
    private final List<Integer> codEnchantUpgradeLevels = new ArrayList<>();
    private final PotionEffectType codEquipEffect;
    private final List<Integer> codEquipEffectUpgradeLevels = new ArrayList<>();
    private final PotionEffectType codUseEffect;
    private final List<Integer> codUseEffectUpgradeLevels = new ArrayList<>();
    private final Map<Integer, Integer> codUseEffectDurations = new HashMap<>();
    private final Map<Integer, Integer> codUseEffectCooldowns = new HashMap<>();

    public ConfigManager() {
        Plugin fishSlap = Bukkit.getPluginManager().getPlugin("FishSlap");
        File file = new File(fishSlap.getDataFolder().toString() + "/config.yml");
        ConfigurationSection config;

        if (!file.exists()) {
            fishSlap.saveResource("config.yml", false);
            System.out.println("[FishSlap] Creating default file: config.yml");
        }

        config = YamlConfiguration.loadConfiguration(file);
        maxFishLevel = config.getInt("Max Fish Level");
        codDisplayName = ChatColor.translateAlternateColorCodes('&', config.getString("Fish.Cod.Display Name"));
        codBaseDamage = config.getDouble("Fish.Cod.Base Damage");
        codDamagePerLevel = config.getDouble("Fish.Cod.Additional Damage per Level");
        codBaseArmor = config.getDouble("Fish.Cod.Base Armor");
        codArmorPerLevel = config.getDouble("Fish.Cod.Additional Armor per Level");
        codEnchant = Enchantment.getByKey(NamespacedKey.minecraft(config.getString("Fish.Cod.Enchantment").toLowerCase()));
        for (String s : config.getStringList("Fish.Cod.Enchant Upgrade Levels"))
            codEnchantUpgradeLevels.add(Integer.parseInt(s));
        codEquipEffect = PotionEffectType.getByName(config.getString("Fish.Cod.Equip Effect"));
        for (String s : config.getStringList("Fish.Cod.Equip Effect Upgrade Levels"))
            codEquipEffectUpgradeLevels.add(Integer.parseInt(s));
        codUseEffect = PotionEffectType.getByName(config.getString("Fish.Cod.Use Effect"));
        for (String s : config.getStringList("Fish.Cod.Use Effect Upgrade Levels"))
            codUseEffectUpgradeLevels.add(Integer.parseInt(s));
        for (String s : config.getConfigurationSection("Fish.Cod.Use Effect Durations").getKeys(true))
            codUseEffectDurations.put(Integer.parseInt(s), config.getInt("Fish.Cod.Use Effect Durations." + s));
        for (String s : config.getConfigurationSection("Fish.Cod.Use Effect Cooldowns").getKeys(true))
            codUseEffectDurations.put(Integer.parseInt(s), config.getInt("Fish.Cod.Use Effect Cooldowns." + s));

    }

    public int getMaxFishLevel() {
        return maxFishLevel;
    }

    public String getCodDisplayName() {
        return codDisplayName;
    }

    public double getCodBaseDamage() {
        return codBaseDamage;
    }

    public double getCodDamagePerLevel() {
        return codDamagePerLevel;
    }

    public double getCodBaseArmor() {
        return codBaseArmor;
    }

    public double getCodArmorPerLevel() {
        return codArmorPerLevel;
    }

    public List<Integer> getCodEnchantUpgradeLevels() {
        return codEnchantUpgradeLevels;
    }

    public PotionEffectType getCodEquipEffect() {
        return codEquipEffect;
    }

    public List<Integer> getCodEquipEffectUpgradeLevels() {
        return codEquipEffectUpgradeLevels;
    }

    public PotionEffectType getCodUseEffect() {
        return codUseEffect;
    }

    public List<Integer> getCodUseEffectUpgradeLevels() {
        return codUseEffectUpgradeLevels;
    }

    public Map<Integer, Integer> getCodUseEffectDurations() {
        return codUseEffectDurations;
    }

    public Map<Integer, Integer> getCodUseEffectCooldowns() {
        return codUseEffectCooldowns;
    }

    public Enchantment getCodEnchant() {
        return codEnchant;
    }
}
