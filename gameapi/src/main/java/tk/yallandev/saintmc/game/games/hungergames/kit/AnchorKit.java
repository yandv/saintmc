package tk.yallandev.saintmc.game.games.hungergames.kit;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import tk.yallandev.saintmc.game.constructor.DefaultKit;
import tk.yallandev.saintmc.game.manager.AbilityManager;

public class AnchorKit extends DefaultKit {

	public AnchorKit() {
		super("anchor", "Se prenda ao chão e não saia dele", new ItemStack(Material.ANVIL), new ArrayList<>());
		abilities.add(AbilityManager.getAbility("anchor"));
	}

}
