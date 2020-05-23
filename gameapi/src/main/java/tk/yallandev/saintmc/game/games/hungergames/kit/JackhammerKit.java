package tk.yallandev.saintmc.game.games.hungergames.kit;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import tk.yallandev.saintmc.game.constructor.DefaultKit;
import tk.yallandev.saintmc.game.manager.AbilityManager;

public class JackhammerKit extends DefaultKit {

	public JackhammerKit() {
		super("jackhammer", "Receba ferros quando matar um jogador", new ItemStack(Material.STONE_AXE), new ArrayList<>());
		abilities.add(AbilityManager.getAbility("jackhammer"));
	}

}
