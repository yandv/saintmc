package tk.yallandev.saintmc.kitpvp.kit;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import lombok.Getter;
import tk.yallandev.saintmc.kitpvp.GameMain;

@Getter
public abstract class Kit implements Listener {

	private String kitName;
	private String kitDescription;
	private Material kitType;
	
	private boolean registred;

	public Kit(String kitName, String kitDescription, Material kitType) {
		this.kitName = kitName;
		this.kitDescription = kitDescription;
		this.kitType = kitType;
	}
	
	public void register() {
		if (registred)
			return;
		
		Bukkit.getPluginManager().registerEvents(this, GameMain.getInstance());
		registred = true;
	}
	
	public void unregister() {
		if (!registred)
			return;
		
		HandlerList.unregisterAll(this);
		registred = false;
	}
	
	public String getName() {
		return kitName;
	}
	
	public boolean hasAbility(Player player) {
		return GameMain.getInstance().getGamerManager().getGamer(player.getUniqueId()).hasKit(getKitName());
	}

	/**
	 * O inventário já vai estar pronto, só adicionar o kit no inv
	 */
	
	public abstract void applyKit(Player player);
}
