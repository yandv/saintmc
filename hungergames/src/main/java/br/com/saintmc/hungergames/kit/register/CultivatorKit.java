package br.com.saintmc.hungergames.kit.register;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.kit.DefaultKit;

public class CultivatorKit extends DefaultKit {

	public CultivatorKit() {
		super("cultivator", "Cultive plantas rapidamente",
				new ItemStack(Material.SAPLING), 17000);
		setAbility(GameGeneral.getInstance().getAbilityController().getAbility("cultivator"));
	}

}
