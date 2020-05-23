package br.com.saintmc.hungergames.kit.register;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.kit.DefaultKit;

public class CannibalKit extends DefaultKit {

	public CannibalKit() {
		super("cannibal", "Ao bater em algum player ira deixa-lo com fome e a sua recuperará", new ItemStack(Material.RAW_FISH));
		setAbility(GameGeneral.getInstance().getAbilityController().getAbility("achilles"));
	}
}
