package br.com.battlebits.game.games.hungergames.kit;

import java.util.ArrayList;

import org.bukkit.Material;

import br.com.battlebits.game.constructor.DefaultKit;
import br.com.battlebits.game.manager.AbilityManager;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;

public class MadmanKit extends DefaultKit {

	public MadmanKit() {
		super("madman", "§%ability-madman-description%§", new ItemBuilder().type(Material.POTION).durability(8232).build(), new ArrayList<>());
		abilities.add(AbilityManager.getAbility("madman"));
	}

}
