package me.stipe.fishslap.types;

import me.stipe.fishslap.FSApi;
import me.stipe.fishslap.configs.Translations;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Powerup {
    private String name;
    private String displayName;
    private Material material;
    private final List<PotionEffect> equipEffects = new ArrayList<>();
    private final Map<Attribute, AttributeModifier> attributeBonuses = new HashMap<>();
    private final Map<Enchantment, Integer> enchantments = new HashMap<>();
    private int maxDuration;
    private int remainingDuration;
    private NamespacedKey key;

    public Powerup(ConfigurationSection section) {
        parseConfigSection(section);
    }

    public boolean tick() {
        remainingDuration--;
        if (remainingDuration <= 0)
            return true;
        return false;
    }

    public ItemStack createItem() {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        List<String> lore = new ArrayList<>();
        Translations trans = FSApi.getConfigManager().getTranslations();

        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, String.valueOf(ThreadLocalRandom.current().nextInt()));
        meta.getPersistentDataContainer().set(new NamespacedKey(FSApi.getPlugin(), String.valueOf(ThreadLocalRandom.current().nextInt())),
                PersistentDataType.STRING, UUID.randomUUID().toString());

        lore.add(ChatColor.GOLD + "Fish Powerup Item");
        lore.add(ChatColor.GRAY + String.format("Duration: %d:%02d", maxDuration / 60, maxDuration % 60));
        lore.add("");
        lore.add(ChatColor.DARK_AQUA + "When Attached to a Fish:");

        for (PotionEffect e : equipEffects)
            lore.add(ChatColor.WHITE + " Grants " + trans.toReadable(e.getType().getName()) + " " + trans.integerToRomanNumeral(e.getAmplifier() + 1) + " when held");
        for (Enchantment e : enchantments.keySet())
            lore.add(ChatColor.WHITE + " Adds " + ChatColor.GRAY + trans.toReadable(e.getKey().getKey()) + " " + trans.integerToRomanNumeral(enchantments.get(e)));
        for (Attribute a : attributeBonuses.keySet()) {
            double amount = attributeBonuses.get(a).getAmount();
            String sign = amount < 0 ? "-" : "+";
            String number = " %s%.0f %s";
            String percent = " %s%.0f%% %s";

            switch (a) {
                case GENERIC_ARMOR:
                    lore.add(ChatColor.WHITE + String.format(number, sign, amount, "Armor when held"));
                    break;
                case GENERIC_MAX_HEALTH:
                    lore.add(ChatColor.WHITE + String.format(number, sign, amount, "Health when held"));
                    break;
                case GENERIC_LUCK:
                    lore.add(ChatColor.WHITE + String.format(number, sign, amount, "Luck when held"));
                    break;
                case GENERIC_ATTACK_SPEED:
                    lore.add(ChatColor.WHITE + String.format(number, sign, amount, "Attack Speed when held"));
                    break;
                case GENERIC_ATTACK_DAMAGE:
                    lore.add(ChatColor.WHITE + String.format(number, sign, amount, "Damage when held"));
                    break;
                case GENERIC_MOVEMENT_SPEED:
                    lore.add(ChatColor.WHITE + String.format(percent, sign, amount, "Movement Speed when held"));
                    break;
                case GENERIC_ARMOR_TOUGHNESS:
                    lore.add(ChatColor.WHITE + String.format(number, sign, amount, "Armor Toughness when held"));
                    break;
                case GENERIC_KNOCKBACK_RESISTANCE:
                    lore.add(ChatColor.WHITE + String.format(percent, sign, amount, "Knockback Resistance when held"));
                    break;
                default:
                    break;
            }
        }


        lore.add("");
        lore.add(ChatColor.GREEN + "To Attach: Shift-Click on a fish while");
        lore.add(ChatColor.GREEN + "holding this on your cursor");

        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public List<PotionEffect> getEquipEffects() {
        return equipEffects;
    }

    public List<String> getAttachedLore() {
        Translations trans = FSApi.getConfigManager().getTranslations();
        List<String> lore = new ArrayList<>();

        lore.add("");
        lore.add(ChatColor.translateAlternateColorCodes('&',"&e[&6Powerup&e]: " + trans.toReadable(name)));
        for (PotionEffect e : equipEffects)
            lore.add(ChatColor.YELLOW + " Grants " + trans.toReadable(e.getType().getName()) + " " + trans.integerToRomanNumeral(e.getAmplifier() + 1) + " when held");
        for (Enchantment e : enchantments.keySet())
            lore.add(ChatColor.YELLOW + " Adds " + ChatColor.GRAY + trans.toReadable(e.getKey().getKey()) + " " + trans.integerToRomanNumeral(enchantments.get(e)));
        for (Attribute a : attributeBonuses.keySet()) {
            double amount = attributeBonuses.get(a).getAmount();
            String sign = amount < 0 ? "-" : "+";
            String number = " %s%.0f %s";
            String percent = " %s%.0f%% %s";

            switch (a) {
                case GENERIC_ARMOR:
                    lore.add(ChatColor.YELLOW + String.format(number, sign, amount, "Armor when held"));
                    break;
                case GENERIC_MAX_HEALTH:
                    lore.add(ChatColor.YELLOW + String.format(number, sign, amount, "Health when held"));
                    break;
                case GENERIC_LUCK:
                    lore.add(ChatColor.YELLOW + String.format(number, sign, amount, "Luck when held"));
                    break;
                case GENERIC_ATTACK_SPEED:
                    lore.add(ChatColor.YELLOW + String.format(number, sign, amount, "Attack Speed when held"));
                    break;
                case GENERIC_ATTACK_DAMAGE:
                    lore.add(ChatColor.YELLOW + String.format(number, sign, amount, "Damage when held"));
                    break;
                case GENERIC_MOVEMENT_SPEED:
                    lore.add(ChatColor.YELLOW + String.format(percent, sign, amount, "Movement Speed when held"));
                    break;
                case GENERIC_ARMOR_TOUGHNESS:
                    lore.add(ChatColor.YELLOW + String.format(number, sign, amount, "Armor Toughness when held"));
                    break;
                case GENERIC_KNOCKBACK_RESISTANCE:
                    lore.add(ChatColor.YELLOW + String.format(percent, sign, amount, "Knockback Resistance when held"));
                    break;
                default:
                    break;
            }
        }
        return lore;
    }

    public void applyPowerup(Fish fish) {
        if (fish.hasPowerup()) {
            fish.getPowerup().removePowerup(fish);
            FSApi.getPowerupManager().removeActivePowerup(fish.getPowerup(), fish);
        }

        remainingDuration = maxDuration;
        if (maxDuration == 0 || fish.getOwner() == null || fish.getFishItem() == null)
            return;

        fish.getFishItem().getItemMeta().getPersistentDataContainer().set(key, PersistentDataType.STRING, fish.getOwner().getName());
        fish.attachPowerup(this);

        if (fish.isEquipped())
            fish.addEquipEffects();

        for (Attribute attribute : attributeBonuses.keySet()) {
            ItemMeta meta = fish.getFishItem().getItemMeta();
            meta.addAttributeModifier(attribute, attributeBonuses.get(attribute));
            fish.getFishItem().setItemMeta(meta);
            fish.updateLore();
        }

        for (Enchantment enchant : enchantments.keySet()) {
            ItemMeta meta = fish.getFishItem().getItemMeta();
            meta.addEnchant(enchant, enchantments.get(enchant), true);
            fish.getFishItem().setItemMeta(meta);
        }

        fish.updateLore();
    }

    public void removePowerup(Fish fish) {
        if (fish.getOwner() == null || fish.getFishItem() == null)
            return;

        if (fish.isEquipped())
            fish.removeEquipEffects();

        fish.removePowerup();
        ItemMeta meta = fish.getFishItem().getItemMeta();

        for (Attribute attribute : attributeBonuses.keySet()) {
            meta.removeAttributeModifier(attribute, attributeBonuses.get(attribute));
        }

        for (Enchantment enchant : enchantments.keySet()) {
            meta.removeEnchant(enchant);
        }

        fish.getFishItem().setItemMeta(meta);

        if (fish.isEquipped())
            fish.addEquipEffects();

        fish.updateLore();
        fish.getFishItem().getItemMeta().getPersistentDataContainer().remove(key);
    }

    private void parseConfigSection(ConfigurationSection section) {
        this.name = section.getString("name");
        this.key = new NamespacedKey(FSApi.getPlugin(), "powerup/" + name);
        this.displayName = ChatColor.translateAlternateColorCodes('&', section.getString("display name"));
        this.maxDuration = section.getInt("duration");
        this.material = Material.valueOf(section.getName().toUpperCase());

        for (String s : section.getStringList("enchantments")) {
            String[] args = s.split(" ");
            Enchantment enchant = Enchantment.getByKey(NamespacedKey.minecraft(args[0].toLowerCase()));
            if (enchant == null) {
                System.out.println("[FishSlap] Error parsing enchant in powerups.yml. Unknown enchant: " + args[0]);
                continue;
            }
            if (args.length > 1) {
                try {
                    int level = Integer.parseInt(args[1]);
                    enchantments.put(enchant, level);
                } catch (Exception ignored) {
                    System.out.println("[FishSlap] Error parsing enchant in powerups.yml. Expected <enchant> <level>, got: " + s);
                }
            } else
                enchantments.put(enchant, enchant.getStartLevel());
        }

        for (String s : section.getStringList("equip effects")) {
            String[] args = s.split(" ");
            PotionEffectType type = PotionEffectType.getByName(args[0]);
            int amplifier = 0;
            if (type == null) {
                System.out.println("[FishSlap] Error parsing equip effect in powerups.yml. Unknown effect type: " + args[0]);
                continue;
            }
            if (args.length > 1) {
                try {
                    amplifier = Integer.parseInt(args[1]);
                } catch (Exception ignored) {
                    System.out.println("[FishSlap] Error parsing equip effect in powerups.yml. Expected <effect> <amplifier>, got: " + s);
                }
            }
            equipEffects.add(new PotionEffect(type, maxDuration, amplifier));
        }

        for (String s : section.getStringList("attribute bonuses")) {
            String[] args = s.split(" ");
            Attribute attribute = null;
            float bonus = 0;
            for (Attribute a : Attribute.values()) {
                if (a.name().toLowerCase().contains(args[0].toLowerCase())) {
                    attribute = a;
                    break;
                }
            }
            if (attribute == null) {
                System.out.println("[FishSlap] Error parsing attribute bonus in powerups.yml. Unknown attribute: " + args[0]);
                continue;
            }
            if (args.length < 2) {
                System.out.println("[FishSlap] Error parsing attribute bonus in powerups.yml. Expected <attribute> <bonus>, got: " + s);
                continue;
            }
            try {
                bonus = Float.parseFloat(args[1]);
            } catch (Exception e) {
                System.out.println("[FishSlap] Error parsing attribute bonus in powerups.yml. Expected <attribute> <bonus>, got: " + s);
                continue;
            }
            AttributeModifier.Operation op = AttributeModifier.Operation.ADD_NUMBER;
            if (attribute == Attribute.GENERIC_KNOCKBACK_RESISTANCE || attribute == Attribute.GENERIC_MOVEMENT_SPEED)
                op = AttributeModifier.Operation.MULTIPLY_SCALAR_1;

            attributeBonuses.put(attribute, new AttributeModifier(UUID.randomUUID(), "powerup/" + attribute.name().toLowerCase(), bonus, op, EquipmentSlot.OFF_HAND));
        }
    }

    public void showCooldownOnActionBar(Fish fish) {
        if (fish.getOwner() == null)
            return;

        Translations trans = FSApi.getConfigManager().getTranslations();

        fish.getOwner().sendActionBar('&', String.format(trans.getActionBarPowerupCooldown(),
                trans.toReadable(name), remainingDuration / 60, remainingDuration % 60));

    }

    public NamespacedKey getKey() {
        return key;
    }

    public void setKey(NamespacedKey key) {
        this.key = key;
    }
}
