package br.com.battlebits.game.games.hungergames.kit;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import br.com.battlebits.game.constructor.DefaultKit;
import br.com.battlebits.game.manager.AbilityManager;

public class ViperKit extends DefaultKit {

	public ViperKit() {
		super("viper", "§%ability-viper-description%§", new ItemStack(Material.SPIDER_EYE), new ArrayList<>());
		abilities.add(AbilityManager.getAbility("viper"));
	}

}
