package me.stipe.fishslap.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;
import me.stipe.fishslap.Fish;

public class FishSlapEvent {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    private final Player slapper;
    private final Player target;
    private final EntityDamageByEntityEvent event;
    private final Fish fish;

    public FishSlapEvent(@NotNull Player slapper, @NotNull Player target, Fish fish, @NotNull EntityDamageByEntityEvent event) {
        this.slapper = slapper;
        this.target = target;
        this.event = event;
        this.fish = fish;
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

    public Fish getFish() {
        return fish;
    }

    @NotNull
    public HandlerList getHandlers() {
        return handlers;
    }

}
