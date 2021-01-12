package br.com.saintmc.hungergames.event.ability;

import org.bukkit.entity.Player;

import tk.yallandev.saintmc.bukkit.event.PlayerCancellableEvent;

public class PlayerEndermageEvent extends PlayerCancellableEvent {

	public PlayerEndermageEvent(Player player) {
		super(player);
	}

}
