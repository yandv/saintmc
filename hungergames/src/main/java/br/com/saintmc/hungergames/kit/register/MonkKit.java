package br.com.saintmc.hungergames.kit.register;

import org.bukkit.Material;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.kit.DefaultKit;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;

public class MonkKit extends DefaultKit {

	public MonkKit() {
		super("monk", "Desarme seu inimigo usando seu Blaze Rod", new ItemBuilder().type(Material.BLAZE_ROD).build(), 23000);
		setAbility(GameGeneral.getInstance().getAbilityController().getAbility("monk"));
	}

}
