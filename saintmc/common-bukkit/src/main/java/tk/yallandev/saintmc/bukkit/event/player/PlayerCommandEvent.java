package tk.yallandev.saintmc.bukkit.event.player;

import org.bukkit.entity.Player;

import lombok.Getter;
import tk.yallandev.saintmc.bukkit.event.PlayerCancellableEvent;

@Getter
public class PlayerCommandEvent extends PlayerCancellableEvent {
	
	private String commandLabel;

	public PlayerCommandEvent(Player player, String commandLabel) {
		super(player);
		this.commandLabel = commandLabel;
	}

}
