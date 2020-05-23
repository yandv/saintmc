package br.com.saintmc.hungergames.kit.register;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.kit.DefaultKit;

public class SwitcherKit extends DefaultKit {

	public SwitcherKit() {
		super("switcher", "Troque de lugar com suas snowballs", new ItemStack(Material.SNOW_BALL));
		setAbility(GameGeneral.getInstance().getAbilityController().getAbility("switcher"));
	}

}
