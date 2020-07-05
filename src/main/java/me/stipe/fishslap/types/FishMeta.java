package me.stipe.fishslap.types;

import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class FishMeta {
    private final double damage;
    private final double armor;
    private final double attackSpeed;
    private final double toughness;
    private final double knockbackResist;
    private final double luckBonus;
    private final double healthBonus;
    private final double speedBonus;
    private final List<String> enchantmentStrings;
    private final List<String> equipEffectStrings;
    private final List<String> useEffectStrings;
    private final List<String> fishAbilities;
    private final int useEffectDuration;
    private final int useEffectCooldown;
    private final int xp;


    public FishMeta(double damage, double armor, double attackSpeed, double toughness, double knockbackResist, double luckBonus, double healthBonus,
                    double speedBonus, String[] enchantmentStrings, String[] equipEffectStrings, String[] useEffectStrings, String[] fishAbilities, int useEffectDuration,
                    int useEffectCooldown, int xp) {
        this.damage = damage;
        this.armor = armor;
        this.attackSpeed = attackSpeed;
        this.toughness = toughness;
        this.knockbackResist = knockbackResist;
        this.luckBonus = luckBonus;
        this.healthBonus = healthBonus;
        this.speedBonus = speedBonus;
        this.enchantmentStrings = Arrays.asList(enchantmentStrings);
        this.equipEffectStrings = Arrays.asList(equipEffectStrings);
        this.useEffectStrings = Arrays.asList(useEffectStrings);
        this.useEffectDuration = useEffectDuration;
        this.useEffectCooldown = useEffectCooldown;
        this.xp = xp;
        this.fishAbilities = Arrays.asList(fishAbilities);
    }

    public FishMeta(ConfigurationSection configData) {
        damage = configData.getDouble("damage");
        armor = configData.getDouble("armor");
        attackSpeed = configData.getDouble("attackSpeed");
        toughness = configData.getDouble("toughness");
        knockbackResist = configData.getDouble("knockbackResistance");
        luckBonus = configData.getDouble("luck");
        healthBonus = configData.getDouble("health");
        speedBonus = configData.getDouble("speedBonus");
        enchantmentStrings = configData.getStringList("enchantments");
        equipEffectStrings = configData.getStringList("equipEffects");
        useEffectStrings = configData.getStringList("useEffects");
        useEffectDuration = configData.getInt("useEffectDuration");
        useEffectCooldown = configData.getInt("useEffectCooldown");
        xp = configData.getInt("xp");
        fishAbilities = configData.getStringList("abilities");
    }

    public void createConfigSection(ConfigurationSection levelData) {
        levelData.set("damage", damage);
        levelData.set("armor", armor);
        levelData.set("attackSpeed", attackSpeed);
        levelData.set("toughness", toughness);
        levelData.set("knockbackResistance", knockbackResist);
        levelData.set("luck", luckBonus);
        levelData.set("health", healthBonus);
        levelData.set("speedBonus", speedBonus);
        levelData.set("enchantments", enchantmentStrings);
        levelData.set("equipEffects", equipEffectStrings);
        levelData.set("useEffects", useEffectStrings);
        levelData.set("useEffectDuration", useEffectDuration);
        levelData.set("useEffectCooldown", useEffectCooldown);
        levelData.set("xp", xp);
        levelData.set("abilities", fishAbilities);
    }

    public double getAttackSpeed() {
        return attackSpeed;
    }

    public double getToughness() {
        return toughness;
    }

    public double getKnockbackResist() {
        return knockbackResist;
    }

    public double getLuckBonus() {
        return luckBonus;
    }

    public double getHealthBonus() {
        return healthBonus;
    }

    public double getSpeedBonus() {
        return speedBonus;
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
            PotionEffect potionEffect = new PotionEffect(type, Integer.MAX_VALUE, amplifier, false, false, true);
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
            PotionEffect potionEffect = new PotionEffect(type, useEffectDuration * 20, amplifier, false, false);
            effects.add(potionEffect);
        }
        return effects;
    }

    public Set<FishAbility> getAbilities() {
        Set<FishAbility> abilities = new HashSet<>();

        for (String s : fishAbilities) {
            Class<?> abilityClass;
            try {
                abilityClass = Class.forName("me.stipe.fishslap.abilities." + s);
                abilities.add((FishAbility) abilityClass.newInstance());
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                System.out.println("[FishSlap] ERROR: Could not load ability: " + s);
            }
        }


        return abilities;
    }

    public double getAttackSpeedModifier() {
        return attackSpeed - 4;
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
