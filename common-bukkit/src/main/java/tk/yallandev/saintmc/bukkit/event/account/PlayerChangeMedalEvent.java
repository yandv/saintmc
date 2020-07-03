package tk.yallandev.saintmc.bukkit.event.account;

import org.bukkit.entity.Player;

import lombok.Getter;
import tk.yallandev.saintmc.bukkit.event.PlayerCancellableEvent;
import tk.yallandev.saintmc.common.medals.Medal;

@Getter
public class PlayerChangeMedalEvent extends PlayerCancellableEvent {
	
	private Medal oldMedal;
	private Medal medal;

	public PlayerChangeMedalEvent(Player player, Medal oldMedal, Medal medal) {
		super(player);
		this.oldMedal = oldMedal;
		this.medal = medal;
	}

}
