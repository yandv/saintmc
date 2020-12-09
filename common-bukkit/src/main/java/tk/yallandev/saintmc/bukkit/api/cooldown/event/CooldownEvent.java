package tk.yallandev.saintmc.bukkit.api.cooldown.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import tk.yallandev.saintmc.bukkit.api.cooldown.types.Cooldown;

@RequiredArgsConstructor
public abstract class CooldownEvent extends Event {
	
    private static final HandlerList HANDLER_LIST = new HandlerList();

    @Getter
    @NonNull
    private Player player;

	@Getter
    @NonNull
    private Cooldown cooldown;
    
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}
