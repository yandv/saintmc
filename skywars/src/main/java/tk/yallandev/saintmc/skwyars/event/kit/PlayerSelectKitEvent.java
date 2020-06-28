package tk.yallandev.saintmc.skwyars.event.kit;

import org.bukkit.entity.Player;

import lombok.Getter;
import tk.yallandev.saintmc.bukkit.event.PlayerCancellableEvent;
import tk.yallandev.saintmc.skwyars.game.kit.Kit;
import tk.yallandev.saintmc.skwyars.gamer.Gamer;

@Getter
public class PlayerSelectKitEvent extends PlayerCancellableEvent {

	private Gamer gamer;
	private Kit kit;

	public PlayerSelectKitEvent(Player player, Gamer gamer, Kit kit) {
		super(player);
		this.gamer = gamer;
		this.kit = kit;
	}

}
