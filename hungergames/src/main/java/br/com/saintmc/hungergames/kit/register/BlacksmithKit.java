package br.com.saintmc.hungergames.kit.register;

import org.bukkit.Material;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.kit.DefaultKit;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;

public class BlacksmithKit extends DefaultKit {

	public BlacksmithKit() {
		super("blacksmith", "Como um ferreiro, remende todos os items do seu inventário",
				new ItemBuilder().type(Material.ANVIL).build(), 14000);
		setAbility(GameGeneral.getInstance().getAbilityController().getAbility("blacksmith"));
	}

}
