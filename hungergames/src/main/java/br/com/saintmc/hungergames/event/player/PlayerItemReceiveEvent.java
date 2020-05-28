package br.com.saintmc.hungergames.event.player;

import org.bukkit.entity.Player;

import tk.yallandev.saintmc.bukkit.event.PlayerCancellableEvent;

public class PlayerItemReceiveEvent extends PlayerCancellableEvent {

	public PlayerItemReceiveEvent(Player player) {
		super(player);
	}

}
