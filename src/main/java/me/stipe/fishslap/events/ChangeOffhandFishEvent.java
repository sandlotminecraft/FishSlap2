package me.stipe.fishslap.events;

import me.stipe.fishslap.types.Fish;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ChangeOffhandFishEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @NotNull
    public HandlerList getHandlers() {
        return handlers;
    }

    private Fish oldFish;
    private Fish newFish;
    private Player player;
    private boolean cancelled;


    public ChangeOffhandFishEvent(Player player, Fish oldFish, Fish newFish) {
        this.player = player;
        this.oldFish = oldFish;
        this.newFish = newFish;
        cancelled = false;
    }


    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        cancelled = b;
    }

    public Fish getOldFish() {
        return oldFish;
    }

    public Fish getNewFish() {
        return newFish;
    }

    public Player getPlayer() {
        return player;
    }
}
