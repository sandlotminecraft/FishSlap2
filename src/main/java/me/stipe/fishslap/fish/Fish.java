package me.stipe.fishslap.fish;

import me.stipe.fishslap.abilities.Cod;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class Fish {
    private final Player owner;
    private final ItemStack item;
    private Integer level;
    private Integer xp;
    private final FishType type;
    private FishMeta meta;


    public enum FishType { CRUSTY_COD, STINKY_SALMON, GRODY_GROUPER, PRICKLY_PUFFERFISH, INVALID }

    public Fish(Player owner, ItemStack item) {
        this.owner = owner;
        this.item = item;

        if (item == null) {
            type = FishType.INVALID;
            return;
        }

        NamespacedKey key = new NamespacedKey(Bukkit.getPluginManager().getPlugin("FishSlap"), "level");
        if (item.getItemMeta() != null && item.getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.INTEGER)) {
            level = item.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.INTEGER);
        }
        else
            level = 0;

        key = new NamespacedKey(Bukkit.getPluginManager().getPlugin("FishSlap"), "xp");
        if (item.getItemMeta() != null && item.getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.INTEGER)) {
            xp = item.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.INTEGER);
        }
        else
            this.xp = 0;

        switch (item.getType()) {
            case COD:
                type = FishType.CRUSTY_COD;
                meta = new Cod(level);
                break;
            case SALMON:
                type = FishType.STINKY_SALMON;
                break;
            case TROPICAL_FISH:
                type = FishType.GRODY_GROUPER;
                break;
            case PUFFERFISH:
                type = FishType.PRICKLY_PUFFERFISH;
                break;
            default:
                type = FishType.INVALID;
        }


    }

    public Integer getLevel() {
        return level;
    }

    public FishType getType() {
        return type;
    }

    public void addXp(int amount) {

    }

    public boolean isValid() {
        return type != FishType.INVALID;
    }

    public boolean isOnCooldown() {
        return (owner.hasCooldown(item.getType()));
    }

    private void levelUp() {

    }

    public void updateXpBar() {

    }

    public FishMeta getMeta() {
        return meta;
    }

    public Integer getXp() {
        return xp;
    }
}
