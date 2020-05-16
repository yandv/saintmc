package tk.yallandev.saintmc.game.constructor;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class Kit {
	
	private String name;
	private transient String description;
	private Material iconMaterial;
	private short iconDurability;
	
	protected transient List<Ability> abilities = new ArrayList<>();
	private transient boolean actived = true;

	public Kit(String name, String description, ItemStack icon) {
		this.name = name;
		this.description = description;
		this.iconMaterial = icon.getType();
		this.iconDurability = icon.getDurability();
	}

	public void setName(String name) {
		this.name = name.toLowerCase();
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public void setIcon(ItemStack icon) {
		this.iconMaterial = icon.getType();
		this.iconDurability = icon.getDurability();
	}

	public ItemStack getIcon() {
		return new ItemStack(iconMaterial, 1, iconDurability);
	}

	public List<Ability> getAbilities() {
		if (abilities == null)
			abilities = new ArrayList<>();
		
		return abilities;
	}

	public boolean hasAbility(String abilityName) {
		for (Ability ability : abilities) {
			if (ability.getName().equalsIgnoreCase(abilityName))
				return true;
		}
		return false;
	}

	public Ability getAbility(String abilityName) {
		for (Ability ability : abilities) {
			if (ability.getName().equalsIgnoreCase(abilityName))
				return ability;
		}
		return null;
	}
	
	public boolean isActived() {
		return actived;
	}
	
	public void setActived(boolean actived) {
		this.actived = actived;
	}
	
	public abstract void loadAbilities(Player player);
}
