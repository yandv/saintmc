package br.com.battlebits.game.games.hungergames.kit;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import br.com.battlebits.game.constructor.DefaultKit;
import br.com.battlebits.game.manager.AbilityManager;

public class AchillesKit extends DefaultKit {

	public AchillesKit() {
		super("achilles", "§%ability-achilles-description%§", new ItemStack(Material.WOOD_SWORD), new ArrayList<>());
		abilities.add(AbilityManager.getAbility("achilles"));
	}

}
