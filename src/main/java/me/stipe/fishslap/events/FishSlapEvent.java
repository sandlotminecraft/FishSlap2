package me.stipe.fishslap.events;

import me.stipe.fishslap.types.Fish;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;

public class FishSlapEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    private final Player slapper;
    private final Player target;
    private int xp;
    private Fish fish;
    private boolean cancelled;

    public FishSlapEvent(@NotNull Player slapper, @NotNull Player target, @NotNull Fish fish) {
        this.slapper = slapper;
        this.target = target;
        this.fish = fish;
        this.xp = 0;
    }

    public Player getSlapper() {
        return slapper;
    }

    public Player getTarget() {
        return target;
    }

    public Fish getFish() {
        return fish;
    }

    public int getXp() {
        return xp;
    }

    public void setXP(int amount) {
        this.xp = amount;
    }

    @NotNull
    public HandlerList getHandlers() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        cancelled = b;
    }
}
