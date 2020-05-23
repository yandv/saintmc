package br.com.saintmc.hungergames.kit.register;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.kit.DefaultKit;

public class UrgalKit extends DefaultKit {

	public UrgalKit() {
		super("urgal", "Fique mais forte tomando uma pocao de força", new ItemStack(Material.POTION, 1, (short) 8201));
		setAbility(GameGeneral.getInstance().getAbilityController().getAbility("urgal"));
	}

}
