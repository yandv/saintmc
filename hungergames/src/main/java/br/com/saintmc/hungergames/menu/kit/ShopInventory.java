package br.com.saintmc.hungergames.menu.kit;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.constructor.Gamer;
import br.com.saintmc.hungergames.kit.Kit;
import lombok.AllArgsConstructor;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.bukkit.api.menu.MenuInventory;
import tk.yallandev.saintmc.bukkit.api.menu.MenuItem;
import tk.yallandev.saintmc.bukkit.api.menu.click.ClickType;
import tk.yallandev.saintmc.bukkit.api.menu.click.MenuClickHandler;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.utils.string.NameUtils;

public class ShopInventory {

	private static int itemsPerPage = 21;

	public ShopInventory(Player player, int page) {
		Gamer gamer = GameGeneral.getInstance().getGamerController().getGamer(player);
		MenuInventory menu = new MenuInventory("§7Loja de Kits", 6, true);

		List<Kit> kits = new ArrayList<>(GameGeneral.getInstance().getKitController().getAllKits().stream()
				.filter(kit -> !gamer.hasKit(kit.getName())).collect(Collectors.toList()));

		List<MenuItem> items = new ArrayList<>();
		int money = Member.getMember(player.getUniqueId()).getMoney();

		for (Kit kit : kits) {
			items.add(
					new MenuItem(
							new ItemBuilder()
									.lore("§7" + kit.getDescription() + "\n\nPreço: §f" + kit.getPrice() + "\n\n"
											+ (money >= kit.getPrice() ? "§aClique para comprar este kit!"
													: "§cVocê não tem coins o suficiente para comprar este kit!"))
									.type(kit.getKitIcon().getType()).durability(kit.getKitIcon().getDurability())
									.name(money >= kit.getPrice() ? "§a" + NameUtils.formatString(kit.getName())
											: "§c" + NameUtils.formatString(kit.getName()))
									.build(),
							new BuyKit(kit, page)));
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

		if (items.isEmpty())
			menu.setItem(22, new ItemBuilder().type(Material.STAINED_GLASS_PANE).durability(6)
					.lore("§7Você possui todos os kits do HG!").name("§aNenhum kit para comprar!").build());
		else {
			for (int i = pageStart; i < pageEnd; i++) {
				MenuItem item = items.get(i);
				menu.setItem(item, w);

				if (w % 9 == 7) {
					w += 3;
					continue;
				}

				w += 1;
			}
		}

		if (page != 1) {
			menu.setItem(new MenuItem(new ItemBuilder().type(Material.ARROW).name("§aPágina " + (page - 1)).build(),
					(p, inventory, clickType, item, slot) -> {new ShopInventory(p, page - 1);return false;}), 45);
		}

		if (Math.ceil(items.size() / itemsPerPage) + 1 > page) {
			menu.setItem(new MenuItem(new ItemBuilder().type(Material.ARROW).name("§aPágina " + (page + 1)).build(),
					(p, inventory, clickType, item, slot) -> {new ShopInventory(p, page + 1);return false;}), 53);
		}

		menu.open(player);
	}

	@AllArgsConstructor
	public static class BuyKit implements MenuClickHandler {

		private Kit kit;
		private int page;

		@Override
		public boolean onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
			Member member = CommonGeneral.getInstance().getMemberManager().getMember(p.getUniqueId());

			if (member.getMoney() >= kit.getPrice()) {
				member.removeMoney(kit.getPrice());
				member.addPermission("kit." + kit.getName().toLowerCase());
				member.sendMessage("§aVocê comprou o kit " + NameUtils.formatString(kit.getName()) + "!");
				new ShopInventory(p, page);
			} else {
				member.sendMessage(
						"§cVocê não tem money o suficiente para comprar o kit " + NameUtils.formatString(kit.getName())
								+ ", você precisa de mais " + (kit.getPrice() - member.getMoney()) + "!");
				p.closeInventory();
			}

			return false;
		}
	}

}
