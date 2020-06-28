package tk.yallandev.saintmc.skwyars.event.kit;

import org.bukkit.entity.Player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import tk.yallandev.saintmc.skwyars.event.GameEvent;
import tk.yallandev.saintmc.skwyars.game.kit.Kit;

@Getter
@AllArgsConstructor
public class PlayerSelectedKitEvent extends GameEvent {

	private Player player;
	private Kit kit;

}
