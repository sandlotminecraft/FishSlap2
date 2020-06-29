package me.stipe.fishslap.types;

import me.stipe.fishslap.FSApi;
import me.stipe.fishslap.events.ChangeOffhandFishEvent;
import me.stipe.fishslap.events.FishSlapEvent;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class Fish implements Listener {
    protected NamespacedKey levelKey = new NamespacedKey(FSApi.getPlugin(), "level");
    protected NamespacedKey xpKey = new NamespacedKey(FSApi.getPlugin(), "xp");
    protected FishConfig config;
    protected Material material;
    protected List<String> extraLore = new ArrayList<>();
    protected Player owner;

    public Fish(FishConfig config, Material material, Player owner) {
        this.config = config;
        this.material = material;
        this.owner = owner;
    }

    private List<String> generateLore(LevelData data, int level, int xp) {
        List<String> lore = new ArrayList<>();

        String itemLevel = ChatColor.GOLD + "Item Level %d (%.1f%%)";
        String damageSpeed = ChatColor.WHITE + "%.1f Damage                        Speed %.1f";
        String dps = ChatColor.WHITE + "(%.1f damage per second)";
        String whenHeldInOffHand = ChatColor.GRAY + "When Held in Off Hand:";
        String whenUsed = ChatColor.GRAY + "When Used (in off hand)";
        String effect = ChatColor.BLUE + "   %s %s";
        String effectDuration = ChatColor.BLUE + "   %s %s for %s seconds";
        String cooldown = ChatColor.BLUE + "      (%s second cooldown)";

        lore.add(String.format(itemLevel, level, xp / data.getXp()));
        lore.add(String.format(damageSpeed, data.getDamage(), data.getSpeed()));
        lore.add(String.format(dps, data.getDamage() * data.getSpeed()));
        if (!data.getEquipEffectStrings().isEmpty()) {
            lore.add(whenHeldInOffHand);
            for (PotionEffect e : data.getEquipEffects()) {
                lore.add(String.format(effect, toReadable(e.getType().getName()), integerToRomanNumeral(e.getAmplifier() + 1)));
            }
        }
        if (!data.getUseEffectStrings().isEmpty()) {
            lore.add(whenUsed);
            for (PotionEffect e : data.getUseEffects()) {
                lore.add(String.format(effectDuration, toReadable(e.getType().getName()), integerToRomanNumeral(e.getAmplifier() + 1), e.getDuration()));
                lore.add(String.format(cooldown, data.getUseEffectCooldown()));
            }
        }

        lore.addAll(extraLore);
        return lore;
    }

    public ItemStack generateItem(int level, int startingXp) {
        ItemStack fish = new ItemStack(material);
        ItemMeta meta = fish.getItemMeta();
        LevelData data = config.getLevelData(level);

        meta.setDisplayName(config.getDisplayName());
        for (String s : data.getEnchantmentStrings()) {
            Enchantment enchant;
            int enchantLevel;
            try {
                enchant = Enchantment.getByKey(NamespacedKey.minecraft(s.split(" ")[0].toLowerCase()));
                if (enchant == null)
                    continue;
                enchantLevel = Integer.parseInt(s.split(" ")[1]);
            } catch (Exception e) {
                System.out.println("[FishSlap] ERROR: Failed to recognize enchantment '" + s + "'. Check your configs.");
                e.printStackTrace();
                return null;
            }
            meta.addEnchant(enchant, enchantLevel, true);
        }
        meta.getPersistentDataContainer().set(levelKey, PersistentDataType.INTEGER, level);
        meta.getPersistentDataContainer().set(xpKey, PersistentDataType.INTEGER, startingXp);
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, new AttributeModifier(UUID.randomUUID(), "speed", data.getAttackSpeedModifier(),
                AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.OFF_HAND));
        meta.addAttributeModifier(Attribute.GENERIC_ARMOR, new AttributeModifier(UUID.randomUUID(), "armor", data.getArmor(),
                AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.OFF_HAND));
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier(UUID.randomUUID(), "damage", data.getDamageModifier(),
                AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.OFF_HAND));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        meta.setLore(generateLore(data, level, startingXp));

        fish.setItemMeta(meta);

        return fish;
    }

    private String toReadable(String string) {
        String[] names = string.split("_");
        for (int i = 0; i < names.length; i++) {
            names[i] = names[i].substring(0, 1) + names[i].substring(1).toLowerCase();
        }
        return StringUtils.join(names, " ");
    }

    public String integerToRomanNumeral(int input) {
        if (input < 1 || input > 3999)
            return "Invalid Roman Number Value";
        StringBuilder s = new StringBuilder();
        while (input >= 5) {
            s.append("V");
            input -= 5;
        }
        while (input >= 4) {
            s.append("IV");
            input -= 4;
        }
        while (input >= 1) {
            s.append("I");
            input -= 1;
        }
        return s.toString();
    }

    @EventHandler
    public void onChangeOffhand(ChangeOffhandFishEvent event) {

    }

    @EventHandler
    public void onFishSlapEvent(FishSlapEvent event) {

    }

}
