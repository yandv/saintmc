package tk.yallandev.saintmc.skwyars.menu.kit;

import java.util.Comparator;

import org.bukkit.entity.Player;

import lombok.AllArgsConstructor;
import tk.yallandev.saintmc.skwyars.game.kit.Kit;
import tk.yallandev.saintmc.skwyars.gamer.Gamer;

@AllArgsConstructor
public class SelectorInventory {

	private static int itemsPerPage = 21;

	public SelectorInventory(Player player, int page, OrderType orderType) {
		
		/* fazer o menu no bukkit normal */
			
//		Gamer gamer = GameGeneral.getInstance().getGamerController().getGamer(player);
//		MenuInventory menu = new MenuInventory("§7Kit Selector", 6, true);
//		List<Kit> kits = new ArrayList<>(GameGeneral.getInstance().getAbilityController().getKits());
//
//		Comparator<Kit> comparator = orderType.getComparator(gamer);
//
//		Collections.sort(kits, comparator);
//
//		List<MenuItem> items = new ArrayList<>();
//
//		for (Kit kit : kits) {
//			boolean hasKit = gamer.hasKit(kit.getName());
//
//			if (hasKit)
//				if (gamer.hasKit() && gamer.getKit() == kit)
//					items.add(new MenuItem(
//							new ItemBuilder().lore("§7" + kit.getDescription() + "\n\n§eClique para selecionar!")
//									.type(kit.getKitIcon().getType()).durability(kit.getKitIcon().getDurability())
//									.name("§a" + NameUtils.formatString(kit.getName())).glow().build(),
//							new OpenKitMenu(kit)));
//				else
//					items.add(new MenuItem(
//							new ItemBuilder().lore("§7" + kit.getDescription() + "\n\n§eClique para selecionar!")
//									.type(kit.getKitIcon().getType()).durability(kit.getKitIcon().getDurability())
//									.name("§a" + NameUtils.formatString(kit.getName())).build(),
//							new OpenKitMenu(kit)));
//			else
//				items.add(new MenuItem(
//						new ItemBuilder().type(Material.STAINED_GLASS_PANE).durability(14)
//								.name("§c" + NameUtils.formatString(kit.getName()))
//								.lore("\n§cVocê não possui este kit!\n§cCompre em: §e" + CommonConst.STORE + "\n\n§7"
//										+ kit.getDescription() + "\n\n§eClique para selecionar!")
//								.build(),
//						new StoreKitMenu(kit)));
//		}
//
//		int pageStart = 0;
//		int pageEnd = itemsPerPage;
//
//		if (page > 1) {
//			pageStart = ((page - 1) * itemsPerPage);
//			pageEnd = (page * itemsPerPage);
//		}
//
//		if (pageEnd > items.size()) {
//			pageEnd = items.size();
//		}
//
//		int w = 10;
//
//		for (int i = pageStart; i < pageEnd; i++) {
//			MenuItem item = items.get(i);
//			menu.setItem(item, w);
//
//			if (w % 9 == 7) {
//				w += 3;
//				continue;
//			}
//
//			w += 1;
//		}
//
//		if (page != 1) {
//			menu.setItem(new MenuItem(new ItemBuilder().type(Material.ARROW).name("§aPágina " + (page - 1)).build(),
//					new MenuClickHandler() {
//
//						@Override
//						public void onClick(Player arg0, Inventory arg1, ClickType arg2, ItemStack arg3, int arg4) {
//							new SelectorInventory(arg0, page - 1, orderType);
//						}
//
//					}), 45);
//		}
//
//		if (Math.ceil(items.size() / itemsPerPage) + 1 > page) {
//			menu.setItem(
//					new MenuItem(new ItemBuilder().type(Material.ARROW).name("§aPágina " + (page + 1)).build(),
//							(p, inventory, clickType, item, slot) -> new SelectorInventory(p, page + 1, orderType)),
//					53);
//		}
//
//		menu.open(player);
	}

//	@AllArgsConstructor
//	public static class OpenKitMenu implements MenuClickHandler {
//
//		private Kit kit;
//
//		@Override
//		public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
//			GameGeneral.getInstance().getGamerController().getGamer(p).selectKit(kit);
//			p.closeInventory();
//		}
//
//	}
//
//	@AllArgsConstructor
//	public static class StoreKitMenu implements MenuClickHandler {
//
//		private Kit kit;
//
//		@Override
//		public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
//			p.sendMessage("§6§l> §fCompre o kit §a" + NameUtils.formatString(kit.getName()) + "§f em §a"
//					+ CommonConst.STORE + "§f!");
//			p.closeInventory();
//		}
//
//	}

	public enum OrderType {

		MINE, ALPHABET, DE_ALPHABET;

		Comparator<Kit> getComparator(Gamer gamer) {
			switch (this) {
			case MINE: {
				return new Comparator<Kit>() {

					@Override
					public int compare(Kit o1, Kit o2) {
						boolean hasKitO1 = gamer.hasKit(o1.getName());
						boolean hasKitO2 = gamer.hasKit(o2.getName());

						int value1 = Boolean.valueOf(hasKitO2).compareTo(hasKitO1);

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
