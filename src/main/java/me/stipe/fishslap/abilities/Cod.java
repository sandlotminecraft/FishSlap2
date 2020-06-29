package me.stipe.fishslap.abilities;

import me.stipe.fishslap.FSApi;
import me.stipe.fishslap.events.ChangeOffhandFishEvent;
import me.stipe.fishslap.events.FishSlapEvent;
import me.stipe.fishslap.types.Fish;
import me.stipe.fishslap.types.FishAbility;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class Cod extends Fish implements FishAbility {

    public Cod(Player player) {
        super(FSApi.getConfigManager().getCodConfig(), Material.COD, player);
    }

    @EventHandler
    public void onFishSlap(FishSlapEvent event) {

    }

    @Override
    public void onEquipFish(ChangeOffhandFishEvent event) {

    }
}
