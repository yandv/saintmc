package br.com.battlebits.game.games.hungergames.abilitie;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import br.com.battlebits.game.ability.AbilityRarity;
import br.com.battlebits.game.constructor.Ability;
import br.com.battlebits.game.constructor.CustomOption;
import br.com.battlebits.game.interfaces.Disableable;

public class UrgalAbility extends Ability implements Disableable {

	public UrgalAbility() {
		super(new ItemStack(Material.POTION, 1, (short) 8201), AbilityRarity.RARE);
		options.put("ITEM", new CustomOption("ITEM", new ItemStack(Material.POTION, 3, (short) 8201), "§cUrgal"));
	}

	@Override
	public int getPowerPoints(HashMap<String, CustomOption> map) {
		return 36;
	}

}
