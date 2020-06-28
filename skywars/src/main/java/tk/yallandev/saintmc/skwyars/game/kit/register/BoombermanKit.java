package tk.yallandev.saintmc.skwyars.game.kit.register;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.skwyars.game.kit.DefaultKit;

public class BoombermanKit extends DefaultKit {

	public BoombermanKit() {
		super("Boomberman", new ItemStack(Material.TNT), "Cause fortes explosões e assim derrote seus inimigos!",
				11000, 450);
	}

	@Override
	public void apply(Player player) {
		player.getInventory().addItem(new ItemBuilder().type(Material.TNT).amount(32).build());
		player.getInventory().addItem(new ItemBuilder().type(Material.REDSTONE_BLOCK).amount(5).build());
		player.getInventory().addItem(new ItemBuilder().type(Material.LOG).amount(16).build());
	}

}
