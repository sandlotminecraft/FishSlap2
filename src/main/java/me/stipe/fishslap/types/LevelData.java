package me.stipe.fishslap.types;

import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class LevelData {
    private double damage;
    private double armor;
    private double speed;
    private List<String> enchantmentStrings;
    private List<String> equipEffectStrings;
    private List<String> useEffectStrings;
    private int useEffectDuration;
    private int useEffectCooldown;
    private int xp;

    public LevelData(double damage, double armor, double speed, String[] enchantmentStrings, String[] equipEffectStrings, String[] useEffectStrings, int useEffectDuration, int useEffectCooldown, int xp) {
        this.damage = damage;
        this.armor = armor;
        this.speed = speed;
        this.enchantmentStrings = Arrays.asList(enchantmentStrings);
        this.equipEffectStrings = Arrays.asList(equipEffectStrings);
        this.useEffectStrings = Arrays.asList(useEffectStrings);
        this.useEffectDuration = useEffectDuration;
        this.useEffectCooldown = useEffectCooldown;
        this.xp = xp;
    }

    public LevelData(ConfigurationSection configData) {
        damage = configData.getDouble("damage");
        armor = configData.getDouble("armor");
        speed = configData.getDouble("speed");
        enchantmentStrings = configData.getStringList("enchantments");
        equipEffectStrings = configData.getStringList("equipEffects");
        useEffectStrings = configData.getStringList("useEffects");
        useEffectDuration = configData.getInt("useEffectDuration");
        useEffectCooldown = configData.getInt("useEffectCooldown");
        xp = configData.getInt("xp");
    }

    public void createConfigSection(ConfigurationSection levelData) {
        levelData.set("damage", damage);
        levelData.set("armor", armor);
        levelData.set("speed", speed);
        levelData.set("enchantments", enchantmentStrings);
        levelData.set("equipEffects", equipEffectStrings);
        levelData.set("useEffects", useEffectStrings);
        levelData.set("useEffectDuration", useEffectDuration);
        levelData.set("useEffectCooldown", useEffectCooldown);
        levelData.set("xp", xp);
    }

    public double getSpeed() {
        return speed;
    }

    public Map<Enchantment, Integer> getEnchantments() {
        Map<Enchantment, Integer> enchants = new HashMap<>();

        for (String s : enchantmentStrings) {
            // TODO add some checks here
            Enchantment enchant = Enchantment.getByKey(NamespacedKey.minecraft(s.split(" ")[0].toLowerCase()));
            int level = Integer.parseInt(s.split(" ")[1]);

            enchants.put(enchant, level);
        }
        return enchants;
    }

    public Set<PotionEffect> getEquipEffects() {
        Set<PotionEffect> effects = new HashSet<>();

        for (String s : equipEffectStrings) {
            // TODO more checks here
            PotionEffectType type = PotionEffectType.getByName(s.split(" ")[0]);
            int amplifier = Integer.parseInt(s.split(" ")[1]);

            if (type == null)
                continue;
            PotionEffect potionEffect = new PotionEffect(type, Integer.MAX_VALUE, amplifier, false, false);
            effects.add(potionEffect);
        }
        return effects;
    }

    public Set<PotionEffect> getUseEffects() {
        Set<PotionEffect> effects = new HashSet<>();

        for (String s : useEffectStrings) {
            // TODO more checks here
            PotionEffectType type = PotionEffectType.getByName(s.split(" ")[0]);
            int amplifier = Integer.parseInt(s.split(" ")[1]);

            if (type == null)
                continue;
            PotionEffect potionEffect = new PotionEffect(type, useEffectDuration, amplifier, false, false);
            effects.add(potionEffect);
        }
        return effects;
    }

    public double getAttackSpeedModifier() {
        return speed - 4;
    }

    public double getDamage() {
        return damage;
    }

    public double getDamageModifier() {
        return damage - 1;
    }

    public double getArmor() {
        return armor;
    }

    public List<String> getEnchantmentStrings() {
        return enchantmentStrings;
    }

    public List<String> getEquipEffectStrings() {
        return equipEffectStrings;
    }

    public List<String> getUseEffectStrings() {
        return useEffectStrings;
    }

    public int getUseEffectDuration() {
        return useEffectDuration;
    }

    public int getUseEffectCooldown() {
        return useEffectCooldown;
    }

    public int getXp() {
        return xp;
    }

}
