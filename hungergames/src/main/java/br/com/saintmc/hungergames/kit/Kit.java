package br.com.saintmc.hungergames.kit;

import java.util.Collection;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import br.com.saintmc.hungergames.abilities.Ability;

public interface Kit {
	
	String getName();
	
	String getDescription();
	
	ItemStack getKitIcon();
	
	Collection<Ability> getAbilities();
	
	void registerAbilities(Player player);

}
