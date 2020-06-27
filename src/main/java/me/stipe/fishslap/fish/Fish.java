package me.stipe.fishslap.fish;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class Fish {
    private Player owner;
    private ItemStack item;
    private Integer level;
    private Integer xp;
    private FishType type;

    public enum FishType { CRUSTY_COD, STINKY_SALMON, GRODY_GROUPER, PRICKLY_PUFFERFISH, INVALID }

    public Fish(Player owner, ItemStack item) {
        this.owner = owner;
        this.item = item;

        if (item == null) {
            this.type = FishType.INVALID;
            return;
        }

        switch (item.getType()) {
            case COD:
                this.type = FishType.CRUSTY_COD;
                break;
            case SALMON:
                this.type = FishType.STINKY_SALMON;
                break;
            case TROPICAL_FISH:
                this.type = FishType.GRODY_GROUPER;
                break;
            case PUFFERFISH:
                this.type = FishType.PRICKLY_PUFFERFISH;
                break;
            default:
                this.type = FishType.INVALID;
                return;
        }

        NamespacedKey key = new NamespacedKey(Bukkit.getPluginManager().getPlugin("FishSlap"), "level");
        if (item.getItemMeta() != null && item.getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.INTEGER)) {
            this.level = item.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.INTEGER);
        }
        else
            this.level = 0;

        key = new NamespacedKey(Bukkit.getPluginManager().getPlugin("FishSlap"), "xp");
        if (item.getItemMeta() != null && item.getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.INTEGER)) {
            this.xp = item.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.INTEGER);
        }
        else
            this.xp = 0;

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
}
