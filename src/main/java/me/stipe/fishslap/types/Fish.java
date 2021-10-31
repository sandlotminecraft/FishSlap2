package me.stipe.fishslap.types;

import me.stipe.fishslap.FSApi;
import me.stipe.fishslap.enchants.HealingTouch;
import me.stipe.fishslap.events.ChangeOffhandFishEvent;
import me.stipe.fishslap.managers.ConfigManager;
import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class Fish implements Listener {
    private NamespacedKey levelKey = new NamespacedKey(FSApi.getPlugin(), "level");
    private NamespacedKey xpKey = new NamespacedKey(FSApi.getPlugin(), "xp");
    private Material material;
    private Player owner;
    private FishMeta fishMeta;
    private ItemStack fishItem;
    private String displayName;
    private Powerup powerup;
    private int level;
    private int xp;
    private Color effectColor;

    public Fish() {
        material = null;
        owner = null;
        level = 0;
        fishMeta = null;
        fishItem = null;
        powerup = null;
        displayName = "";
        xp = 0;
        effectColor = Color.WHITE;
    }

    public Fish(Material type, int level, int xp, Player owner) {
        material = type;
        this.level = level;
        this.xp = xp;
        this.owner = owner;
        fishItem = null;
        powerup = null;
        getFishMeta();
    }

    public Fish(Material type, int level, int xp, Player owner, ItemStack fishItem) {
        this(type, level, xp, owner);
        this.fishItem = fishItem;
    }

    public Material getType() {
        return material;
    }

    public Player getOwner() {
        return owner;
    }

    public boolean isEquipped() {
        if (owner != null && fishItem != null) {
            Fish held = getFromItemStack(owner.getInventory().getItemInOffHand(), owner);

            return held != null && isSameAs(held);
        }
        return false;
    }

    public boolean isInMainHand() {
        if (owner != null && fishItem != null) {
            Fish held = getFromItemStack(owner.getInventory().getItemInMainHand(), owner);

            return held != null && isSameAs(held);
        }
        return false;
    }

    public void updateLore() {
        if (fishItem == null)
            return;

        ItemMeta meta = fishItem.getItemMeta();
        meta.setLore(generateLore());
        fishItem.setItemMeta(meta);
    }

    public boolean isSameAs(Fish fish) {
        return getLevel() == fish.getLevel() && getXp() == fish.getXp() && material == fish.getType();
    }

    public void attachPowerup(Powerup powerup) {
        this.powerup = powerup;
    }

    public void removePowerup() {
        powerup = null;
    }

    public boolean hasPowerup() {
        return powerup != null;
    }

    public Powerup getPowerup() {
        return powerup;
    }

    public FishMeta getFishMeta() {
        if (fishMeta == null) {
            ConfigManager cm = FSApi.getConfigManager();
            switch (material) {
                case COD:
                    fishMeta = cm.getCodConfig().getLevelData(level);
                    displayName = cm.getCodConfig().getDisplayName();
                    effectColor = Color.BLUE;
                    break;
                case SALMON:
                    fishMeta = cm.getSalmonConfig().getLevelData(level);
                    displayName = cm.getSalmonConfig().getDisplayName();
                    effectColor = Color.FUCHSIA;
                    break;
                case TROPICAL_FISH:
                    fishMeta = cm.getTropicalFishConfig().getLevelData(level);
                    displayName = cm.getTropicalFishConfig().getDisplayName();
                    effectColor = Color.ORANGE;
                    break;
                case PUFFERFISH:
                    fishMeta = cm.getPufferfishConfig().getLevelData(level);
                    displayName = cm.getPufferfishConfig().getDisplayName();
                    effectColor = Color.YELLOW;
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
    public static Fish getFromItemStack(@NotNull ItemStack item, Player owner) {
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

        return new Fish(mat, level, xp, owner, item);
    }
    public void addEquipEffects() {
        for (PotionEffect e : fishMeta.getEquipEffects())
            e.apply(owner);
        if (powerup != null) {
            for (PotionEffect e : powerup.getEquipEffects())
                e.apply(owner);
        }
    }

    

    public void removeEquipEffects() {
        for (PotionEffect e : fishMeta.getEquipEffects()) {
            owner.removePotionEffect(e.getType());
        }
        if (powerup != null) {
            for (PotionEffect e : powerup.getEquipEffects())
                owner.removePotionEffect(e.getType());
        }
    }

    private boolean hasCooldown() {
        return owner.hasCooldown(material);
    }

    private void doUseEffect() {
        if (fishMeta.getUseEffectCooldown() == 0 && fishMeta.getUseEffects().isEmpty())
            return;

        for (PotionEffect e : fishMeta.getUseEffects())
            e.apply(owner);
        owner.setCooldown(material, fishMeta.getUseEffectCooldown() * 20);
        for (int i = 0; i < 200; i++) {
            owner.getWorld().spawnParticle(Particle.SPELL_MOB, owner.getLocation().add(0,(float) i / 100,0),
                    0, (float) effectColor.getRed()/255, (float) effectColor.getGreen()/255, (float) effectColor.getBlue()/255, 1);
        }
        owner.getWorld().playSound(owner.getLocation(), Sound.ENTITY_EVOKER_CAST_SPELL, 1F, 1.2F);
        for (FishAbility ability : fishMeta.getAbilities())
            ability.doRightClickEffect(owner);
    }

    private List<String> generateLore() {
        List<String> lore = new ArrayList<>();

        String itemLevel = ChatColor.GOLD + "Item Level %d";
        String damageSpeed = ChatColor.WHITE + "%.1f Damage                        Speed %.1f";
        String healingDamageSpeed = ChatColor.GREEN + "Heals %.1f Damage" + ChatColor.WHITE + "                 Speed %.1f";
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

        lore.add(String.format(itemLevel, level));
        if (fishMeta.getEnchantments().containsKey(HealingTouch.HEALING_TOUCH))
            lore.add(String.format(healingDamageSpeed, fishMeta.getDamage(), fishMeta.getAttackSpeed()));
        else
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
            lore.add("");
            lore.add(whenHeldInOffHand);
            lore.addAll(equipEffects);
        }

        if (!fishMeta.getUseEffects().isEmpty()) {
            lore.add(" ");
            for (PotionEffect e : fishMeta.getUseEffects()) {
                lore.add(String.format(effectDuration, toReadable(e.getType().getName()), integerToRomanNumeral(e.getAmplifier() + 1), e.getDuration() / 20));
                lore.add(String.format(cooldown, fishMeta.getUseEffectCooldown()));
            }
        }

        for (FishAbility ability : fishMeta.getAbilities()) {
            if (!ability.getAbilityLore(0).isEmpty())
                lore.add(" ");
            lore.addAll(ability.getAbilityLore(fishMeta.getUseEffectCooldown()));
        }

        if (powerup != null)
            lore.addAll(powerup.getAttachedLore());

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

        fishItem = fish;

        return fish;
    }

    public int getLevel() {
        if (fishItem != null) {
            if (fishItem.getItemMeta() != null && fishItem.getItemMeta().getPersistentDataContainer().has(levelKey, PersistentDataType.INTEGER)) {
                Integer l = fishItem.getItemMeta().getPersistentDataContainer().get(levelKey, PersistentDataType.INTEGER);
                if (l == null)
                    return 1;
                level = l;
            }
        }
        return level;
    }

    public int getXp() {
        if (fishItem != null) {
            Integer experience = fishItem.getItemMeta().getPersistentDataContainer().get(xpKey, PersistentDataType.INTEGER);
            if (experience == null)
                return 0;
            xp = experience;
        }
        return xp;
    }

    private String toReadable(String string) {
        String[] names = string.split("_");
        for (int i = 0; i < names.length; i++) {
            names[i] = names[i].substring(0, 1) + names[i].substring(1).toLowerCase();
        }
        return StringUtils.join(names, " ");
    }

    public static String integerToRomanNumeral(int input) {
        String[] numerals = {"", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "XI", "XII", "XIII", "XIV", "XV"};

        if (input > 15)
            return "";

        return numerals[input];
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

    @EventHandler
    public void onUseFish(PlayerInteractEvent event) {
        Fish fish = Fish.getFromItemStack(event.getPlayer().getInventory().getItemInMainHand(), event.getPlayer());

        if (fish == null || !event.getAction().name().toLowerCase().contains("right"))
            return;

// debug        if (!fish.hasCooldown())
            fish.doUseEffect();

    }

    public ItemStack getFishItem() {
        return fishItem;
    }
}
