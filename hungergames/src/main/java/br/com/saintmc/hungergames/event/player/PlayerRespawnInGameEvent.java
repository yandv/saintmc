package br.com.saintmc.hungergames.event.player;

import org.bukkit.entity.Player;

import tk.yallandev.saintmc.bukkit.event.PlayerCancellableEvent;

public class PlayerRespawnInGameEvent extends PlayerCancellableEvent {

	public PlayerRespawnInGameEvent(Player player) {
		super(player);
	}

}
