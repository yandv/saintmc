package br.com.saintmc.hungergames.kit.register;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.kit.DefaultKit;

public class SpecialistKit extends DefaultKit {

	public SpecialistKit() {
		super("specialist",
				"Mate jogadores e ganhe experiência para encantar seus itens usando sua mesa de encantamento portátil",
				new ItemStack(Material.BOOK), 34000);
		setAbility(GameGeneral.getInstance().getAbilityController().getAbility("specialist"));
	}

}
