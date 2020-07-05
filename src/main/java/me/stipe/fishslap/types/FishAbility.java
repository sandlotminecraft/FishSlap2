package me.stipe.fishslap.types;

import me.stipe.fishslap.FSApi;
import me.stipe.fishslap.managers.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class FishAbility implements Listener {
    protected transient Set<String> abilityUsers = new HashSet<>();

    public List<Player> getUsers() {
        List<Player> players = new ArrayList<>();
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (abilityUsers.contains(p.getName()))
                players.add(p);
        }
        return players;
    }

    public void doRightClickEffect(Player player) {

    }

    public boolean hasAbility(Player player) {
        return abilityUsers.contains(player.getName());
    }

    public String getName() {
        return this.getClass().getSimpleName();
    }

    public boolean hasAbility(String name) {
        return abilityUsers.contains(name);
    }

    public void addUser(Player player) {
        PlayerManager pm = FSApi.getPlayerManager();

        if (pm.getAbilities().contains(this)) {
            abilityUsers.add(player.getName());
            return;
        }

        if (!pm.isRegistered(this))
            pm.registerAbility(this);

        pm.getAbility(this.getName()).addUser(player);
    }

    public void removeUser(Player player) {
        PlayerManager pm = FSApi.getPlayerManager();

        if (pm.getAbilities().contains(this)) {
            abilityUsers.remove(player.getName());
            if (abilityUsers.isEmpty())
                pm.unregisterAbility(this);
            return;
        }

        pm.getAbility(this.getName()).removeUser(player);

    }

    public List<String> getAbilityLore(int cooldown) {
        return new ArrayList<>();
    }
}
