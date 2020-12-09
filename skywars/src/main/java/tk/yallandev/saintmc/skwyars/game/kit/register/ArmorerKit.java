package tk.yallandev.saintmc.skwyars.game.kit.register;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import tk.yallandev.saintmc.skwyars.game.kit.DefaultKit;

public class ArmorerKit extends DefaultKit {

	public ArmorerKit() {
		super("Armorer", new ItemStack(Material.IRON_CHESTPLATE), "");
	}

	@Override
	public void apply(Player player) {
		player.getInventory().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
		player.getInventory().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
	}

}
