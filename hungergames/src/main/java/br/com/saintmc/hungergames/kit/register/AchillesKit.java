package br.com.saintmc.hungergames.kit.register;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.kit.DefaultKit;

public class AchillesKit extends DefaultKit {

	public AchillesKit() {
		super("achilles", "Tome mais dano para itens de madeira e menos para outros itens", new ItemStack(Material.WOOD_SWORD));
		setAbility(GameGeneral.getInstance().getAbilityController().getAbility("achilles"));
	}

}
