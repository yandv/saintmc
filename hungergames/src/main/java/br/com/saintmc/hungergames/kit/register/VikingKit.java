package br.com.saintmc.hungergames.kit.register;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.kit.DefaultKit;

public class VikingKit extends DefaultKit {

	public VikingKit() {
		super("viking", "Dê mais dano com machados", new ItemStack(Material.DIAMOND_AXE), 37000,
				Arrays.asList(BoxerKit.class, AssassinKit.class));
		setAbility(GameGeneral.getInstance().getAbilityController().getAbility("viking"));
	}

}
