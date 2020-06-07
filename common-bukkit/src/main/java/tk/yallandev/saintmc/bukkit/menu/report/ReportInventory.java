package tk.yallandev.saintmc.bukkit.menu.report;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.bukkit.api.menu.MenuInventory;
import tk.yallandev.saintmc.bukkit.api.menu.click.ClickType;
import tk.yallandev.saintmc.bukkit.api.menu.click.MenuClickHandler;
import tk.yallandev.saintmc.bukkit.api.menu.types.ConfirmInventory;
import tk.yallandev.saintmc.bukkit.api.menu.types.ConfirmInventory.ConfirmHandler;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.account.MemberVoid;
import tk.yallandev.saintmc.common.report.Report;
import tk.yallandev.saintmc.common.tag.Tag;
import tk.yallandev.saintmc.common.utils.DateUtils;

public class ReportInventory {

	public ReportInventory(Player player, Report report) {
		MenuInventory menu = new MenuInventory("§7Report do " + report.getPlayerName(), 6);

		menu.setItem(0, new ItemBuilder().type(Material.ARROW).name("§aVoltar").build(), new MenuClickHandler() {

			@Override
			public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
				new ReportListInventory(p, 1);
			}
		});

		Member reportPlayer = CommonGeneral.getInstance().getMemberManager().getMember(report.getPlayerUniqueId());

		if (reportPlayer == null)
			reportPlayer = new MemberVoid(
					CommonGeneral.getInstance().getPlayerData().loadMember(report.getPlayerUniqueId()));

		System.out.println((report.getReportExpire() - System.currentTimeMillis()) / 1000);

		menu.setItem(22, new ItemBuilder().type(Material.SKULL_ITEM).durability(3)
				.lore("", "§7Status: " + (reportPlayer.isOnline() ? "§aOnline no momento" : "§cOffline no momento"),
						"§7Servidor: §f" + reportPlayer.getServerId(),
						"§7Expira em: §f" + DateUtils.getTime(report.getReportExpire()))
				.name(Tag.valueOf(reportPlayer.getGroup().name()).getPrefix() + " " + report.getPlayerName())
				.skin(report.getPlayerName()).build());

		menu.setItem(38, new ItemBuilder().type(Material.COMPASS).name("§aClique para teletransportar!").build(),
				new MenuClickHandler() {

					@Override
					public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
						player.chat("/tp " + report.getPlayerName());
					}
				});

		menu.setItem(40,
				new ItemBuilder().type(Material.BOOK_AND_QUILL).name("§aLista de reports")
						.lore("\n§7Ultimo motivo: §f" + report.getLastReport().getReason() + "\n§7Ultimo player: §f"
								+ report.getLastReport().getPlayerName() + "\n\n§7Numero de reports: §f"
								+ report.getPlayersReason().size())
						.build(),
				new MenuClickHandler() {

					@Override
					public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
						new ReportInformationListInventory(player, report, menu, 1);
					}
				});

		menu.setItem(42, new ItemBuilder().type(Material.REDSTONE_BLOCK).name("§aDeletar report").build(),
				new MenuClickHandler() {

					@Override
					public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
						new ConfirmInventory(player, "§7Remover report", new ConfirmHandler() {

							@Override
							public void onCofirm(boolean confirmed) {
								if (confirmed) {
									report.expire();
									new ReportListInventory(p, 1);
								} else {
									menu.open(p);
								}
							}
						}, menu);
					}
				});

		ItemStack nullItem = new ItemBuilder().type(Material.STAINED_GLASS_PANE).durability(15).name(" ").build();

		for (int i = 0; i < 9; i++) {
			if (menu.getItem(i) == null)
				menu.setItem(i, nullItem);
		}

		menu.open(player);

		reportPlayer = null;
		nullItem = null;
	}

}
