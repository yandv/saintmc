package br.com.saintmc.hungergames.kit.register;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.kit.DefaultKit;

public class KayaKit extends DefaultKit {

	public KayaKit() {
		super("kaya", "Crie blocos falsos nos quais irão sumir quando alguém passar por cima",
				new ItemStack(Material.GRASS), 22000);
		setAbility(GameGeneral.getInstance().getAbilityController().getAbility("kaya"));
	}

}
