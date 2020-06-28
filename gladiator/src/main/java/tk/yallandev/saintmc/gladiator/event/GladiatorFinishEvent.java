package tk.yallandev.saintmc.gladiator.event;

import org.bukkit.entity.Player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import tk.yallandev.saintmc.bukkit.event.NormalEvent;
import tk.yallandev.saintmc.gladiator.challenge.Challenge;

@Getter
@AllArgsConstructor
public class GladiatorFinishEvent extends NormalEvent {

	private Challenge challenge;
	private Player loser;
	private Player winner;

}
