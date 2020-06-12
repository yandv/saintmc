package tk.yallandev.saintmc.bukkit.event.player;

import org.bukkit.entity.Player;

import lombok.Getter;
import tk.yallandev.saintmc.bukkit.event.PlayerCancellableEvent;

@Getter
public class PlayerScoreboardStateEvent extends PlayerCancellableEvent {
	
	private boolean scoreboardEnabled;
	
	public PlayerScoreboardStateEvent(Player player, boolean scoreboardEnabled) {
		super(player);
		this.scoreboardEnabled = scoreboardEnabled;
	}

}
