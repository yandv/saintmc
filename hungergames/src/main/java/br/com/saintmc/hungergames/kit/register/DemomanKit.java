package br.com.saintmc.hungergames.kit.register;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.kit.DefaultKit;

public class DemomanKit extends DefaultKit {

	public DemomanKit() {
		super("demoman", "Tenha a habilidade de montar uma mina e com ela exploda seus inimigos",
				new ItemStack(Material.GRAVEL), 23000,
				Arrays.asList(FishermanKit.class, AjninKit.class, TankKit.class, SwitcherKit.class));
		setAbility(GameGeneral.getInstance().getAbilityController().getAbility("demoman"));
	}
}
