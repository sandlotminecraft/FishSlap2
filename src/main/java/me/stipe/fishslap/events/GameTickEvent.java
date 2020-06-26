package me.stipe.fishslap.events;

import org.jetbrains.annotations.NotNull;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameTickEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public GameTickEvent() {
    }

    @NotNull
    public HandlerList getHandlers() {
        return handlers;
    }
}
