package tk.yallandev.saintmc.game.games.hungergames.kit;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import tk.yallandev.saintmc.game.constructor.DefaultKit;
import tk.yallandev.saintmc.game.manager.AbilityManager;

public class IronmanKit extends DefaultKit {

	public IronmanKit() {
		super("ironman", "Receba ferros quando matar um jogador", new ItemStack(Material.IRON_INGOT), new ArrayList<>());
		abilities.add(AbilityManager.getAbility("ironman"));
	}

}
