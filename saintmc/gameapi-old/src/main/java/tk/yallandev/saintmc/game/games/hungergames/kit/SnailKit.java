package br.com.battlebits.game.games.hungergames.kit;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import br.com.battlebits.game.constructor.DefaultKit;
import br.com.battlebits.game.manager.AbilityManager;

public class SnailKit extends DefaultKit {

	public SnailKit() {
		super("snail", "§%ability-snail-description%§", new ItemStack(Material.WEB), new ArrayList<>());
		abilities.add(AbilityManager.getAbility("snail"));
	}

}
