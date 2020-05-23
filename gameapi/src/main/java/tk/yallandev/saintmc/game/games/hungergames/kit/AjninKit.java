package tk.yallandev.saintmc.game.games.hungergames.kit;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import tk.yallandev.saintmc.game.constructor.DefaultKit;
import tk.yallandev.saintmc.game.manager.AbilityManager;

public class AjninKit extends DefaultKit {

	public AjninKit() {
		super("ajnin", "Aperte SHIFT para teletransportar o ultimo jogador hitado até você", new ItemStack(Material.NETHER_STAR), new ArrayList<>());
		abilities.add(AbilityManager.getAbility("ajnin"));
	}

}
