package tk.yallandev.saintmc.game.constructor;

import java.util.HashMap;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import tk.yallandev.saintmc.game.interfaces.Optional;
import tk.yallandev.saintmc.game.manager.AbilityManager;

public class DefaultKit extends Kit implements Optional {
	
	protected HashMap<String, HashMap<String, CustomOption>> kitOptions;

	public DefaultKit(String name, String description, ItemStack icon, List<Ability> abilities) {
		super(name, description, icon);
		this.abilities = abilities;
		this.kitOptions = new HashMap<>();
	}

	public CustomOption getOption(String abilityName, String optionName) {
		if (!kitOptions.containsKey(abilityName))
			return new CustomOption(-1);
		
		if (!kitOptions.get(abilityName).containsKey(optionName))
			return new CustomOption(-1);
		
		return kitOptions.get(abilityName).get(optionName);
	}

	public void setOption(String abilityName, String optionName, int option) {
		setOption(abilityName, optionName, new CustomOption(option));
	}

	public void setOption(String abilityName, String optionName, CustomOption value) {
		if (!kitOptions.containsKey(abilityName)) {
			kitOptions.put(abilityName, new HashMap<>());
		}
		kitOptions.get(abilityName).put(optionName, value);
	}

	@Override
	public void loadAbilities(Player player) {
		for (Ability abl : abilities) {
			AbilityManager.registerPlayerAbility(player, abl.getName().toLowerCase());
		}
	}

}
