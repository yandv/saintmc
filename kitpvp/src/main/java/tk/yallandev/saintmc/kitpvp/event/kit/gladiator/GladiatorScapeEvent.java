package tk.yallandev.saintmc.kitpvp.event.kit.gladiator;

import org.bukkit.entity.Player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import tk.yallandev.saintmc.bukkit.event.NormalEvent;

@Getter
@AllArgsConstructor
public class GladiatorScapeEvent extends NormalEvent {

	private Player gladiator;
	private Player player;

}
