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
	
	private List<ItemStack> itemList;
	
	public Ability(String name, List<ItemStack> itemList) {
		this.myPlayers = new ArrayList<>();
		
		this.name = name;
		this.itemList = itemList;
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

	public boolean isAbilityItem(ItemStack item) {
		for (ItemStack kitItem : itemList) {
			if (kitItem.getType() == item.getType()) {
				if (kitItem.hasItemMeta() && item.hasItemMeta()) {
					if (kitItem.getItemMeta().hasDisplayName() && item.getItemMeta().hasDisplayName()) {
						if (item.getItemMeta().getDisplayName().equals(kitItem.getItemMeta().getDisplayName()))
							return true;
					} else if (!kitItem.getItemMeta().hasDisplayName() && !item.getItemMeta().hasDisplayName())
						return true;
				} else if (!kitItem.hasItemMeta() && !item.hasItemMeta())
					return true;
			}
			
			return false;
		}
		
		return false;
	}

}
