package tk.yallandev.saintmc.kitpvp.gamer;

import java.util.UUID;

import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.Setter;
import tk.yallandev.saintmc.kitpvp.kit.Kit;
import tk.yallandev.saintmc.kitpvp.warp.Warp;

@Getter
public class Gamer {
	
	private Player player;
	
	private Kit kit;
	private Warp warp;
	
	private long combatStart;
	
	@Setter
	private boolean spawnProtection;
	@Setter
	private boolean teleporting;
	@Setter
	private boolean blockCommand;

	public Gamer(Player player) {
		this.player = player;
		
		this.spawnProtection = true;
		
		this.combatStart = -1l;
	}
	
	public void setWarp(Warp warp) {
		this.warp = warp;
	}
	
	public boolean isInWarp(Warp warp) {
		return getWarp() == warp;
	}
	
	public boolean isInCombat() {
		return combatStart + 15000 > System.currentTimeMillis();
	}
	
	public void setCombat() {
		combatStart = System.currentTimeMillis();
	}
	
	public void removeCombat() {
		combatStart = Long.MIN_VALUE;
	}
	
	public void setKit(Kit kit) {
		this.kit = kit;
	}
	
	public boolean hasKit(Kit kit) {
		return this.kit != null && this.kit.getKitName().equalsIgnoreCase(kit.getKitName());
	}
	
	public boolean hasKit(String kitName) {
		return this.kit != null && this.kit.getKitName().equalsIgnoreCase(kitName);
	}
	
	public String getKitName() {
		return this.kit == null ? "Nenhum" : this.kit.getKitName();
	}
	
	public boolean hasKit() {
		return this.kit != null;
	}

	public UUID getUuid() {
		return player.getUniqueId();
	}
}
