package br.com.saintmc.hungergames.kit.register;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.kit.DefaultKit;

public class KangarooKit extends DefaultKit {

	public KangarooKit() {
		super("kangaroo", "Movimente-se mais rapido com seu kangaroo", new ItemStack(Material.FIREWORK), 36000,
				Arrays.asList(StomperKit.class));
		setAbility(GameGeneral.getInstance().getAbilityController().getAbility("kangaroo"));
	}

}
