package tk.yallandev.saintmc.skwyars.menu.kit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.bukkit.api.menu.MenuInventory;
import tk.yallandev.saintmc.bukkit.api.menu.MenuItem;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.utils.string.NameUtils;
import tk.yallandev.saintmc.skwyars.GameGeneral;
import tk.yallandev.saintmc.skwyars.game.kit.Kit;
import tk.yallandev.saintmc.skwyars.gamer.Gamer;

@AllArgsConstructor
public class SelectorInventory {

	private static int itemsPerPage = 20;

	public SelectorInventory(Player player, int page, InventoryType inventoryType, OrderType orderType) {
		Member member = CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId());
		Gamer gamer = GameGeneral.getInstance().getGamerController().getGamer(player);
		MenuInventory menu = new MenuInventory(inventoryType.getDisplayName(), 6, true);
		List<Kit> kits = new ArrayList<>(GameGeneral.getInstance().getAbilityController().getKits());

		Comparator<Kit> comparator = orderType.getComparator(gamer);

		Collections.sort(kits, comparator);

		List<MenuItem> items = new ArrayList<>();

		for (Kit kit : kits) {
			if (inventoryType == InventoryType.STORE) {
				if (!gamer.hasKit(kit.getName())) {
					ItemBuilder item = new ItemBuilder()
							.lore("§f\n§7" + kit.getDescription() + "\n\n§7Preço em Moedas: §a" + kit.getPrice()
									+ "\n§7Preço em Cash: §6" + kit.getCashPrice() + "\n\n"
									+ (member.getMoney() > kit.getPrice() || member.getCash() > kit.getCashPrice()
											? "§aClique para comprar!"
											: "§cVocê não possui money para comprar esse kit!"))
							.type(kit.getKitIcon().getType()).durability(kit.getKitIcon().getDurability())
							.hideAttributes()
							.name((member.getMoney() > kit.getPrice() || member.getCash() > kit.getCashPrice() ? "§a"
									: "§c") + NameUtils.formatString(kit.getName()));

					items.add(new MenuItem(item.build(), (p, inv, type, stack, slot) -> {
						new BuyInventory(player, kit, page, orderType);
					}));
				}
			} else {
				if (gamer.hasKit(kit.getName())) {
					ItemBuilder item = new ItemBuilder()
							.lore("§f\n§7" + kit.getDescription() + "\n\n§eClique para selecionar!")
							.type(kit.getKitIcon().getType()).durability(kit.getKitIcon().getDurability())
							.name("§a" + NameUtils.formatString(kit.getName()));

					if (gamer.hasKit() && gamer.getKit() == kit)
						item.glow();

					items.add(new MenuItem(item.build(), (p, inv, type, stack, slot) -> {
						GameGeneral.getInstance().getGamerController().getGamer(p).selectKit(kit);
						p.closeInventory();
					}));
				} else
					items.add(new MenuItem(
							new ItemBuilder().type(Material.STAINED_GLASS_PANE).durability(14)
									.name("§c" + NameUtils.formatString(kit.getName()))
									.lore("§f\n§7" + kit.getDescription() + "\n\n§cVocê não possui esse kit!").build(),
							(p, inv, type, stack, slot) -> {
								p.sendMessage("§cVocê não possui esse kit!");
								p.closeInventory();
							}));
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

		if (items.isEmpty())
			menu.setItem(22, new ItemBuilder().type(Material.BARRIER).name("§c" + (inventoryType.getNoItem())).build());
		else {
			int w = 11;

			for (int i = pageStart; i < pageEnd; i++) {
				MenuItem item = items.get(i);
				menu.setItem(item, w);

				if (w % 9 == 6) {
					w += 5;
					continue;
				}

				w += 1;
			}
		}

		menu.setItem(9,
				new ItemBuilder().name("§aSelecionar kit").type(Material.INK_SACK)
						.durability(inventoryType == InventoryType.SELECTOR ? 10 : 8).build(),
				(p, inventory, clickType, item, slot) -> {
					if (inventoryType != InventoryType.SELECTOR)
						new SelectorInventory(p, page, InventoryType.SELECTOR, orderType);
				});
		menu.setItem(18,
				new ItemBuilder().name("§aComprar kit").type(Material.INK_SACK)
						.durability(inventoryType == InventoryType.STORE ? 10 : 8).build(),
				(p, inventory, clickType, item, slot) -> {
					if (inventoryType != InventoryType.STORE)
						new SelectorInventory(p, page, InventoryType.STORE, orderType);
				});

		if (inventoryType == InventoryType.STORE)
			menu.setItem(49, new ItemBuilder().type(Material.EMERALD).name("§aInformações")
					.lore("", "§7Suas moedas: §f" + member.getMoney(), "§7Seu cash: §6" + member.getCash()).build());

		if (page != 1) {
			menu.setItem(new MenuItem(new ItemBuilder().type(Material.ARROW).name("§aPágina " + (page - 1)).build(),
					(p, inventory, clickType, item, slot) -> {
						new SelectorInventory(p, page - 1, inventoryType, orderType);
					}), 45);
		}

		if (Math.ceil(items.size() / itemsPerPage) + 1 > page) {
			menu.setItem(new MenuItem(new ItemBuilder().type(Material.ARROW).name("§aPágina " + (page + 1)).build(), (p,
					inventory, clickType, item, slot) -> new SelectorInventory(p, page + 1, inventoryType, orderType)),
					53);
		}

		menu.open(player);
	}

	@AllArgsConstructor
	@Getter
	public enum InventoryType {

		STORE("§7§nComprar kit", "Nenhum kit para comprar!"),
		SELECTOR("§7§nSelecionar kit", "Nenhum kit para selecionar!");

		String displayName;
		String noItem;

	}

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
