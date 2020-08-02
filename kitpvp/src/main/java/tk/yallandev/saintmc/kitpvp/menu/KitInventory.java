package tk.yallandev.saintmc.kitpvp.menu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.bukkit.api.menu.MenuInventory;
import tk.yallandev.saintmc.bukkit.api.menu.MenuItem;
import tk.yallandev.saintmc.bukkit.api.menu.click.ClickType;
import tk.yallandev.saintmc.bukkit.api.menu.click.MenuClickHandler;
import tk.yallandev.saintmc.kitpvp.GameMain;
import tk.yallandev.saintmc.kitpvp.gamer.Gamer;
import tk.yallandev.saintmc.kitpvp.kit.Kit;

public class KitInventory {

	public KitInventory(Player player, InventoryType inventoryType) {
		Gamer gamer = GameMain.getInstance().getGamerManager().getGamer(player.getUniqueId());
		MenuInventory menuInventory = new MenuInventory("§eKit Selector", 6);

		menuInventory.setItem(3, new ItemBuilder().name("§aSeus kits")
				.durability(inventoryType == InventoryType.OWN ? 10 : 8).type(Material.INK_SACK).build(),
				new MenuClickHandler() {

					@Override
					public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
						if (inventoryType != InventoryType.OWN)
							new KitInventory(player, InventoryType.OWN);
					}
				});

		menuInventory.setItem(4, new ItemBuilder().name("§aTodos os kits")
				.durability(inventoryType == InventoryType.ALL ? 10 : 8).type(Material.INK_SACK).build(),
				new MenuClickHandler() {

					@Override
					public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
						if (inventoryType != InventoryType.ALL)
							new KitInventory(player, InventoryType.ALL);
					}
				});

		menuInventory.setItem(5, new ItemBuilder().name("§aLoja de kits")
				.durability(inventoryType == InventoryType.SHOP ? 10 : 8).type(Material.INK_SACK).build(),
				new MenuClickHandler() {

					@Override
					public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
						if (inventoryType != InventoryType.SHOP)
							new KitInventory(player, InventoryType.SHOP);
					}
				});

		int i = 19;

		for (Kit kit : inventoryType == InventoryType.ALL ? GameMain.getInstance().getKitManager().getKitList()
				: GameMain.getInstance().getKitManager().getKitList()) {
			if (inventoryType == InventoryType.ALL || inventoryType == InventoryType.SHOP) {
				menuInventory
						.setItem(i,
								new MenuItem(
										new ItemBuilder().type(kit.getKitType()).name("§a" + kit.getKitName())
												.lore("\n§7" + kit.getKitDescription() + "\n\n"
														+ (inventoryType == InventoryType.SHOP ? "§eClique para comprar"
																: "§cClique para selecionar"))
												.build(),
										new MenuClickHandler() {

											@Override
											public void onClick(Player p, Inventory inv, ClickType type,
													ItemStack stack, int slot) {
												p.performCommand("kit " + kit.getKitName());
												p.closeInventory();
											}
										}));
			} else if (gamer.hasKitPermission(kit)) {
				menuInventory
						.setItem(i,
								new MenuItem(
										new ItemBuilder().type(kit.getKitType()).name("§a" + kit.getKitName())
												.lore("\n§7" + kit.getKitDescription() + "\n\n"
														+ (inventoryType == InventoryType.OWN
																? "§aClique para selecionar"
																: "§cClique para selecionar"))
												.build(),
										new MenuClickHandler() {

											@Override
											public void onClick(Player p, Inventory inv, ClickType type,
													ItemStack stack, int slot) {
												p.performCommand("kit " + kit.getKitName());
												p.closeInventory();
											}
										}));
			}

			if (i % 9 == 7) {
				i += 3;
				continue;
			}

			i += 1;
		}

		menuInventory.open(player);
	}

	public enum InventoryType {

		ALL, OWN, SHOP;

	}

}
