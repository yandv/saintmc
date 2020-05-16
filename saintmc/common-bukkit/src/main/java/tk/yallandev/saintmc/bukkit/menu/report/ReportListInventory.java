package tk.yallandev.saintmc.bukkit.menu.report;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.bukkit.api.menu.ClickType;
import tk.yallandev.saintmc.bukkit.api.menu.MenuClickHandler;
import tk.yallandev.saintmc.bukkit.api.menu.MenuInventory;
import tk.yallandev.saintmc.common.report.Report;

public class ReportListInventory {

	private static int itemsPerPage = 36;

	public ReportListInventory(Player player, int page) {
		List<Report> reports = new ArrayList<>(CommonGeneral.getInstance().getReportManager().getReports());
		Iterator<Report> iterator = reports.iterator();

		while (iterator.hasNext()) {
			Report report = iterator.next();

			if (report.isExpired()) {
				report.expire();
				iterator.remove();
				continue;
			}
		}

		Collections.sort(reports, new Comparator<Report>() {
			@Override
			public int compare(Report o1, Report o2) {
				if (o1.getLastReportTime() > o2.getLastReportTime())
					return 1;
				else if (o1.getLastReportTime() == o2.getLastReportTime())
					return 0;
				return -1;
			}
		});

		MenuInventory menu = new MenuInventory("§7Lista de reports", 6);

		int pageStart = 0;
		int pageEnd = itemsPerPage;

		if (page > 1) {
			pageStart = ((page - 1) * itemsPerPage);
			pageEnd = (page * itemsPerPage);
		}

		if (pageEnd > reports.size()) {
			pageEnd = reports.size();
		}

		if (page == 1) {
			menu.setItem(0, new ItemBuilder().type(Material.INK_SACK).durability(8).name("§cPágina anterior").build());
		} else {
			menu.setItem(0, new ItemBuilder().type(Material.INK_SACK).durability(10).name("§aPágina anterior")
					.lore("\n§7Clique para voltar de página").build(), new MenuClickHandler() {
						@Override
						public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
							new ReportListInventory(player, page - 1);
						}
					});
		}

		if (Math.ceil(reports.size() / itemsPerPage) + 1 > page) {
			menu.setItem(8, new ItemBuilder().type(Material.INK_SACK).durability(10).name("§aPágina posterior")
					.lore("\n§7Clique para avançar de página").build(), new MenuClickHandler() {
						@Override
						public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
							new ReportListInventory(player, page + 1);
						}
					});
		} else {
			menu.setItem(8, new ItemBuilder().type(Material.INK_SACK).durability(8).name("§cPágina posterior").build());
		}

		int w = 9;

		for (int i = pageStart; i < pageEnd; i++) {
			Report report = reports.get(i);

			menu.setItem(w,
					new ItemBuilder().type(Material.SKULL_ITEM).durability(3)
							.name((report.isOnline() ? "§a" : "§c") + report.getPlayerName())
							.lore("\n§7Status: " + (report.isOnline() ? "§aOnline no momento" : "§cOffline no momento")
									+ "\n\n§aClique para teletransportar")
							.skin(report.getPlayerName()).build(),
					new ReportClickHandler(report, menu));
			w += 1;
		}

		ItemStack nullItem = new ItemBuilder().type(Material.STAINED_GLASS_PANE).durability(15).name(" ").build();

		for (int i = 0; i < 9; i++) {
			if (menu.getItem(i) == null)
				menu.setItem(i, nullItem);
		}

		menu.open(player);
	}

	private static class ReportClickHandler implements MenuClickHandler {

		private Report report;

		public ReportClickHandler(Report report, MenuInventory topInventory) {
			this.report = report;
		}

		@Override
		public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
			if (type == ClickType.RIGHT) {
				if (!report.isOnline()) {
					p.sendMessage(" §c* §fO jogador §a" + report.getPlayerName() + "§f não está online!");
					return;
				}

				p.chat("/tp " + report.getPlayerName());
				return;
			}

			new ReportInventory(p, report);
		}

	}
}
