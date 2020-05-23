package tk.yallandev.saintmc.game.games.hungergames.kit;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import tk.yallandev.saintmc.game.constructor.DefaultKit;
import tk.yallandev.saintmc.game.manager.AbilityManager;

public class AchillesKit extends DefaultKit {

	public AchillesKit() {
		super("achilles", "Tome mais dano para itens de madeira e menos para outros itens", new ItemStack(Material.WOOD_SWORD), new ArrayList<>());
		abilities.add(AbilityManager.getAbility("achilles"));
	}

}
