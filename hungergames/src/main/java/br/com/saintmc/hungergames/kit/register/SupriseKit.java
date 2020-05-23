package br.com.saintmc.hungergames.kit.register;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.kit.DefaultKit;

public class SupriseKit extends DefaultKit {

	public SupriseKit() {
		super("surprise", "Selecione um kit aleatório no inicio da partida", new ItemStack(Material.CAKE));
		setAbility(GameGeneral.getInstance().getAbilityController().getAbility("surprise"));
	}

}
