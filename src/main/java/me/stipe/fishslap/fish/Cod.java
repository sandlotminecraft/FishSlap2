package me.stipe.fishslap.fish;

import me.stipe.fishslap.events.ChangeOffhandFishEvent;
import me.stipe.fishslap.events.FishSlapEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class Cod implements Listener {

    @EventHandler
    public void onFishSlap(FishSlapEvent event) {
        if (event.getFish().getType() == Fish.FishType.CRUSTY_COD) {
            // do what a cod does
        }
    }

    @EventHandler
    public void onChangeFish(ChangeOffhandFishEvent event) {
        if (event.getNewFish().getType() == Fish.FishType.CRUSTY_COD)
            addEquipEffect(event.getPlayer());
        if (event.getOldFish().getType() == Fish.FishType.CRUSTY_COD)
            removeEquipEffect(event.getPlayer());
    }

    public void addEquipEffect(Player player) {

    }

    public void removeEquipEffect(Player player) {

    }


}
