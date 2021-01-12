package br.com.saintmc.hungergames.event.ability;

import org.bukkit.entity.Player;

import lombok.Getter;
import tk.yallandev.saintmc.bukkit.event.PlayerCancellableEvent;

@Getter
public class PlayerNinjaTeleportEvent extends PlayerCancellableEvent {
	
	private Player target;

	public PlayerNinjaTeleportEvent(Player player, Player target) {
		super(player);
		this.target = target;
	}

}
