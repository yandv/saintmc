package br.com.saintmc.hungergames.kit.register;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.kit.DefaultKit;

public class LauncherKit extends DefaultKit {

	public LauncherKit() {
		super("launcher", "Pule alto com suas esponjas!", new ItemStack(Material.SPONGE), 31000,
				Arrays.asList(FishermanKit.class, StomperKit.class, AjninKit.class));
		setAbility(GameGeneral.getInstance().getAbilityController().getAbility("launcher"));
	}

}
