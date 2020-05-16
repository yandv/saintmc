package tk.yallandev.saintmc.bukkit.event.vanish;

import org.bukkit.entity.Player;

import lombok.Getter;
import tk.yallandev.saintmc.bukkit.event.PlayerCancellableEvent;

@Getter
public class PlayerShowToPlayerEvent extends PlayerCancellableEvent {
	private Player toPlayer;

	public PlayerShowToPlayerEvent(Player player, Player toPlayer) {
		super(player);
		this.toPlayer = toPlayer;
	}
}
