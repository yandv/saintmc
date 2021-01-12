package br.com.saintmc.hungergames.event.player;

import org.bukkit.entity.Player;

import tk.yallandev.saintmc.bukkit.event.PlayerCancellableEvent;

public class PlayerSpectateEvent extends PlayerCancellableEvent {

	public PlayerSpectateEvent(Player player) {
		super(player);
	}

}
