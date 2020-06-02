package tk.yallandev.saintmc.game.games.hungergames.inventory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import lombok.AllArgsConstructor;
import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.bukkit.api.menu.MenuInventory;
import tk.yallandev.saintmc.bukkit.api.menu.MenuItem;
import tk.yallandev.saintmc.bukkit.api.menu.click.ClickType;
import tk.yallandev.saintmc.bukkit.api.menu.click.MenuClickHandler;
import tk.yallandev.saintmc.common.utils.string.NameUtils;
import tk.yallandev.saintmc.game.GameMain;
import tk.yallandev.saintmc.game.constructor.Gamer;
import tk.yallandev.saintmc.game.constructor.Kit;
import tk.yallandev.saintmc.game.manager.KitManager;

public class KitSelectorMenu {

	private static int itemsPerPage = 21;

	public KitSelectorMenu(Player player, int page, int kitOrigin, OrderType orderType) {
		Gamer gamer = Gamer.getGamer(player);
		List<Kit> kits = new ArrayList<>(KitManager.getKits().values());

		Collections.sort(kits, (o1, o2) -> o1.getName().compareTo(o2.getName()));

		MenuInventory menu = new MenuInventory(
				"§7Kit Selector 1", 6, true);

		List<MenuItem> items = new ArrayList<>();

		if (orderType == OrderType.ALPHABET) {
			for (int i = 0; i < kits.size(); i++) {
				Kit kit = kits.get(i);

				if (gamer.hasKit(kit.getName())) {
					items.add(new MenuItem(
							new ItemBuilder().lore("§7" + kit.getDescription() + "\n\n§eClique para selecionar!")
									.type(kit.getIcon().getType()).durability(kit.getIcon().getDurability())
									.name("§a" + NameUtils.formatString(kit.getName())).build(),
							new OpenKitMenu(kit, kitOrigin)));
				} else {
					ItemStack item = new ItemBuilder().type(Material.STAINED_GLASS_PANE).durability(14)
							.name("§c" + NameUtils.formatString(kit.getName())).lore("\n§cVocê não possui este kit!\n§cCompre em: §e"
									+ CommonConst.STORE + "\n\n§7" + kit.getDescription() + "\n\n§eClique para selecionar!")
							.build();
					items.add(new MenuItem(item, new StoreKitMenu(kit)));
				}
			}
		} else {
			for (int i = 0; i < kits.size(); i++) {
				Kit kit = kits.get(i);

				if (gamer.hasKit(kit.getName())) {
					items.add(new MenuItem(
							new ItemBuilder().lore("§7" + kit.getDescription() + "\n\n§eClique para selecionar!")
									.type(kit.getIcon().getType()).durability(kit.getIcon().getDurability())
									.name("§e§l" + NameUtils.formatString(kit.getName())).build(),
							new OpenKitMenu(kit, kitOrigin)));
				}
			}

			for (int i = 0; i < kits.size(); i++) {
				Kit kit = kits.get(i);

				if (!gamer.hasKit(kit.getName())) {
					ItemStack item = new ItemBuilder().type(Material.STAINED_GLASS_PANE).durability(14)
							.name("§c" + NameUtils.formatString(kit.getName())).lore("\n§cVocê não possui este kit!\n§cCompre em: §e"
									+ CommonConst.STORE + "\n\n§7" + kit.getDescription() + "\n\n§eClique para selecionar!").build();
					items.add(new MenuItem(item, new StoreKitMenu(kit)));
				}
			}
		}

		int pageStart = 0;
		int pageEnd = itemsPerPage;

		if (page > 1) {
			pageStart = ((page - 1) * itemsPerPage);
			pageEnd = (page * itemsPerPage);
		}

		if (pageEnd > items.size()) {
			pageEnd = items.size();
		}

		int w = 10;

		for (int i = pageStart; i < pageEnd; i++) {
			MenuItem item = items.get(i);
			menu.setItem(item, w);

			if (w % 9 == 7) {
				w += 3;
				continue;
			}

			w += 1;
		}

		if (page != 1) {
			menu.setItem(new MenuItem(new ItemBuilder().type(Material.ARROW).name("§aPágina " + (page - 1)).build(),
					new MenuClickHandler() {

						@Override
						public void onClick(Player arg0, Inventory arg1, ClickType arg2, ItemStack arg3, int arg4) {
							new KitSelectorMenu(arg0, page - 1, kitOrigin, orderType);
						}

					}), 45);
		}

		if (Math.ceil(items.size() / itemsPerPage) + 1 > page) {
			menu.setItem(new MenuItem(new ItemBuilder().type(Material.ARROW).name("§aPágina " + (page + 1)).build(),
					new MenuClickHandler() {
						@Override
						public void onClick(Player arg0, Inventory arg1, ClickType arg2, ItemStack arg3, int arg4) {
							new KitSelectorMenu(arg0, page + 1, kitOrigin, orderType);
						}
					}), 53);
		}

		menu.open(player);
	}

	@AllArgsConstructor
	public static class OpenKitMenu implements MenuClickHandler {

		private Kit kit;
		private int kitOrigin;
		
		@Override
		public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
			GameMain.getPlugin().getKitManager().selectKit(p, kit, kitOrigin);
			p.closeInventory();			
		}
		
	}

	@AllArgsConstructor
	public static class StoreKitMenu implements MenuClickHandler {

		private Kit kit;

		@Override
		public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
			p.sendMessage("§6§l> §fCompre o kit §a" + NameUtils.formatString(kit.getName()) + "§f em §a"
					+ CommonConst.STORE + "§f!");			
		}

	}

	public enum OrderType {

		MINE, ALPHABET;

	}
	
}
