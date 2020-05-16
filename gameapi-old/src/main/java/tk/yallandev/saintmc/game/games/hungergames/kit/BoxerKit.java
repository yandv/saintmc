package br.com.battlebits.game.games.hungergames.kit;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import br.com.battlebits.game.constructor.DefaultKit;
import br.com.battlebits.game.manager.AbilityManager;

public class BoxerKit extends DefaultKit {

	public BoxerKit() {
		super("boxer", "§%ability-boxer-description%§", new ItemStack(Material.STONE_SWORD), new ArrayList<>());
		abilities.add(AbilityManager.getAbility("boxer"));
		setOption("boxer", "DAMAGE", 2);
		setOption("boxer", "REDUCE", 1);
	}

}
