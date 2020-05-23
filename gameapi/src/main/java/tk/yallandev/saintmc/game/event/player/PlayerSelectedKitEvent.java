package tk.yallandev.saintmc.game.event.player;

import org.bukkit.entity.Player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import tk.yallandev.saintmc.game.constructor.Kit;
import tk.yallandev.saintmc.game.event.Event;

@AllArgsConstructor
@Getter
public class PlayerSelectedKitEvent extends Event {

	private Player player;
	private Kit kit;
	private int kitIndex;

}
