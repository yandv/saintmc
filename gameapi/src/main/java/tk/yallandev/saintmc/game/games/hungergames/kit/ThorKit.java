package tk.yallandev.saintmc.game.games.hungergames.kit;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import tk.yallandev.saintmc.game.constructor.DefaultKit;
import tk.yallandev.saintmc.game.manager.AbilityManager;

public class ThorKit extends DefaultKit {

	public ThorKit() {
		super("thor", "Lance raios com o seu machado", new ItemStack(Material.WOOD_AXE), new ArrayList<>());
		abilities.add(AbilityManager.getAbility("thor"));
	}

}
