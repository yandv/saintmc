package tk.yallandev.saintmc.skwyars.game.kit.register;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.skwyars.game.kit.DefaultKit;

public class MagoKit extends DefaultKit {

	public MagoKit() {
		super("Mago", new ItemStack(Material.BLAZE_POWDER),
				"Utilize de suas valiosas magias para atormentar seus oponentes e se bonificar!", 13000, 450);
	}

	@Override
	public void apply(Player player) {
		player.getInventory().addItem(new ItemStack(Material.IRON_CHESTPLATE));
		player.getInventory().addItem(new ItemBuilder().type(Material.POTION).durability(16428).amount(2).build());
		player.getInventory().addItem(new ItemBuilder().type(Material.POTION).durability(16417).amount(2).build());
		player.getInventory().addItem(new ItemBuilder().type(Material.POTION).durability(16418).amount(1).build());
		player.getInventory().addItem(new ItemBuilder().type(Material.POTION).durability(16424).amount(1).build());
		player.getInventory().addItem(new ItemBuilder().type(Material.POTION).durability(16418).amount(1).build());
	}

}
