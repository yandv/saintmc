package tk.yallandev.saintmc.shadow.gamer;

import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.UUID;

@Getter
public class Gamer {

	private Player player;

	private long combatStart;

	public Gamer(Player player) {
		this.player = player;

		this.combatStart = -1l;
	}

	public boolean isInCombat() {
		return combatStart + 10000 > System.currentTimeMillis();
	}

	public void setCombat() {
		combatStart = System.currentTimeMillis();
	}

	public void removeCombat() {
		combatStart = Long.MIN_VALUE;
	}

	public UUID getUuid() {
		return player.getUniqueId();
	}
}
