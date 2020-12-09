package tk.yallandev.saintmc.lobby.menu.server;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.bukkit.api.menu.MenuInventory;
import tk.yallandev.saintmc.bukkit.api.menu.MenuUpdateHandler;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.server.ServerType;
import tk.yallandev.saintmc.common.server.loadbalancer.server.ProxiedServer;
import tk.yallandev.saintmc.lobby.menu.server.ServerInventory.SendClick;

public class LobbyInventory {

	public LobbyInventory(Player player) {
		Member member = CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId());
		List<ProxiedServer> serverList = new ArrayList<>(
				BukkitMain.getInstance().getServerManager().getBalancer(ServerType.LOBBY).getList());

		MenuInventory menu = new MenuInventory("§7Servidores de Lobby",
				2 + (serverList.size() == 0 ? 1 : (serverList.size() / 7) + 1));

		serverList.sort((o1, o2) -> Integer.valueOf(o1.getOnlinePlayers()).compareTo(o2.getOnlinePlayers()));

		create(serverList, member, menu);

		menu.setUpdateHandler(new MenuUpdateHandler() {

			@Override
			public void onUpdate(Player player, MenuInventory menu) {
				create(serverList, member, menu);
			}
		});

		menu.open(player);
	}

	private void create(List<ProxiedServer> serverList, Member member, MenuInventory menu) {
		int w = 10;

		for (ProxiedServer server : serverList) {
			if (!server.isJoinEnabled() && !member.hasGroupPermission(Group.TRIAL)
					&& !server.isInWhitelist(member.getPlayerName()))
				continue;

			ItemBuilder builder = new ItemBuilder();
			builder.type(Material.INK_SACK);

			if (CommonGeneral.getInstance().getServerId().equalsIgnoreCase(server.getServerId()))
				builder.glow();

			if (server.isFull()) {
				builder.name("§c§l" + server.getServerId().substring(0, 2).toUpperCase());
				builder.durability(8);
				builder.lore(
						"\n§3§l" + server.getOnlinePlayers() + " §7jogadores conectados\n§cEsse servidor está lotado!");
			} else {
				builder.name("§a§l" + server.getServerId().substring(0, 2).toUpperCase());
				builder.durability(10);
				builder.lore("\n§3§l" + server.getOnlinePlayers() + " §7jogadores conectados");
			}

			builder.amount(server.getOnlinePlayers());

			if (w % 9 == 8) {
				w += 2;
			}
			menu.setItem(w, builder.build(), new SendClick(server.getServerId()));
			w++;
		}
	}

}
