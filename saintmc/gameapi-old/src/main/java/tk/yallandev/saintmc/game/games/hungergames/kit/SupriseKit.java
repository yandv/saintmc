package br.com.battlebits.game.games.hungergames.kit;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import br.com.battlebits.game.constructor.DefaultKit;
import br.com.battlebits.game.manager.AbilityManager;

public class SupriseKit extends DefaultKit {

	public SupriseKit() {
		super("surprise", "§%ability-surprise-description%§", new ItemStack(Material.CAKE), new ArrayList<>());
		abilities.add(AbilityManager.getAbility("surprise"));
	}

}
