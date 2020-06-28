package br.com.saintmc.hungergames.kit.register;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.kit.DefaultKit;

public class TimelordKit extends DefaultKit {

	public TimelordKit() {
		super("timelord", "Pare o tempo com seu relógio", new ItemStack(Material.WATCH), 23400);
		setAbility(GameGeneral.getInstance().getAbilityController().getAbility("timelord"));
	}

}
