package tk.yallandev.saintmc.game.games.hungergames.kit;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import tk.yallandev.saintmc.game.constructor.DefaultKit;
import tk.yallandev.saintmc.game.manager.AbilityManager;

public class GladiatorKit extends DefaultKit {

	public GladiatorKit() {
		super("gladiator", "Puxe um jogador clicando com o direito nele para um luta nos ceus", new ItemStack(Material.IRON_FENCE), new ArrayList<>());
		abilities.add(AbilityManager.getAbility("gladiator"));
	}

}
