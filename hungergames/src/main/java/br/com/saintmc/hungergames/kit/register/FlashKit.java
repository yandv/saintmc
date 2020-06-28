package br.com.saintmc.hungergames.kit.register;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.kit.DefaultKit;

public class FlashKit extends DefaultKit {

	public FlashKit() {
		super("flash", "Movimente-se tão rapido que parecerá que você está se teletransportando",
				new ItemStack(Material.REDSTONE_TORCH_ON), 31000,
				Arrays.asList(StomperKit.class));
		setAbility(GameGeneral.getInstance().getAbilityController().getAbility("flash"));
	}

}
