package me.stipe.fishslap.abilities;

import me.stipe.fishslap.types.FishAbility;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class RemovePoison extends FishAbility {
    @Override
    public void doRightClickEffect(Player player) {
        player.removePotionEffect(PotionEffectType.POISON);
        player.removePotionEffect(PotionEffectType.WITHER);
    }

    @Override
    public List<String> getAbilityLore(int cooldown) {
        List<String> meta = new ArrayList<>();

        meta.add(ChatColor.GREEN + "Use: Removes all Poison and Wither");
        meta.add(ChatColor.GREEN + "effects (" + cooldown + " second cooldown)");

        return meta;
    }
}
