package br.com.saintmc.hungergames.kit.register;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.kit.DefaultKit;

public class SurpriseKit extends DefaultKit {

	public SurpriseKit() {
		super("surprise", "Selecione um kit aleatório no inicio da partida", new ItemStack(Material.CAKE), 10000);
		setAbility(GameGeneral.getInstance().getAbilityController().getAbility("surprise"));
	}

}
