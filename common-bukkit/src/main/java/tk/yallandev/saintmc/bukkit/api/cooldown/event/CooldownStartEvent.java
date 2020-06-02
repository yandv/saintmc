package tk.yallandev.saintmc.bukkit.api.cooldown.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

import lombok.Getter;
import lombok.Setter;
import tk.yallandev.saintmc.bukkit.api.cooldown.types.Cooldown;

public class CooldownStartEvent extends CooldownEvent implements Cancellable {
	
	@Getter
	@Setter
    private boolean cancelled;

    public CooldownStartEvent(Player player, Cooldown cooldown) {
        super(player, cooldown);
    }

}
