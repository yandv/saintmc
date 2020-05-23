package tk.yallandev.saintmc.game.games.hungergames.kit;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import tk.yallandev.saintmc.game.constructor.DefaultKit;
import tk.yallandev.saintmc.game.manager.AbilityManager;

public class NinjaKit extends DefaultKit {

	public NinjaKit() {
		super("ninja", "Aperte SHIFT para teletransportar-se para o ultimo jogador hitado", new ItemStack(Material.NETHER_STAR), new ArrayList<>());
		abilities.add(AbilityManager.getAbility("ninja"));
	}

}
