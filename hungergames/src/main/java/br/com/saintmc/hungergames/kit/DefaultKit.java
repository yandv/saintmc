package br.com.saintmc.hungergames.kit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.abilities.Ability;
import lombok.Getter;
import lombok.Setter;

@Getter
public class DefaultKit implements Kit {

	private String name;
	private String description;
	private ItemStack kitIcon;

	private int price;

	@Setter
	private Ability ability;
	private List<Class<?>> notCompatibleList;

	public DefaultKit(String name, String description, ItemStack kitIcon, int price,
			List<Class<?>> notCompatibleList) {
		this.name = name;
		this.description = description;
		this.kitIcon = kitIcon;
		this.price = price;
		this.notCompatibleList = notCompatibleList;
	}

	public DefaultKit(String name, String description, ItemStack kitIcon, int price) {
		this(name, description, kitIcon, price, new ArrayList<>());
	}

	@Override
	public Collection<Ability> getAbilities() {
		return ability == null ? new ArrayList<>() : Arrays.asList(ability);
	}

	@Override
	public void registerAbilities(Player player) {
		if (ability != null) {
			GameGeneral.getInstance().getAbilityController().registerPlayerAbility(player,
					ability.getName().toLowerCase());
		}
	}

	@Override
	public boolean isNotCompatible(Class<? extends Kit> kitClazz) {
		return notCompatibleList.contains(kitClazz);
	}

}
