package br.com.saintmc.hungergames.menu.kit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.GameMain;
import br.com.saintmc.hungergames.constructor.Gamer;
import br.com.saintmc.hungergames.kit.Kit;
import br.com.saintmc.hungergames.kit.KitType;
import lombok.AllArgsConstructor;
import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.bukkit.api.menu.ClickType;
import tk.yallandev.saintmc.bukkit.api.menu.MenuClickHandler;
import tk.yallandev.saintmc.bukkit.api.menu.MenuInventory;
import tk.yallandev.saintmc.bukkit.api.menu.MenuItem;
import tk.yallandev.saintmc.common.utils.string.NameUtils;

@AllArgsConstructor
public class KitSelector {

	private static int itemsPerPage = 21;

	public KitSelector(Player player, int page, KitType kitType, OrderType orderType) {
		Gamer gamer = GameGeneral.getInstance().getGamerController().getGamer(player);
		MenuInventory menu = new MenuInventory("§7Kit Selector", 6, true);
		List<Kit> kits = new ArrayList<>(GameGeneral.getInstance().getKitController().getAllKits());

		Comparator<Kit> comparator = Comparator.comparing(kit -> kit.getName());

		if (orderType == OrderType.MINE) {
			comparator.thenComparing(kit -> (GameMain.DOUBLEKIT ? kitType == KitType.PRIMARY ? true : gamer.hasKit(kit.getName()) : gamer.hasKit(kit.getName())));
		}
		Collections.sort(kits, comparator);
//		Collections.sort(kits, orderType == OrderType.ALPHABET ? (o1,o2) -> o1.getName().compareTo(o2.getName()) : (o1,o2) -> Boolean.compare(gamer.hasKit(o1.getName()), gamer.hasKit(o2.getName())) | o1.getName().compareTo(o2.getName()));

		List<MenuItem> items = new ArrayList<>();

		for (int i = 0; i < kits.size(); i++) {
			Kit kit = kits.get(i);
			System.out.println(GameMain.DOUBLEKIT ? kitType == KitType.PRIMARY ? true : gamer.hasKit(kit.getName()) : gamer.hasKit(kit.getName()));
			
			boolean hasKit = GameMain.DOUBLEKIT ? kitType == KitType.PRIMARY ? true : gamer.hasKit(kit.getName()) : gamer.hasKit(kit.getName());

			if (hasKit) {
				items.add(new MenuItem(
						new ItemBuilder().lore("§7" + kit.getDescription() + "\n\n§eClique para selecionar!")
								.type(kit.getKitIcon().getType()).durability(kit.getKitIcon().getDurability())
								.name("§a" + NameUtils.formatString(kit.getName())).build(),
						new OpenKitMenu(kit, kitType)));
			} else {
				ItemStack item = new ItemBuilder().type(Material.STAINED_GLASS_PANE).durability(14)
						.name("§c" + NameUtils.formatString(kit.getName()))
						.lore("\n§cVocê não possui este kit!\n§cCompre em: §e" + CommonConst.STORE + "\n\n§7"
								+ kit.getDescription() + "\n\n§eClique para selecionar!")
						.build();
				items.add(new MenuItem(item, new StoreKitMenu(kit)));
			}
		}

		if (orderType == OrderType.ALPHABET) {
//			for (int i = 0; i < kits.size(); i++) {
//				Kit kit = kits.get(i);
//
//				if (gamer.hasKit(kit.getName())) {
//					items.add(new MenuItem(
//							new ItemBuilder().lore("§7" + kit.getDescription() + "\n\n§eClique para selecionar!")
//									.type(kit.getKitIcon().getType()).durability(kit.getKitIcon().getDurability())
//									.name("§a" + NameUtils.formatString(kit.getName())).build(),
//							new OpenKitMenu(kit, kitType)));
//				} else {
//					ItemStack item = new ItemBuilder().type(Material.STAINED_GLASS_PANE).durability(14)
//							.name("§c" + NameUtils.formatString(kit.getName())).lore("\n§cVocê não possui este kit!\n§cCompre em: §e"
//									+ CommonConst.STORE + "\n\n§7" + kit.getDescription() + "\n\n§eClique para selecionar!")
//							.build();
//					items.add(new MenuItem(item, new StoreKitMenu(kit)));
//				}
//			}
		} else {
//			for (int i = 0; i < kits.size(); i++) {
//				Kit kit = kits.get(i);
//
//				if (gamer.hasKit(kit.getName())) {
//					items.add(new MenuItem(
//							new ItemBuilder().lore("§7" + kit.getDescription() + "\n\n§eClique para selecionar!")
//									.type(kit.getKitIcon().getType()).durability(kit.getKitIcon().getDurability())
//									.name("§e§l" + NameUtils.formatString(kit.getName())).build(),
//							new OpenKitMenu(kit, kitType)));
//				}
//			}
//
//			for (int i = 0; i < kits.size(); i++) {
//				Kit kit = kits.get(i);
//
//				if (!gamer.hasKit(kit.getName())) {
//					ItemStack item = new ItemBuilder().type(Material.STAINED_GLASS_PANE).durability(14)
//							.name("§c" + NameUtils.formatString(kit.getName())).lore("\n§cVocê não possui este kit!\n§cCompre em: §e"
//									+ CommonConst.STORE + "\n\n§7" + kit.getDescription() + "\n\n§eClique para selecionar!").build();
//					items.add(new MenuItem(item, new StoreKitMenu(kit)));
//				}
//			}
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
							new KitSelector(arg0, page - 1, kitType, orderType);
						}

					}), 45);
		}

		if (Math.ceil(items.size() / itemsPerPage) + 1 > page) {
			menu.setItem(
					new MenuItem(new ItemBuilder().type(Material.ARROW).name("§aPágina " + (page + 1)).build(),
							(p, inventory, clickType, item, slot) -> new KitSelector(p, page + 1, kitType, orderType)),
					53);
		}

		menu.open(player);
	}

	@AllArgsConstructor
	public static class OpenKitMenu implements MenuClickHandler {

		private Kit kit;
		private KitType kitType;

		@Override
		public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
			if (type == ClickType.RIGHT) {
				new KitInfo(p, kit, kitType);
				return;
			}

			GameGeneral.getInstance().getKitController().selectKit(p, kit, kitType);
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
