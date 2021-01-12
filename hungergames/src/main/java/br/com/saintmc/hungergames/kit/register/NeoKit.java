package br.com.saintmc.hungergames.kit.register;

import org.bukkit.Material;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.kit.DefaultKit;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;

public class NeoKit extends DefaultKit {

	public NeoKit() {
		super("neo", "Reflita projéteis e não seja afetado pelos kits Gladiator, Ninja, Ajnin, Endermage e Ultimato",
				new ItemBuilder().type(Material.ARROW).build(), 23000);
		setAbility(GameGeneral.getInstance().getAbilityController().getAbility("neo"));
	}

}
