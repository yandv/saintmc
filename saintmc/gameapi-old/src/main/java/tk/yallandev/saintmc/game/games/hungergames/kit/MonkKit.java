package br.com.battlebits.game.games.hungergames.kit;

import java.util.ArrayList;

import org.bukkit.Material;

import br.com.battlebits.game.constructor.DefaultKit;
import br.com.battlebits.game.manager.AbilityManager;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;

public class MonkKit extends DefaultKit {

	public MonkKit() {
		super("monk", "§%ability-monk-description%§", new ItemBuilder().type(Material.BLAZE_ROD).build(), new ArrayList<>());
		abilities.add(AbilityManager.getAbility("monk"));
	}

}
