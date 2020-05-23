package br.com.saintmc.hungergames.kit.register;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.kit.DefaultKit;

public class WormKit extends DefaultKit {

	public WormKit() {
		super("worm", "Quebre terra rapidamente", new ItemStack(Material.DIRT));
		setAbility(GameGeneral.getInstance().getAbilityController().getAbility("worm"));
	}

}
