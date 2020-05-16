package br.com.battlebits.game.games.hungergames.kit;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import br.com.battlebits.game.constructor.DefaultKit;
import br.com.battlebits.game.manager.AbilityManager;

public class GladiatorKit extends DefaultKit {

	public GladiatorKit() {
		super("gladiator", "§%ability-gladiator-description%§", new ItemStack(Material.IRON_FENCE), new ArrayList<>());
		abilities.add(AbilityManager.getAbility("gladiator"));
	}

}
