package tk.yallandev.saintmc.skwyars.game.kit.register;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import tk.yallandev.saintmc.skwyars.game.kit.DefaultKit;

public class GuerreiroKit extends DefaultKit {

	public GuerreiroKit() {
		super("Guerreiro", new ItemStack(Material.IRON_SWORD), "Lute como um guerreiro, e abata seus inimigos!", 17000,
				450);
	}

	@Override
	public void apply(Player player) {
		player.getInventory().addItem(new ItemStack(Material.IRON_CHESTPLATE));
		player.getInventory().addItem(new ItemStack(Material.IRON_LEGGINGS));
		player.getInventory().addItem(new ItemStack(Material.DIAMOND_SWORD));
	}

}
