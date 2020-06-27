package me.stipe.fishslap.abilities;

import me.stipe.fishslap.FishSlap;
import me.stipe.fishslap.events.ChangeOffhandFishEvent;
import me.stipe.fishslap.events.FishSlapEvent;
import me.stipe.fishslap.fish.FishMeta;
import me.stipe.fishslap.managers.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;

public class Cod implements FishMeta, Listener {

    double damage;
    double armor;
    int enchantLevel;
    int cooldownLength;
    Enchantment enchant;
    PotionEffect equipEffect;
    PotionEffect useEffect;

    double attackSpeed;

    public Cod(int level) {
        ConfigManager config = FishSlap.getConfigManager();

        damage = config.getCodBaseDamage() + (level - 1) * config.getCodDamagePerLevel();
        armor = config.getCodBaseArmor() + (level - 1) * config.getCodArmorPerLevel();
        enchant = config.getCodEnchant();

        enchantLevel = 0;
        cooldownLength = 0;
        for (int i = 0; i < level; i++) {
            if (config.getCodEnchantUpgradeLevels().contains(i))
                enchantLevel++;
            if (config.getCodUseEffectCooldowns().containsKey(i))
                cooldownLength = config.getCodUseEffectCooldowns().get(i);
        }

        if (config.getCodEquipEffect() != null) {
            int amplifier = 0;

            for (int i = 0; i < level; i++ ) {
                if (config.getCodEquipEffectUpgradeLevels().contains(i))
                    amplifier++;
            }
            equipEffect = new PotionEffect(config.getCodEquipEffect(), 100000, amplifier);
        }

        if (config.getCodUseEffect() != null) {
            int duration = 0;
            int amplifier = 0;

            for (int i = 0; i < level; i++) {
                if (config.getCodUseEffectDurations().containsKey(i))
                    duration = config.getCodUseEffectDurations().get(i);
                if (config.getCodUseEffectUpgradeLevels().contains(i))
                    amplifier++;
            }
            useEffect = new PotionEffect(config.getCodUseEffect(), duration, amplifier);
        }


    }

    @EventHandler
    public void onFishSlap(FishSlapEvent event) {
        if (event.getFish().getMeta() instanceof Cod) {
            // do what a cod does
        }
    }

    @EventHandler
    public void onChangeFish(ChangeOffhandFishEvent event) {
        if (event.getNewFish().getMeta() instanceof Cod)
            addEquipEffect(event.getPlayer());
        if (event.getOldFish().getMeta() instanceof Cod)
            removeEquipEffect(event.getPlayer());
    }

    public void addEquipEffect(Player player) {

    }

    public void removeEquipEffect(Player player) {

    }


    @Override
    public double getDamage() {
        return damage;
    }

    @Override
    public double getAttackSpeed() {
        return attackSpeed;
    }

    @Override
    public double getArmor() {
        return armor;
    }

    @Override
    public Enchantment getEnchant() {
        return enchant;
    }

    @Override
    public int getEnchantLevel() {
        return enchantLevel;
    }

    @Override
    public PotionEffect getEquipEffect() {
        return equipEffect;
    }

    @Override
    public PotionEffect getUseEffect() {
        return useEffect;
    }

    @Override
    public int getCooldownLength() {
        return cooldownLength;
    }

    @Override
    public ItemStack createItemStack(int level, int startXp) {
        ItemStack cod = new ItemStack(Material.COD);
        ItemMeta meta = cod.getItemMeta();
        Plugin plugin = Bukkit.getPluginManager().getPlugin("FishSlap");

        if (plugin == null)
            return null;

        NamespacedKey key = new NamespacedKey(plugin, "level");

        meta.setDisplayName(FishSlap.getConfigManager().getCodDisplayName());
        meta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, level);
        key = new NamespacedKey(plugin, "xp");
        meta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, startXp);
        return null;
    }
}
