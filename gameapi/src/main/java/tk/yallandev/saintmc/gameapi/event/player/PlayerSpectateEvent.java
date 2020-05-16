package tk.yallandev.saintmc.gameapi.event.player;

import org.bukkit.entity.Player;

import tk.yallandev.saintmc.gameapi.event.Event;

public class PlayerSpectateEvent extends Event {

	private Player player;
	
	public PlayerSpectateEvent(Player player) {
		this.player = player;
	}
	
	public Player getPlayer() {
		return player;
	}
	
}
