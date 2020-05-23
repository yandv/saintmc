package br.com.saintmc.hungergames.abilities;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.GameMain;
import br.com.saintmc.hungergames.game.GameState;
import lombok.Getter;

@Getter
public abstract class Ability implements Listener {
	
	private List<UUID> myPlayers;
	
	private String name;
	private String description;
	private ItemStack icon;
	
	public Ability(String name, String description, ItemStack icon) {
		this.myPlayers = new ArrayList<>();
		
		this.name = name;
		this.description = description;
		this.icon = icon;
	}
	
	public boolean hasAbility(UUID uuid) {
		return myPlayers.contains(uuid);
	}
	
	public boolean hasAbility(Player p) {
		return hasAbility(p.getUniqueId());
	}
	
	public void registerPlayer(Player player) {
		if (!GameState.isPregame(GameGeneral.getInstance().getGameState()) && myPlayers.size() == 0) {
			Bukkit.getPluginManager().registerEvents(this, GameMain.getInstance());
		}
		
		myPlayers.add(player.getUniqueId());
	}

	public void unregisterPlayer(Player player) {
		myPlayers.remove(player.getUniqueId());
		
		if (!GameState.isPregame(GameGeneral.getInstance().getGameState()) && myPlayers.size() == 0) {
			HandlerList.unregisterAll(this);
		}
	}

}
