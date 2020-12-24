package tk.yallandev.saintmc.bukkit.menu.account.punish;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.bukkit.api.menu.MenuItem;
import tk.yallandev.saintmc.bukkit.menu.ListInventory;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.ban.constructor.Ban;
import tk.yallandev.saintmc.common.utils.DateUtils;

public class BanListInventory extends ListInventory<Ban> {

	public BanListInventory(Player player, Member member, int page) {
		super("§7§nHistórico de banimentos", member.getPunishmentHistory().getBanList(), 6, 28, page, 10,
				new ItemHandler<Ban>() {

					@Override
					public MenuItem handleItem(Ban ban, int index) {
						return new MenuItem(new ItemBuilder()
								.name("§aBanimento #" + (index + 1)).type(Material.PAPER).lore(
										"", "§7Autor: §f" + ban
												.getBannedBy(),
										"§7Tempo: §f"
												+ (ban.isPermanent() ? "Permanente"
														: "alguns dias "
																+ (ban.hasExpired() ? "§8(expirado)"
																		: "§8(Tempo restante: "
																				+ DateUtils.getTime(ban.getBanExpire()))
																+ ")"),
										"§7Motivo: §f" + ban.getReason(), "§7Provas: §fNenhuma", "",
										"§8Banimento de ID " + ban.getId(),
										ban.isUnbanned() ? "§aBanimento revogado pelo " + ban.getUnbannedBy()
												: ban.hasExpired() ? "§eBanimento expirado!" : "§cBanimento ativado!")
								.build());

						/*
						 * 
						 * https://i.imgur.com/R4ooG58.png https://i.imgur.com/aV2QBWc.png
						 * https://i.imgur.com/zp96cAx.png https://i.imgur.com/He0irb5.png
						 * https://i.imgur.com/fl5bobl.png https://i.imgur.com/Fsix3Xx.png
						 * 
						 */
					}

				}, "§cNenhum banimento registrado!");

		if (page == 1)
			getMenu().setItem(45, new ItemBuilder().name("§aVoltar!").type(Material.ARROW).build(),
					(p, inv, type, stack, slot) -> {
						new PunishmentInventory(player, member);
					});

		openInventory(player);
	}

}
