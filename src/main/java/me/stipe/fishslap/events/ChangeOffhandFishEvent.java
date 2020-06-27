package me.stipe.fishslap.events;

import me.stipe.fishslap.FishSlap;
import me.stipe.fishslap.fish.Fish;
import me.stipe.fishslap.managers.PlayerManager;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ChangeOffhandFishEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @NotNull
    public HandlerList getHandlers() {
        return handlers;
    }

    private final Player player;
    private final Fish oldFish;
    private final Fish newFish;

    public ChangeOffhandFishEvent(Player player, Fish oldFish, Fish newFish) {
        this.oldFish = oldFish;
        this.newFish = newFish;
        this.player = player;
        PlayerManager pm = FishSlap.getPlayerManager();

        // joining game
        if (!oldFish.isValid() && newFish.isValid()) {
            pm.addPlayer(player);
        }
        // leaving game
        else if (oldFish.isValid() && !newFish.isValid()) {
            pm.removePlayer(player);
        }

        if (oldFish.isValid())
            oldFish.updateXpBar();
    }


    public Player getPlayer() {
        return player;
    }

    public Fish getOldFish() {
        return oldFish;
    }

    public Fish getNewFish() {
        return newFish;
    }
}
