package br.com.saintmc.hungergames.kit.register;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.kit.DefaultKit;

public class PhantomKit extends DefaultKit {

	public PhantomKit() {
		super("phantom", "Ganhe o poder de voar por 5 segundos", new ItemStack(Material.NETHER_STAR));
		setAbility(GameGeneral.getInstance().getAbilityController().getAbility("phantom"));
	}


}