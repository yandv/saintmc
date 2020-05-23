package tk.yallandev.saintmc.game.games.hungergames.kit;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import tk.yallandev.saintmc.game.constructor.DefaultKit;
import tk.yallandev.saintmc.game.manager.AbilityManager;

public class SwitcherKit extends DefaultKit {

	public SwitcherKit() {
		super("switcher", "Troque de lugar com suas snowballs", new ItemStack(Material.SNOW_BALL), new ArrayList<>());
		abilities.add(AbilityManager.getAbility("switcher"));
	}

}
