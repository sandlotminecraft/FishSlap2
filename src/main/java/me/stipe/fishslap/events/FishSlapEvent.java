package me.stipe.fishslap.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;

public class FishSlapEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    private final Player slapper;
    private final Player target;
    private final EntityDamageByEntityEvent event;
    private int xp;

    public FishSlapEvent(@NotNull Player slapper, @NotNull Player target, @NotNull EntityDamageByEntityEvent event) {
        this.slapper = slapper;
        this.target = target;
        this.event = event;
        this.xp = 0;
    }

    public Player getSlapper() {
        return slapper;
    }

    public Player getTarget() {
        return target;
    }

    public EntityDamageByEntityEvent getEvent() {
        return event;
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

}
