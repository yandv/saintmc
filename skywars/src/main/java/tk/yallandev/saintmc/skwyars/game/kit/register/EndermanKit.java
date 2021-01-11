package tk.yallandev.saintmc.skwyars.game.kit.register;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.skwyars.game.kit.DefaultKit;

public class EndermanKit extends DefaultKit {

	public EndermanKit() {
		super("Enderman", new ItemBuilder().type(Material.ENDER_PEARL).build(),
				"Teleporte-se como um Enderman, e avance em seus oponentes!", 23500);
	}

	@Override
	public void apply(Player player) {
		player.getInventory().addItem(new ItemBuilder().type(Material.ENDER_PEARL).amount(2).build());
		player.getInventory().addItem(new ItemBuilder().type(Material.GOLDEN_APPLE).amount(3).build());
	}

}
