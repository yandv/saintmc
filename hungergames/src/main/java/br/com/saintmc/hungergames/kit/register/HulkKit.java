package br.com.saintmc.hungergames.kit.register;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.kit.DefaultKit;

public class HulkKit extends DefaultKit {

	public HulkKit() {
		super("hulk", "Pegue e esmague seus inimigos", new ItemStack(Material.DISPENSER), 28000,
				Arrays.asList(PhantomKit.class));
		setAbility(GameGeneral.getInstance().getAbilityController().getAbility("hulk"));
	}

}
