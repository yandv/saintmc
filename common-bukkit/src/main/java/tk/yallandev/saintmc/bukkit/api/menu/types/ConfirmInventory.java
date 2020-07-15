package tk.yallandev.saintmc.bukkit.api.menu.types;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.bukkit.api.menu.MenuInventory;
import tk.yallandev.saintmc.bukkit.api.menu.click.ClickType;
import tk.yallandev.saintmc.bukkit.api.menu.click.MenuClickHandler;

public class ConfirmInventory {

	public ConfirmInventory(Player player, String confirmTitle, ConfirmHandler handler, MenuInventory topInventory) {
		MenuInventory menu = new MenuInventory(confirmTitle, 5);

		MenuClickHandler yesHandler = new MenuClickHandler() {

			@Override
			public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
				handler.onCofirm(true);
			}
		};

		MenuClickHandler noHandler = new MenuClickHandler() {

			@Override
			public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
				handler.onCofirm(false);
			}
		};

		menu.setItem(11, new ItemBuilder().type(Material.REDSTONE_BLOCK).name("§cNegar").build(), noHandler);
		menu.setItem(12, new ItemBuilder().type(Material.REDSTONE_BLOCK).name("§cNegar").build(), noHandler);
		menu.setItem(20, new ItemBuilder().type(Material.REDSTONE_BLOCK).name("§cNegar").build(), noHandler);
		menu.setItem(21, new ItemBuilder().type(Material.REDSTONE_BLOCK).name("§cNegar").build(), noHandler);

		menu.setItem(14, new ItemBuilder().type(Material.EMERALD_BLOCK).name("§aAceitar").build(), yesHandler);
		menu.setItem(15, new ItemBuilder().type(Material.EMERALD_BLOCK).name("§aAceitar").build(), yesHandler);
		menu.setItem(23, new ItemBuilder().type(Material.EMERALD_BLOCK).name("§aAceitar").build(), yesHandler);
		menu.setItem(24, new ItemBuilder().type(Material.EMERALD_BLOCK).name("§aAceitar").build(), yesHandler);

		menu.setItem(39, new ItemBuilder().type(Material.ARROW).name("§aVoltar").build(), new MenuClickHandler() {

			@Override
			public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
				if (topInventory != null)
					topInventory.open(p);
				else
					menu.close(p);
			}
		});

		menu.open(player);
	}

	public static interface ConfirmHandler {

		public void onCofirm(boolean confirmed);

	}
}
