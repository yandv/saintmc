package br.com.saintmc.hungergames.event.player;

import org.bukkit.entity.Player;

import tk.yallandev.saintmc.bukkit.event.PlayerCancellableEvent;

public class PlayerTimeoutEvent extends PlayerCancellableEvent {

	public PlayerTimeoutEvent(Player player) {
		super(player);
	}

}