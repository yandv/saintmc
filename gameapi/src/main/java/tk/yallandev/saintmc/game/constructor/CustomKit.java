package tk.yallandev.saintmc.game.constructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import tk.yallandev.saintmc.game.interfaces.Optional;
import tk.yallandev.saintmc.game.manager.AbilityManager;

public class CustomKit extends Kit implements Optional, Cloneable {
	
	protected HashMap<String, HashMap<String, CustomOption>> kitOptions;
	private List<String> abilityList;

	public CustomKit(String name, ItemStack item) {
		super(name.toLowerCase(), "CustomKit", item);
		this.kitOptions = new HashMap<>();
		this.abilityList = new ArrayList<>();
	}

	public CustomOption getOption(String abilityName, String optionName) {
		if (!kitOptions.containsKey(abilityName))
			return new CustomOption(-1);
		if (!kitOptions.get(abilityName).containsKey(optionName))
			return new CustomOption(-1);
		return kitOptions.get(abilityName).get(optionName);
	}

	public void removeOption(String abilityName) {
		if (!kitOptions.containsKey(abilityName))
			return;
		kitOptions.remove(abilityName);
	}

	public void addAbility(Ability ability) {
		abilityList.add(ability.getName().toLowerCase());
		getAbilities().add(ability);
	}

	public void removeAbility(Ability ability) {
		abilityList.remove(ability.getName().toLowerCase());
		getAbilities().remove(ability);
	}

	public void setOption(String abilityName, String optionName, int option) {
		setOption(abilityName, optionName, new CustomOption(option));
	}

	@Override
	public void setOption(String abilityName, String optionName, CustomOption option) {
		if (!kitOptions.containsKey(abilityName)) {
			kitOptions.put(abilityName, new HashMap<>());
		}
		kitOptions.get(abilityName).put(optionName, option);
	}

	public int getPowerPoints() {
		int powerPoints = 0;
		for (Ability ability : abilities) {
			powerPoints += ability.getPowerPoints(abilityOption(ability.getName()));
		}
		return powerPoints;
	}

	public HashMap<String, CustomOption> abilityOption(String abilityName) {
		return kitOptions.get(abilityName);
	}

	public List<String> getAbilityList() {
		return abilityList;
	}

	public void updateAbilities() {
		for (String str : abilityList) {
			getAbilities().add(AbilityManager.getAbility(str));
		}
	}

	@Override
	public void loadAbilities(Player player) {
		for (Ability str : getAbilities()) {
			AbilityManager.registerPlayerAbility(player, str.getName());
		}
	}

	@Override
	public CustomKit clone() {
		try {
			return (CustomKit) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}

}
