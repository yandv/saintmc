package tk.yallandev.saintmc.bukkit.menu.account;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.google.common.base.Joiner;

import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.bukkit.api.menu.MenuInventory;
import tk.yallandev.saintmc.bukkit.api.menu.click.ClickType;
import tk.yallandev.saintmc.bukkit.api.menu.click.MenuClickHandler;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.tag.Tag;
import tk.yallandev.saintmc.common.utils.DateUtils;

public class AccountInventory {

	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm");

	public AccountInventory(Player sender, Member player, String playerName) {
		MenuInventory menu = new MenuInventory("§7Conta de " + player.getPlayerName(), 5);
		boolean isStaff = Member.hasGroupPermission(sender.getUniqueId(), Group.DEVELOPER);

		String lore = isStaff
				? "\n§7Fake: §f" + player.getFakeName() + "\n§7Discord: §f" + player.getDiscordName() + "\n§7Conta: §f"
						+ player.getLoginConfiguration().getAccountType().name()
				: "\n§7";

		menu.setItem(13, new ItemBuilder().type(Material.SKULL_ITEM).durability(3)
				.name(player.getGroup() == Group.MEMBRO ? "§7" + player.getPlayerName()
						: Tag.getByName(player.getServerGroup().name()).getPrefix() + " " + player.getPlayerName() + " "
								+ "§7[" + player.getLeague().getColor() + player.getLeague().getSymbol() + "§7]")
				.lore(lore).skin(player.getPlayerName()).build());

		create(menu, player);

		menu.setUpdateHandler((p, m) -> create(m, player));
		menu.open(sender);
	}

	public void create(MenuInventory menu, Member player) {
		if (player.isOnline())
			menu.setItem(29, new ItemBuilder().type(Material.PAPER).name("§aTempo")
					.lore("", "§7Primeiro login: §f" + DATE_FORMAT.format(new Date(player.getFirstLogin())),
							"§7Ultimo login: §f" + DATE_FORMAT.format(new Date(player.getLastLogin())),
							"§7Tempo online total: §f" + DateUtils.formatDifference((player.getOnlineTime() / 1000) + (player.getSessionTime() / 1000)),
							"§7Tempo online atual: §f" + DateUtils.formatDifference(player.getSessionTime() / 1000), "",
							"§aO usuario está online no momento!")
					.build());
		else
			menu.setItem(29, new ItemBuilder().type(Material.PAPER).name("§aTempo")
					.lore("", "§7Primeiro login: §f" + DATE_FORMAT.format(new Date(player.getFirstLogin())),
							"§7Ultimo login: §f" + DATE_FORMAT.format(new Date(player.getLastLogin())),
							"§7Tempo online total: §f" + DateUtils.formatDifference(player.getOnlineTime() / 1000))
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
//						new MenuInventory("§7Lista de punições", 4).open(p);
					}
				});

		menu.setItem(31, new ItemBuilder().type(Material.DIAMOND).name("§aGrupos Temporários").lore("\n" + Joiner
				.on("\n")
				.join(player.getRanks().entrySet().stream().map((entry) -> (DateUtils.isForever(entry.getValue())
						? Tag.valueOf(entry.getKey().name()).getPrefix() + " §f- §7Eterno"
						: Tag.valueOf(entry.getKey().name()).getPrefix() + " §f- §7"
								+ DateUtils.formatDifference((entry.getValue() - System.currentTimeMillis()) / 1000)))
						.collect(Collectors.toList())))
				.build());
	}

}
