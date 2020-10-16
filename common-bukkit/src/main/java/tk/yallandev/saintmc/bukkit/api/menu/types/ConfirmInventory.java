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
		MenuInventory menu = new MenuInventory(confirmTitle, 4);

		MenuClickHandler yesHandler = new MenuClickHandler() {

			@Override
			public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
				handler.onConfirm(true);
			}
		};

		MenuClickHandler noHandler = new MenuClickHandler() {

			@Override
			public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
				handler.onConfirm(false);

				if (topInventory != null)
					topInventory.open(p);
				else
					menu.close(p);
			}
		};

		menu.setItem(10, new ItemBuilder().type(Material.STAINED_GLASS_PANE).durability(14).name("§cRejeitar").build(),
				noHandler);
		menu.setItem(11, new ItemBuilder().type(Material.STAINED_GLASS_PANE).durability(14).name("§cRejeitar").build(),
				noHandler);
		menu.setItem(19, new ItemBuilder().type(Material.STAINED_GLASS_PANE).durability(14).name("§cRejeitar").build(),
				noHandler);
		menu.setItem(20, new ItemBuilder().type(Material.STAINED_GLASS_PANE).durability(14).name("§cRejeitar").build(),
				noHandler);

		menu.setItem(15, new ItemBuilder().type(Material.STAINED_GLASS_PANE).durability(5).name("§aAceitar").build(),
				yesHandler);
		menu.setItem(16, new ItemBuilder().type(Material.STAINED_GLASS_PANE).durability(5).name("§aAceitar").build(),
				yesHandler);
		menu.setItem(24, new ItemBuilder().type(Material.STAINED_GLASS_PANE).durability(5).name("§aAceitar").build(),
				yesHandler);
		menu.setItem(25, new ItemBuilder().type(Material.STAINED_GLASS_PANE).durability(5).name("§aAceitar").build(),
				yesHandler);

		menu.open(player);
	}

	public static interface ConfirmHandler {

		public void onConfirm(boolean confirmed);

	}
}
