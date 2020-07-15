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
import tk.yallandev.saintmc.common.server.loadbalancer.server.SkywarsServer;
import tk.yallandev.saintmc.common.utils.string.StringUtils;
import tk.yallandev.saintmc.lobby.menu.server.ServerInventory.SendClick;

public class SkywarsInventory {

	public SkywarsInventory(Player player) {
		Member member = CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId());
		List<ProxiedServer> serverList = new ArrayList<>(
				BukkitMain.getInstance().getServerManager().getBalancer(ServerType.SW_SOLO).getList());

		MenuInventory menu = new MenuInventory("§7Servidores de Skywars",
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

		for (ProxiedServer proxiedServer : serverList) {
			if (!proxiedServer.isJoinEnabled() && !member.hasGroupPermission(Group.DEV)
					&& !proxiedServer.isInWhitelist(member.getPlayerName()))
				continue;

			SkywarsServer server = (SkywarsServer) proxiedServer;

			if (server.getTime() <= 0)
				continue;

			ItemBuilder builder = new ItemBuilder();
			builder.type(Material.INK_SACK);

			String name = server.getServerId().substring(0, 2).toUpperCase();
			String nameId = "§a§l" + name;
			String loreId = null;

			switch (server.getState()) {
			case WAITING:
			case PREGAME: {
				loreId = "\n§7A partir §a§linicia§7 em §a§l%time%§7!\n§7Mapa: §a" + server.getMap()
						+ "\n\n§3§l%online% §7jogadores conectados";
				builder.durability(10);
				break;
			}
			case STARTING: {
				nameId = "§e§l" + name;
				loreId = "\n§7A partir §e§linicia§7 em §e§l%time%§7!\n§7Mapa: §a" + server.getMap()
						+ "\n\n§3§l%online% §7jogadores conectados";
				builder.durability(10);
				break;
			}
			case GAMETIME: {
				nameId = "§c§l" + name;
				loreId = "\n§7A partida está §c§landamento§7!\n§7Mapa: §a" + server.getMap()
						+ "\n§7O tempo de partida é §c§l%time%§7\n\n§3§l%online% §7jogadores conectados";
				builder.durability(1);
				break;
			}
			case NONE: {
				nameId = "§4§l" + name;
				loreId = "\n§7O servidor está sendo iniciado!";
				builder.durability(1);
				break;
			}
			default: {
				nameId = "§c§l" + name;
				loreId = "\n§7A partida está §c§landamento§7!\n§7O tempo de partida é §c§l%time%§7\n\n§3§l%online% §7jogadores conectados";
				builder.durability(1);
				break;
			}
			}

			loreId = loreId.replace("%time%", StringUtils.format(server.getTime())).replace("%online%",
					"" + server.getOnlinePlayers());

			builder.amount(server.getOnlinePlayers());
			builder.name(nameId);
			builder.lore(loreId);

			if (w % 9 == 8) {
				w += 2;
			}

			menu.setItem(w, builder.build(), new SendClick(server.getServerId()));
			w++;
		}
	}
}
