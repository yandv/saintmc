package br.com.saintmc.hungergames.kit.register;

import org.bukkit.Material;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.kit.DefaultKit;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;

public class MadmanKit extends DefaultKit {

	public MadmanKit() {
		super("madman", "Dê fraqueza nos inimigos ao seu redor", new ItemBuilder().type(Material.POTION).durability(8232).build());
		setAbility(GameGeneral.getInstance().getAbilityController().getAbility("madman"));
	}

}
