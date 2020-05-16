package tk.yallandev.saintmc.game.event.player;

import org.bukkit.entity.Player;

import tk.yallandev.saintmc.game.event.Event;

public class PlayerSpectateEvent extends Event {

	private Player player;
	
	public PlayerSpectateEvent(Player player) {
		this.player = player;
	}
	
	public Player getPlayer() {
		return player;
	}
	
}
