package tk.yallandev.saintmc.bukkit.event.player;

import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import lombok.Getter;
import tk.yallandev.saintmc.bukkit.event.PlayerCancellableEvent;

@Getter
public class PlayerKnockbackEvent extends PlayerCancellableEvent {

	private Player damager;
	private double horMultiplier;
	private double verMultiplier;
	private double sprintMultiplier;
	private double kbMultiplier;
	private double airMultiplier;
	private Vector knockback;

	public PlayerKnockbackEvent(Player player, Player damager, double horMultiplier, double verMultiplier,
			double sprintMultiplier, double kbMultiplier, double airMultiplier, Vector knockback) {
		super(player);
		this.damager = damager;
		this.horMultiplier = horMultiplier;
		this.verMultiplier = verMultiplier;
		this.sprintMultiplier = sprintMultiplier;
		this.kbMultiplier = kbMultiplier;
		this.airMultiplier = airMultiplier;
		this.knockback = knockback;
	}

}
