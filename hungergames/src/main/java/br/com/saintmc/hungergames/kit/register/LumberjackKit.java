package br.com.saintmc.hungergames.kit.register;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.kit.DefaultKit;

public class LumberjackKit extends DefaultKit {

	public LumberjackKit() {
		super("lumberjack", "Quebre árvores como um verdadeiro lenhador!", new ItemStack(Material.WOOD_AXE));
		setAbility(GameGeneral.getInstance().getAbilityController().getAbility("lumberjack"));
	}

}
