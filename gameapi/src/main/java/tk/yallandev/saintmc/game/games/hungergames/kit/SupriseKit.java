package tk.yallandev.saintmc.game.games.hungergames.kit;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import tk.yallandev.saintmc.game.constructor.DefaultKit;
import tk.yallandev.saintmc.game.manager.AbilityManager;

public class SupriseKit extends DefaultKit {

	public SupriseKit() {
		super("surprise", "Selecione um kit aleatório no inicio da partida", new ItemStack(Material.CAKE), new ArrayList<>());
		abilities.add(AbilityManager.getAbility("surprise"));
	}

}
