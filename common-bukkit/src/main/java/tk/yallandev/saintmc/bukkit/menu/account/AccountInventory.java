package tk.yallandev.saintmc.bukkit.menu.account;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.bukkit.api.menu.MenuInventory;
import tk.yallandev.saintmc.bukkit.api.menu.MenuUpdateHandler;
import tk.yallandev.saintmc.bukkit.api.menu.click.ClickType;
import tk.yallandev.saintmc.bukkit.api.menu.click.MenuClickHandler;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.permission.RankType;
import tk.yallandev.saintmc.common.permission.Tag;
import tk.yallandev.saintmc.common.utils.DateUtils;

public class AccountInventory {

	public AccountInventory(Player sender, Member player) {
		MenuInventory menu = new MenuInventory("§7Conta de " + player.getPlayerName(), 5);
		boolean isStaff = Member.hasGroupPermission(sender.getUniqueId(), Group.DEV);

		String lore = isStaff ? "\n§7Fake: §f" + player.getFakeName() + "\n§7Discord: §f" + player.getDiscordName()
				: "\n§7";

		menu.setItem(13, new ItemBuilder().type(Material.SKULL_ITEM).durability(3)
				.name((player.getGroup() == Group.MEMBRO ? "§7" + player.getPlayerName()
						: Tag.getByName(player.getGroup().toString()).getPrefix() + " " + player.getPlayerName()) + " "
						+ "§7[" + player.getLeague().getColor() + player.getLeague().getSymbol() + "§7]")
				.lore(lore).build());

		DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		
		System.out.println(player.getOnlineTime());

		menu.setItem(29, new ItemBuilder().type(Material.PAPER).name("§aTempo")
				.lore("", "§7Primeiro login: §f" + df.format(new Date(player.getFirstLogin())),
						"§7Ultimo login: §f" + df.format(new Date(player.getLastLogin())),
						"§7Tempo online total: §f" + DateUtils.formatDifference(player.getOnlineTime() / 1000),
						"§7Tempo online atual: §f" + DateUtils.formatDifference(player.getSessionTime() / 1000),
						"",
						(player.isOnline() ? "§aO usuario está online no momento!" : ""))
				.build());

		menu.setItem(30,
				new ItemBuilder().type(Material.BOOK).name("§aHistórico de punições")
						.lore("", "§7Banimentos: §f" + player.getPunishmentHistory().getBanList().size(),
								"§7Mutes: §f" + player.getPunishmentHistory().getMuteList().size(),
								"§7Warns: §f" + player.getPunishmentHistory().getWarnList().size())
						.build(),
				new MenuClickHandler() {

					@Override
					public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
						new MenuInventory("§7Lista de punições", 4).open(p);
					}
				});

		List<String> temporaryRole = new ArrayList<>();

		temporaryRole.add("");

		for (Entry<RankType, Long> entry : player.getRanks().entrySet()) {
			temporaryRole.add(Tag.valueOf(entry.getKey().name()).getPrefix() + " §f- §7"
					+ DateUtils.formatDifference((entry.getValue() - System.currentTimeMillis()) / 1000));
		}

		menu.setItem(31,
				new ItemBuilder().type(Material.PAPER).name("§aGrupos Temporários").lore(temporaryRole).build());

		menu.setUpdateHandler(new MenuUpdateHandler() {

			@Override
			public void onUpdate(Player p, MenuInventory menu) {
				menu.setItem(29, new ItemBuilder().type(Material.PAPER).name("§aTempo")
						.lore("", "§7Primeiro login: §f" + df.format(new Date(player.getFirstLogin())),
								"§7Ultimo login: §f" + df.format(new Date(player.getLastLogin())),
								"§7Tempo online total: §f" + DateUtils.formatDifference(player.getOnlineTime() / 1000),
								"§7Tempo online atual: §f" + DateUtils.formatDifference(player.getSessionTime() / 1000),
								"",
								(player.isOnline() ? "§aO usuario está online no momento!" : ""))
						.build());

				List<String> temporaryRole = new ArrayList<>();

				temporaryRole.add("");

				for (Entry<RankType, Long> entry : player.getRanks().entrySet()) {

					if (entry.getValue() > System.currentTimeMillis()) {
						temporaryRole.add(Tag.valueOf(entry.getKey().name()).getPrefix() + " §f- §7"
								+ DateUtils.formatDifference((entry.getValue() - System.currentTimeMillis()) / 1000));
					} else {
						temporaryRole.add(Tag.valueOf(entry.getKey().name()).getPrefix() + " §f- §cacabou!");
					}
				}

				menu.setItem(31, new ItemBuilder().type(Material.PAPER).name("§aGrupos Temporários").lore(temporaryRole)
						.build());
			}
		});

		menu.open(sender);
	}

}
