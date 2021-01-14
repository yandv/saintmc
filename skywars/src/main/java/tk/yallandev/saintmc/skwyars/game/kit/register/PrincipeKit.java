package tk.yallandev.saintmc.skwyars.game.kit.register;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.skwyars.game.kit.DefaultKit;

public class PrincipeKit extends DefaultKit {

	public PrincipeKit() {
		super("Principe", new ItemBuilder().type(Material.DIAMOND_CHESTPLATE).build(),
				"Lute como um verdadeiro príncipe, o herdeiro do trono!", 23000, 450);
	}

	@Override
	public void apply(Player player) {
		player.getInventory().addItem(new ItemBuilder().type(Material.DIAMOND_CHESTPLATE).amount(1).build());
		player.getInventory().addItem(new ItemBuilder().type(Material.IRON_LEGGINGS).amount(1).build());
		player.getInventory().addItem(new ItemBuilder().type(Material.GOLDEN_APPLE).amount(3).build());
	}

}
