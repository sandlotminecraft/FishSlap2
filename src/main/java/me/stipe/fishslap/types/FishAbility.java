package me.stipe.fishslap.types;

import me.stipe.fishslap.events.ChangeOffhandFishEvent;
import me.stipe.fishslap.events.FishSlapEvent;
import org.bukkit.event.Listener;

public interface FishAbility extends Listener {

    public void onFishSlap(FishSlapEvent event);

    public void onEquipFish(ChangeOffhandFishEvent event);

}
