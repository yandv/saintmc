package tk.yallandev.saintmc.skwyars.game.kit.register;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.skwyars.game.kit.DefaultKit;

public class LancadorKit extends DefaultKit {

	public LancadorKit() {
		super("Lançador", new ItemBuilder().type(Material.SNOW_BALL).build(),
				"Com sua incrivel pontaria, jogue projeteis em seus oponentes!", 7000, 450);
	}

	@Override
	public void apply(Player player) {
		player.getInventory().addItem(new ItemBuilder().type(Material.SNOW_BALL).amount(64).build());
		player.getInventory().addItem(new ItemBuilder().type(Material.SNOW_BALL).amount(64).build());
		player.getInventory().addItem(new ItemBuilder().type(Material.DIAMOND_HELMET).build());
	}

}
