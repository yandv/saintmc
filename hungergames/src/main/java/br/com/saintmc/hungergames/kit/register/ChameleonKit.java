package br.com.saintmc.hungergames.kit.register;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.kit.DefaultKit;

public class ChameleonKit extends DefaultKit {

	public ChameleonKit() {
		super("chameleon", "Transforme-se em mobs", new ItemStack(Material.WHEAT), 26000,
				Arrays.asList(StomperKit.class));
		setAbility(GameGeneral.getInstance().getAbilityController().getAbility("chameleon"));
	}

}
