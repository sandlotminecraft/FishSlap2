package me.stipe.fishslap;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class Cod extends Fish implements Listener {

    public Cod(Player owner, ItemStack item) {
        super(owner, item);
    }
}
