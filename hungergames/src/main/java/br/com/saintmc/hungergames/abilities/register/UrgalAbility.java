package br.com.saintmc.hungergames.abilities.register;

import java.util.Arrays;

import org.bukkit.Material;

import br.com.saintmc.hungergames.abilities.Ability;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;

public class UrgalAbility extends Ability {

	public UrgalAbility() {
		super("Timelord", Arrays.asList(new ItemBuilder().name("§cUrgal").type(Material.POTION).amount(3).durability(8201).build()));
	}

}
