package tk.yallandev.saintmc.game.games.hungergames.kit;

import java.util.ArrayList;

import org.bukkit.Material;

import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.game.constructor.DefaultKit;
import tk.yallandev.saintmc.game.manager.AbilityManager;

public class MadmanKit extends DefaultKit {

	public MadmanKit() {
		super("madman", "Dê fraqueza nos inimigos ao seu redor", new ItemBuilder().type(Material.POTION).durability(8232).build(), new ArrayList<>());
		abilities.add(AbilityManager.getAbility("madman"));
	}

}
