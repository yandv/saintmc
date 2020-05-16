package br.com.battlebits.game.games.hungergames.kit;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import br.com.battlebits.game.constructor.DefaultKit;
import br.com.battlebits.game.manager.AbilityManager;

public class KangarooKit extends DefaultKit {

	public KangarooKit() {
		super("kangaroo", "§%ability-kangaroo-description%§", new ItemStack(Material.FIREWORK), new ArrayList<>());
		abilities.add(AbilityManager.getAbility("kangaroo"));
		setOption("kangaroo", "COOLDOWN", 5);
	}

}
