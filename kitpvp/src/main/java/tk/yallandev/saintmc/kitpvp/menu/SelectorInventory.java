package tk.yallandev.saintmc.kitpvp.menu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import tk.yallandev.saintmc.kitpvp.GameMain;
import tk.yallandev.saintmc.kitpvp.gamer.Gamer;
import tk.yallandev.saintmc.kitpvp.kit.Kit;

public class SelectorInventory {

	private static int itemsPerPage = 21;

	public SelectorInventory(Player player, int page, OrderType orderType) {
		Gamer gamer = GameMain.getInstance().getGamerManager().getGamer(player.getUniqueId());
		MenuInventory menu = new MenuInventory("§7Kit Selector", 6, true);
		List<Kit> kits = new ArrayList<>(GameMain.getInstance().getKitManager().getKitList());

		Comparator<Kit> comparator = orderType.getComparator(gamer);

		Collections.sort(kits, comparator);

		List<MenuItem> items = new ArrayList<>();

		for (Kit kit : kits) {
			boolean hasKit = gamer.hasKitPermission(kit);

			if (hasKit) {
				items.add(new MenuItem(
						new ItemBuilder().lore("§7" + kit.getKitDescription() + "\n\n§eClique para selecionar!")
								.type(kit.getKitType()).name("§a" + NameUtils.formatString(kit.getName())).build(),
						new OpenKitMenu(kit)));
			} else {
				ItemStack item = new ItemBuilder().type(Material.STAINED_GLASS_PANE).durability(14)
						.name("§c" + NameUtils.formatString(kit.getName()))
						.lore("\n§cVocê não possui este kit!\n§cCompre em: §e" + CommonConst.STORE + "\n\n§7"
								+ kit.getKitDescription() + "\n\n§eClique para selecionar!")
						.build();
				items.add(new MenuItem(item, new StoreKitMenu(kit)));
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
							new SelectorInventory(arg0, page - 1, orderType);
						}

					}), 45);
		}

		if (Math.ceil(items.size() / itemsPerPage) + 1 > page) {
			menu.setItem(
					new MenuItem(new ItemBuilder().type(Material.ARROW).name("§aPágina " + (page + 1)).build(),
							(p, inventory, clickType, item, slot) -> new SelectorInventory(p, page + 1, orderType)),
					53);
		}

		menu.setItem(49,
				new ItemBuilder()
						.name("§fOrdenar por: §7" + (orderType == OrderType.MINE ? "Meus kits"
								: orderType == OrderType.ALPHABET ? "Alfabeto" : "Alfabeto ao contrário"))
						.type(Material.ITEM_FRAME).build(),
				new MenuClickHandler() {

					@Override
					public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
						new SelectorInventory(player, page,
								orderType.ordinal() == OrderType.values().length - 1 ? OrderType.values()[0]
										: OrderType.values()[orderType.ordinal() + 1]);
					}
				});

		menu.open(player);
	}

	@AllArgsConstructor
	public static class OpenKitMenu implements MenuClickHandler {

		private Kit kit;

		@Override
		public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
			GameMain.getInstance().getGamerManager().getGamer(p.getUniqueId()).setKit(kit);
			GameMain.getInstance().getKitManager().selectKit(p, kit);
			p.closeInventory();
		}

	}

	@AllArgsConstructor
	public static class StoreKitMenu implements MenuClickHandler {

		private Kit kit;

		@Override
		public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
			p.sendMessage("§cVocê não possui o kit " + kit.getName() + "!");
		}

	}

	public enum OrderType {

		MINE, ALPHABET, DE_ALPHABET;

		Comparator<Kit> getComparator(Gamer gamer) {
			switch (this) {
			case MINE: {
				return new Comparator<Kit>() {

					@Override
					public int compare(Kit o1, Kit o2) {
						int value1 = Boolean.valueOf(gamer.hasKitPermission(o2)).compareTo(gamer.hasKitPermission(o1));

						if (value1 == 0) {
							return o1.getName().compareTo(o2.getName());
						}

						return value1;
					}
				};
			}
			case DE_ALPHABET: {
				return (kit1, kit2) -> kit2.getName().compareTo(kit1.getName());
			}
			default: {
				return Comparator.comparing(kit -> kit.getName());
			}
			}
		}

	}

}
