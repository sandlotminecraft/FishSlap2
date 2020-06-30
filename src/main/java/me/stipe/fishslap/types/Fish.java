package me.stipe.fishslap.types;

import me.stipe.fishslap.FSApi;
import me.stipe.fishslap.events.ChangeOffhandFishEvent;
import me.stipe.fishslap.managers.ConfigManager;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Fish implements Listener {
    protected NamespacedKey levelKey = new NamespacedKey(FSApi.getPlugin(), "level");
    protected NamespacedKey xpKey = new NamespacedKey(FSApi.getPlugin(), "xp");
    protected Material material;
    protected List<String> extraLore = new ArrayList<>();
    protected Player owner;
    protected FishMeta fishMeta;
    protected String displayName;
    protected int level;
    protected int xp;

    public Fish() {
        material = null;
        owner = null;
        level = 0;
        fishMeta = null;
        displayName = "";
        xp = 0;
    }

    public Fish(Material type, int level, int xp, Player owner) {
        material = type;
        this.level = level;
        this.xp = xp;
        this.owner = owner;
        getFishMeta();
    }

    public FishMeta getFishMeta() {
        if (fishMeta == null) {
            ConfigManager cm = FSApi.getConfigManager();
            switch (material) {
                case COD:
                    fishMeta = cm.getCodConfig().getLevelData(level);
                    displayName = cm.getCodConfig().getDisplayName();
                    break;
                case SALMON:
                    fishMeta = cm.getSalmonConfig().getLevelData(level);
                    displayName = cm.getSalmonConfig().getDisplayName();
                    break;
                case TROPICAL_FISH:
                    fishMeta = cm.getTropicalFishConfig().getLevelData(level);
                    displayName = cm.getTropicalFishConfig().getDisplayName();
                    break;
                case PUFFERFISH:
                    fishMeta = cm.getPufferfishConfig().getLevelData(level);
                    displayName = cm.getPufferfishConfig().getDisplayName();
                    break;
                default:
                    return null;
            }
        }
        return fishMeta;
    }

    public static boolean isSpecialFish(@NotNull ItemStack item, @NotNull Player owner) {
        return getFromItemStack(item, owner) != null;
    }

    @Nullable
    public static Fish getFromItemStack(@NotNull ItemStack item, @NotNull Player owner) {
        NamespacedKey levelKey = new NamespacedKey(FSApi.getPlugin(), "level");
        NamespacedKey xpKey = new NamespacedKey(FSApi.getPlugin(), "xp");
        Material mat = item.getType();
        int level = 1;
        int xp = 0;

        if (item.getItemMeta() != null && item.getItemMeta().getPersistentDataContainer().has(levelKey, PersistentDataType.INTEGER)) {
            Integer l = item.getItemMeta().getPersistentDataContainer().get(levelKey, PersistentDataType.INTEGER);
            if (l == null)
                return null;
            level = l;

            Integer experience = item.getItemMeta().getPersistentDataContainer().get(xpKey, PersistentDataType.INTEGER);
            if (experience == null)
                return null;
            xp = experience;
        }
        else return null;

        return new Fish(mat, level, xp, owner);
    }
    public void addEquipEffects() {
        for (PotionEffect e : fishMeta.getEquipEffects())
            e.apply(owner);
    }

    public void removeEquipEffects() {
        for (PotionEffect e : fishMeta.getEquipEffects()) {
            owner.removePotionEffect(e.getType());
        }
    }

    private List<String> generateLore() {
        List<String> lore = new ArrayList<>();

        String itemLevel = ChatColor.GOLD + "Item Level %d (%.1f%%)";
        String damageSpeed = ChatColor.WHITE + "%.1f Damage                        Speed %.1f";
        String dps = ChatColor.WHITE + "(%.1f damage per second)";
        String armor = ChatColor.WHITE + " +%.0f Armor";
        String whenHeldInOffHand = ChatColor.DARK_AQUA + "When Held in Off Hand:";
        String effect = ChatColor.GREEN + " Grants %s %s";
        String statBonus = ChatColor.WHITE + " %s%s %s";
        String effectDuration = ChatColor.GREEN + "Use: Grants %s %s for %s";
        String cooldown = ChatColor.GREEN + "seconds (%s second cooldown)";

        List<String> equipEffects = new ArrayList<>();

        for (Enchantment ench : fishMeta.getEnchantments().keySet()) {
            if (ench instanceof CustomEnchantment)
                lore.add(((CustomEnchantment) ench).getLore(fishMeta.getEnchantments().get(ench)));
        }

        lore.add(String.format(itemLevel, level, ((float) xp / fishMeta.getXp()) * 100));
        lore.add(String.format(damageSpeed, fishMeta.getDamage(), fishMeta.getAttackSpeed()));
        lore.add(String.format(dps, fishMeta.getDamage() * fishMeta.getAttackSpeed()));

        if (fishMeta.getArmor() > 0)
            equipEffects.add(String.format(armor, fishMeta.getArmor()));
        if (fishMeta.getToughness() > 0)
            equipEffects.add(String.format(statBonus, "+", (int) fishMeta.getToughness(), "Armor Toughness"));
        if (fishMeta.getKnockbackResist() > 0)
            equipEffects.add(String.format(statBonus, "+", (int) (fishMeta.getKnockbackResist() * 100) + "%", "Knockback Resistance"));
        if (fishMeta.getLuckBonus() != 0)
            equipEffects.add(String.format(statBonus, fishMeta.getLuckBonus() > 0 ? "+" : "-", (int) fishMeta.getLuckBonus(), "Luck"));
        if (fishMeta.getHealthBonus() != 0)
            equipEffects.add(String.format(statBonus, fishMeta.getHealthBonus() > 0 ? "+" : "-", (int) fishMeta.getHealthBonus(), "Health"));
        if (fishMeta.getSpeedBonus() != 0)
            equipEffects.add(String.format(statBonus, fishMeta.getSpeedBonus() > 0 ? "+" : "-", (int) (fishMeta.getSpeedBonus() * 100) + "%", "Movement Speed"));
        if (!fishMeta.getEquipEffects().isEmpty()) {
            for (PotionEffect e : fishMeta.getEquipEffects()) {
                equipEffects.add(String.format(effect, toReadable(e.getType().getName()), integerToRomanNumeral(e.getAmplifier() + 1)));
            }
        }

        if (!equipEffects.isEmpty()) {
            lore.add(whenHeldInOffHand);
            lore.addAll(equipEffects);
        }

        if (!fishMeta.getUseEffects().isEmpty()) {
            lore.add(" ");
            for (PotionEffect e : fishMeta.getUseEffects()) {
                lore.add(String.format(effectDuration, toReadable(e.getType().getName()), integerToRomanNumeral(e.getAmplifier() + 1), e.getDuration()));
                lore.add(String.format(cooldown, fishMeta.getUseEffectCooldown()));
            }
        }

        lore.addAll(extraLore);
        return lore;
    }

    public ItemStack generateItem() {
        ItemStack fish = new ItemStack(material);
        ItemMeta meta = fish.getItemMeta();

        meta.setDisplayName(displayName);
        for (String s : fishMeta.getEnchantmentStrings()) {
            Enchantment enchant;
            int enchantLevel;
            try {
                enchant = Enchantment.getByKey(NamespacedKey.minecraft(s.split(" ")[0].toLowerCase()));
                if (enchant == null)
                    enchant = Enchantment.getByKey(new NamespacedKey(FSApi.getPlugin(), s.split(" ")[0].toLowerCase()));
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
        meta.getPersistentDataContainer().set(xpKey, PersistentDataType.INTEGER, xp);
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, new AttributeModifier(UUID.randomUUID(), "speed", fishMeta.getAttackSpeedModifier(),
                AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.OFF_HAND));
        meta.addAttributeModifier(Attribute.GENERIC_ARMOR, new AttributeModifier(UUID.randomUUID(), "armor", fishMeta.getArmor(),
                AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.OFF_HAND));
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier(UUID.randomUUID(), "damage", fishMeta.getDamageModifier(),
                AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.OFF_HAND));
        meta.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, new AttributeModifier(UUID.randomUUID(), "toughness", fishMeta.getToughness(),
                AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.OFF_HAND));
        meta.addAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE, new AttributeModifier(UUID.randomUUID(), "knockback", fishMeta.getKnockbackResist(),
                AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.OFF_HAND));
        meta.addAttributeModifier(Attribute.GENERIC_LUCK, new AttributeModifier(UUID.randomUUID(), "luck", fishMeta.getLuckBonus(),
                AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.OFF_HAND));
        meta.addAttributeModifier(Attribute.GENERIC_MAX_HEALTH, new AttributeModifier(UUID.randomUUID(), "health", fishMeta.getHealthBonus(),
                AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.OFF_HAND));
        meta.addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED, new AttributeModifier(UUID.randomUUID(), "movementSpeed", fishMeta.getSpeedBonus(),
                AttributeModifier.Operation.MULTIPLY_SCALAR_1, EquipmentSlot.OFF_HAND));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        meta.setLore(generateLore());

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

    public static String integerToRomanNumeral(int input) {
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
    public void onChangeFish(ChangeOffhandFishEvent event) {

        if (event.getOldFish() != null)
            event.getOldFish().removeEquipEffects();
        if (event.getNewFish() != null) {
            if (!FSApi.getConfigManager().getMainConfig().isEnabledEquipEffectsIfNotPlaying() && !FSApi.getPlayerManager().isPlaying(event.getPlayer()))
                return;
            event.getNewFish().addEquipEffects();
        }
    }
}
