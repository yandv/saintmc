package tk.yallandev.saintmc.shadow.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import tk.yallandev.saintmc.bukkit.event.NormalEvent;
import tk.yallandev.saintmc.shadow.challenge.Challenge;

@Getter
@AllArgsConstructor
public class GladiatorFinishEvent extends NormalEvent {

	private Challenge challenge;
	private Player loser;
	private Player winner;

}
