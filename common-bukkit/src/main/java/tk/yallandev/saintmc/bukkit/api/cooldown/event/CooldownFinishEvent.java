package tk.yallandev.saintmc.bukkit.api.cooldown.event;

import org.bukkit.entity.Player;

import tk.yallandev.saintmc.bukkit.api.cooldown.types.Cooldown;

public class CooldownFinishEvent extends CooldownStopEvent {

	public CooldownFinishEvent(Player player, Cooldown cooldown) {
		super(player, cooldown);
	}
	
}
