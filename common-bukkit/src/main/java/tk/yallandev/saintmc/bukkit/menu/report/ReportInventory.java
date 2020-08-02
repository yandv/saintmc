package tk.yallandev.saintmc.bukkit.menu.report;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.ClickEvent;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.bukkit.api.menu.MenuInventory;
import tk.yallandev.saintmc.bukkit.api.menu.types.ConfirmInventory;
import tk.yallandev.saintmc.bukkit.api.menu.types.ConfirmInventory.ConfirmHandler;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.account.MemberVoid;
import tk.yallandev.saintmc.common.report.Report;
import tk.yallandev.saintmc.common.tag.Tag;
import tk.yallandev.saintmc.common.utils.DateUtils;
import tk.yallandev.saintmc.common.utils.string.MessageBuilder;

public class ReportInventory {

	public ReportInventory(Player player, Report report) {
		MenuInventory menu = new MenuInventory("§7Report do " + report.getPlayerName(), 5);

		Member rPlayer = CommonGeneral.getInstance().getMemberManager().getMember(report.getPlayerUniqueId());

		if (rPlayer == null)
			rPlayer = new MemberVoid(
					CommonGeneral.getInstance().getPlayerData().loadMember(report.getPlayerUniqueId()));

		final Member reportPlayer = rPlayer;

		create(player, reportPlayer, report, menu);
		menu.setUpdateHandler((p, m) -> create(player, reportPlayer, report, menu));

		menu.open(player);
	}

	private void create(Player player, Member reportPlayer, Report report, MenuInventory menu) {
		String tag = Tag.valueOf(reportPlayer.getGroup().name()).getPrefix()
				+ (ChatColor.stripColor(Tag.valueOf(reportPlayer.getGroup().name()).getPrefix()).trim().length() > 0
						? " "
						: "");

		if (report.isOnline())
			menu.setItem(13,
					new ItemBuilder().type(Material.SKULL_ITEM).durability(3)
							.lore("", "§7Servidor: §f" + reportPlayer.getServerId(),
									"§7Expira em: §f" + DateUtils.getTime(report.getReportExpire()), "",
									"§aO jogador está online no momento!")
							.name(tag + " " + report.getPlayerName()).skin(report.getPlayerName()).build());
		else
			menu.setItem(13,
					new ItemBuilder().type(Material.SKULL_ITEM).durability(3)
							.lore("", "§7Servidor: §f" + reportPlayer.getServerId(),
									"§7Expira em: §f" + DateUtils.getTime(report.getReportExpire()))
							.name(tag + " " + report.getPlayerName()).skin(report.getPlayerName()).build());

		menu.setItem(29, new ItemBuilder().type(Material.COMPASS).name("§aClique para teletransportar!").build(),
				(p, inv, type, stack, slot) -> {
					if (!report.isOnline()) {
						p.sendMessage(" §c* §fO jogador §a" + report.getPlayerName() + "§f não está online!");
						return;
					}

					p.spigot()
							.sendMessage(new MessageBuilder("§aClique aqui para teletransportar!").setClickEvent(
									new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tp " + report.getPlayerName()))
									.create());
				});

		menu.setItem(30,
				new ItemBuilder().type(Material.BOOK_AND_QUILL).name("§aLista de reports")
						.lore("\n§7Ultimo motivo: §f" + report.getLastReport().getReason() + "\n§7Ultimo player: §f"
								+ report.getLastReport().getPlayerName() + "\n\n§7Numero de reports: §f"
								+ report.getPlayersReason().size())
						.build(),
				(p, inv, type, stack, slot) -> new ReportInformationListInventory(player, report, menu, 1));

		menu.setItem(31, new ItemBuilder().type(Material.REDSTONE_BLOCK).name("§aDeletar report").build(),
				(p, inv, type, stack, slot) -> {
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
				});

		menu.setItem(33, new ItemBuilder().type(Material.ARROW).name("§aVoltar").build(),
				(p, inv, type, stack, slot) -> new ReportListInventory(p, 1));
	}

}
