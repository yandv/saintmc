package tk.yallandev.saintmc.bukkit.api.cooldown.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import tk.yallandev.saintmc.bukkit.api.cooldown.types.Cooldown;

public class CooldownFinishEvent extends CooldownEvent {

    public CooldownFinishEvent(Player player, Cooldown cooldown) {
        super(player, cooldown);
    }

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
