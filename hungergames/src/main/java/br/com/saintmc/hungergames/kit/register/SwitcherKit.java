package br.com.saintmc.hungergames.kit.register;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.kit.DefaultKit;

public class SwitcherKit extends DefaultKit {

	public SwitcherKit() {
		super("switcher", "Troque de lugar com suas snowballs", new ItemStack(Material.SNOW_BALL), 25000,
				Arrays.asList(GladiatorKit.class, DemomanKit.class));
		setAbility(GameGeneral.getInstance().getAbilityController().getAbility("switcher"));
	}

}
