package br.com.battlebits.game.games.hungergames.kit;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import br.com.battlebits.game.constructor.DefaultKit;
import br.com.battlebits.game.manager.AbilityManager;

public class StomperKit extends DefaultKit {

	public StomperKit() {
		super("stomper", "§%ability-stomper-description%§", new ItemStack(Material.IRON_BOOTS), new ArrayList<>());
		abilities.add(AbilityManager.getAbility("stomper"));
	}

}
