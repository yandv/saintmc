package tk.yallandev.saintmc.game.games.hungergames.kit;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import tk.yallandev.saintmc.game.constructor.DefaultKit;
import tk.yallandev.saintmc.game.manager.AbilityManager;

public class StomperKit extends DefaultKit {

	public StomperKit() {
		super("stomper", "Esmague seus inimigos", new ItemStack(Material.IRON_BOOTS), new ArrayList<>());
		abilities.add(AbilityManager.getAbility("stomper"));
	}

}
