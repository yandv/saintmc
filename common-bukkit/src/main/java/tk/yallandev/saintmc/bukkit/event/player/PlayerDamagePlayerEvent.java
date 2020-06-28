package tk.yallandev.saintmc.bukkit.event.player;

import org.bukkit.entity.Player;

import lombok.Getter;
import tk.yallandev.saintmc.bukkit.event.PlayerCancellableEvent;

@Getter
public class PlayerDamagePlayerEvent extends PlayerCancellableEvent {
	
	private Player damager;
	
	private double damage, finalDamage;
	
	public PlayerDamagePlayerEvent(Player entity, Player damager, boolean cancelled, double damage, double finalDamage) {
		super(entity);
		
		this.setCancelled(cancelled);
		this.damager = damager;
		this.damage = damage;
		this.finalDamage = finalDamage;
	}
	
	public void setDamage(double damage) {
		this.damage = damage;
	}

}
