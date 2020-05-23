package tk.yallandev.saintmc.game.games.hungergames.kit;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import tk.yallandev.saintmc.game.constructor.DefaultKit;
import tk.yallandev.saintmc.game.manager.AbilityManager;

public class WormKit extends DefaultKit {

	public WormKit() {
		super("worm", "Quebre terra rapidamente", new ItemStack(Material.DIRT), new ArrayList<>());
		abilities.add(AbilityManager.getAbility("worm"));
	}

}
