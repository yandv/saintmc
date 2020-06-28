package br.com.saintmc.hungergames.event.player;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.Setter;
import tk.yallandev.saintmc.bukkit.event.PlayerCancellableEvent;

@Getter
@Setter
public class PlayerDeathDropItemEvent extends PlayerCancellableEvent {
	
	private Location location;

	public PlayerDeathDropItemEvent(Player player, Location location) {
		super(player);
		this.location = location;
	}
	
}
