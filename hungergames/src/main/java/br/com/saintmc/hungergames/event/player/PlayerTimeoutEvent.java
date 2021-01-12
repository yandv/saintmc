package br.com.saintmc.hungergames.event.player;

import br.com.saintmc.hungergames.constructor.Gamer;
import lombok.Getter;
import tk.yallandev.saintmc.bukkit.event.PlayerCancellableEvent;

@Getter
public class PlayerTimeoutEvent extends PlayerCancellableEvent {
	
	private Gamer gamer;

	public PlayerTimeoutEvent(Gamer gamer) {
		super(null);
		this.gamer = gamer;
	}

}