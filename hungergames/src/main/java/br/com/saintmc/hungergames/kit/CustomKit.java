package br.com.saintmc.hungergames.kit;

import java.util.Arrays;
import java.util.Collection;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.abilities.Ability;
import br.com.saintmc.hungergames.abilities.register.DefaultAbility;
import lombok.Getter;
import lombok.Setter;

@Getter
public class CustomKit implements Kit {
	
	private String name;
	private String description;
	private ItemStack kitIcon;

	@Setter
	private Ability ability = new DefaultAbility();

	public CustomKit(String name, String description, ItemStack kitIcon) {
		this.name = name;
		this.description = description;
		this.kitIcon = kitIcon;
	}
	
	@Override
	public Collection<Ability> getAbilities() {
		return Arrays.asList(ability);
	}

	@Override
	public void registerAbilities(Player player) {
		GameGeneral.getInstance().getAbilityController().registerPlayerAbility(player, ability.getName().toLowerCase());
	}

}
