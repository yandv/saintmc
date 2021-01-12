package br.com.saintmc.hungergames.kit.register;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.kit.DefaultKit;

public class EndermageKit extends DefaultKit {

	public EndermageKit() {
		super("endermage", "Teleporte jogadores até você usando o seu portal",
				new ItemStack(Material.ENDER_PORTAL_FRAME), 22000, Arrays.asList(AjninKit.class));
		setAbility(GameGeneral.getInstance().getAbilityController().getAbility("endermage"));
	}
}
