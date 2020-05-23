package br.com.saintmc.hungergames.kit;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.abilities.Ability;
import br.com.saintmc.hungergames.abilities.register.DefaultAbility;
import lombok.Getter;
import lombok.Setter;

@Getter
public class DefaultKit implements Kit {

	private String name;
	private String description;
	private ItemStack kitIcon;

	@Setter
	private Ability ability = new DefaultAbility();

	public DefaultKit(String name, String description, ItemStack kitIcon) {
		this.name = name;
		this.description = description;
		this.kitIcon = kitIcon;
	}

	@Override
	public void registerAbilities(Player player) {
		GameGeneral.getInstance().getAbilityController().registerPlayerAbility(player, ability.getName().toLowerCase());
	}

}
