package tk.yallandev.saintmc.game.games.hungergames.inventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import br.com.battlebits.commons.BattlebitsAPI;
import br.com.battlebits.commons.core.account.BattlePlayer;
import br.com.battlebits.commons.core.translate.T;
import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.bukkit.api.menu.ClickType;
import tk.yallandev.saintmc.bukkit.api.menu.MenuClickHandler;
import tk.yallandev.saintmc.bukkit.api.menu.MenuInventory;
import tk.yallandev.saintmc.bukkit.api.menu.MenuItem;
import tk.yallandev.saintmc.common.utils.string.NameUtils;
import tk.yallandev.saintmc.game.GameMain;
import tk.yallandev.saintmc.game.constructor.Gamer;
import tk.yallandev.saintmc.game.constructor.Kit;
import tk.yallandev.saintmc.game.manager.KitManager;

public class KitSelectorMenu {
	
	private static int itemsPerPage = 21;

	public static void open(Player player, int page) {
		Gamer gamer = Gamer.getGamer(player);
		List<Kit> kits = new ArrayList<>(KitManager.getKits().values());
		
		Collections.sort(kits, new Comparator<Kit>() {
			@Override
			public int compare(Kit o1, Kit o2) {
				return o1.getName().compareTo(o2.getName());
			}

		});
		
		MenuInventory menu = new MenuInventory("§%your-kits%§ [" + page + "/" + ((int) Math.ceil(kits.size() / itemsPerPage) + 1) + "]", 6, true);
		ItemStack nullItem = new ItemBuilder().type(Material.STAINED_GLASS_PANE).durability(15).name(" ").build();

		List<MenuItem> items = new ArrayList<>();
		
		for (int i = 0; i < kits.size(); i++) {
			Kit kit = kits.get(i);
			
			if (gamer.hasKit(kit.getName())) {
				items.add(new MenuItem(new ItemBuilder().lore(kit.getDescription()).type(kit.getIcon().getType()).durability(kit.getIcon().getDurability()).name("§e§l" + NameUtils.formatString(kit.getName())).build(), new OpenKitMenu(kit)));
			}
		}
		
		for (int i = 0; i < kits.size(); i++) {
			Kit kit = kits.get(i);
			
			if (!gamer.hasKit(kit.getName())) {
				ItemStack item = new ItemBuilder().type(Material.STAINED_GLASS_PANE).durability(14).name(T.t(BukkitMain.getInstance(),BattlePlayer.getLanguage(player.getUniqueId()), "kit-inventory-dont-have").replace("%kit%", NameUtils.formatString(kit.getName()))).lore(T.t(BukkitMain.getInstance(),BattlePlayer.getLanguage(player.getUniqueId()), "ability-inventory-click-buy").replace("%kit%", NameUtils.formatString(kit.getName())).replace("%store%", BattlebitsAPI.STORE)).build();
				items.add(new MenuItem(item, new StoreKitMenu(kit)));
			}
		}
		
		// PAGINA§§O
		int pageStart = 0;
		int pageEnd = itemsPerPage;
		if (page > 1) {
			pageStart = ((page - 1) * itemsPerPage);
			pageEnd = (page * itemsPerPage);
		}
		if (pageEnd > items.size()) {
			pageEnd = items.size();
		}
		if (page == 1) {
			menu.setItem(new ItemBuilder().type(Material.INK_SACK).durability(8).name("§%page-last-dont-have%§").build(), 27);
		} else {
			menu.setItem(new MenuItem(new ItemBuilder().type(Material.INK_SACK).durability(10).name("§%page-last-page%§").lore(Arrays.asList("§%page-last-click-here%§")).build(), new MenuClickHandler() {
				
				@Override
				public void onClick(Player arg0, Inventory arg1, ClickType arg2, ItemStack arg3, int arg4) {
					open(arg0, page - 1);
				}
				
			}), 27);
		}

		if (Math.ceil(items.size() / itemsPerPage) + 1 > page) {
			menu.setItem(new MenuItem(new ItemBuilder().type(Material.INK_SACK).durability(10).name("§%page-next-page%§").lore(Arrays.asList("§%page-next-click-here%§")).build(), new MenuClickHandler() {
				@Override
				public void onClick(Player arg0, Inventory arg1, ClickType arg2, ItemStack arg3, int arg4) {
					open(arg0, page + 1);
				}
			}), 35);
		} else {
			menu.setItem(new ItemBuilder().type(Material.INK_SACK).durability(8).name("§%page-next-dont-have%§").build(), 35);
		}

		// CUSTOM KITS

		int w = 19;

		for (int i = pageStart; i < pageEnd; i++) {
			MenuItem item = items.get(i);
			menu.setItem(item, w);
			
			if (w % 9 == 7) {
				w += 3;
				continue;
			}
			
			w += 1;
		}
		if (items.size() == 0) {
			menu.setItem(new ItemBuilder().type(Material.PAINTING).name("§c§lOps!").lore(Arrays.asList("§%error%§")).build(), 31);
		}

		for (int i = 0; i < 9; i++) {
			if (menu.getItem(i) == null)
				menu.setItem(nullItem, i);
		}
		menu.open(player);
	}

	public static class OpenKitMenu implements MenuClickHandler {

		private Kit kit;

		public OpenKitMenu(Kit kit) {
			this.kit = kit;
		}

		@Override
		public void onClick(Player arg0, Inventory arg1, ClickType arg2, ItemStack arg3, int arg4) {
			GameMain.getPlugin().getKitManager().selectKit(arg0, kit);
			arg0.closeInventory();
		}

	}
	
	public static class StoreKitMenu implements MenuClickHandler {

		private Kit kit;

		public StoreKitMenu(Kit kit) {
			this.kit = kit;
		}

		@Override
		public void onClick(Player arg0, Inventory arg1, ClickType arg2, ItemStack arg3, int arg4) {
			arg0.sendMessage("§6§l> §fCompre o kit §a" + NameUtils.formatString(kit.getName()) + "§f");
		}

	}
}
