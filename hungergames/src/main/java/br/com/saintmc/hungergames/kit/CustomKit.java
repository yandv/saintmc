package br.com.saintmc.hungergames.kit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.abilities.Ability;
import lombok.Getter;
import lombok.Setter;

@Getter
public class CustomKit implements Kit {
	
	private String name;
	private String description;
	private ItemStack kitIcon;

	@Setter
	private List<Ability> abilityList = new ArrayList<>();

	public CustomKit(String name, String description, ItemStack kitIcon) {
		this.name = name;
		this.description = description;
		this.kitIcon = kitIcon;
	}
	
	public void addAbility(Ability ability) {
		if (!abilityList.contains(ability))
			abilityList.add(ability);
	}
	
	public void removeAbility(Ability ability) {
		if (abilityList.contains(ability))
			abilityList.remove(ability);
	}
	
	@Override
	public Collection<Ability> getAbilities() {
		return abilityList;
	}

	@Override
	public void registerAbilities(Player player) {
		for (Ability ability : this.abilityList)
			GameGeneral.getInstance().getAbilityController().registerPlayerAbility(player, ability.getName().toLowerCase());
	}

	@Override
	public int getPrice() {
		return Integer.MAX_VALUE;
	}

	@Override
	public boolean isNotCompatible(Class<? extends Kit> kitClazz) {
		return false;
	}

}
