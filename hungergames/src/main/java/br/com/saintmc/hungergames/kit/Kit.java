package br.com.saintmc.hungergames.kit;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface Kit {
	
	String getName();
	
	String getDescription();
	
	ItemStack getKitIcon();
	
	void registerAbilities(Player player);

}
