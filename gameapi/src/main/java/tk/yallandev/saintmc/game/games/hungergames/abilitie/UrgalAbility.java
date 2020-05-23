package tk.yallandev.saintmc.game.games.hungergames.abilitie;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import tk.yallandev.saintmc.game.ability.AbilityRarity;
import tk.yallandev.saintmc.game.constructor.Ability;
import tk.yallandev.saintmc.game.constructor.CustomOption;
import tk.yallandev.saintmc.game.interfaces.Disableable;

public class UrgalAbility extends Ability implements Disableable {

	public UrgalAbility() {
		super(new ItemStack(Material.POTION, 1, (short) 8201), AbilityRarity.RARE);
		options.put("ITEM", new CustomOption("ITEM", new ItemStack(Material.POTION, 3, (short) 8201), "�cUrgal"));
	}

	@Override
	public int getPowerPoints(HashMap<String, CustomOption> map) {
		return 36;
	}

}
