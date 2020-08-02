package tk.yallandev.saintmc.lobby.menu.server;

import java.util.ArrayList;
import java.util.Comparator;
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
import tk.yallandev.saintmc.common.server.loadbalancer.server.HungerGamesServer;
import tk.yallandev.saintmc.common.server.loadbalancer.server.ProxiedServer;
import tk.yallandev.saintmc.common.utils.string.StringUtils;
import tk.yallandev.saintmc.lobby.menu.server.ServerInventory.SendClick;

public class HungergamesInventory {

	public HungergamesInventory(Player player) {
		Member member = CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId());

		List<ProxiedServer> serverList = new ArrayList<>(
				BukkitMain.getInstance().getServerManager().getBalancer(ServerType.HUNGERGAMES).getList());

		MenuInventory menu = new MenuInventory("§8Servidores de HungerGames",
				2 + (serverList.size() == 0 ? 1 : (serverList.size() / 7) + 1));
		
		serverList.sort(new Comparator<ProxiedServer>() {

			@Override
			public int compare(ProxiedServer o1, ProxiedServer o2) {
				int object = ((HungerGamesServer) o1).getState().compareTo(((HungerGamesServer) o2).getState());

				if (object != 0)
					return object;

				object = Integer.valueOf(o1.getOnlinePlayers()).compareTo(o2.getOnlinePlayers());

				if (object != 0)
					return object;

				object = Integer.valueOf(((HungerGamesServer) o1).getTime())
						.compareTo(((HungerGamesServer) o2).getTime());

				if (object != 0)
					return object;

				return o1.getServerId().compareTo(o2.getServerId());
			}

		});

		create(serverList, member, menu);

		menu.setUpdateHandler(new MenuUpdateHandler() {

			@Override
			public void onUpdate(Player player, MenuInventory menu) {
				create(serverList, member, menu);
			}

		});

		menu.open(player);

	}

	public void create(List<ProxiedServer> serverList, Member member, MenuInventory menu) {
		int w = 10;

		for (ProxiedServer proxiedServer : serverList) {
			if (!proxiedServer.isJoinEnabled() && !member.hasGroupPermission(Group.TRIAL)
					&& !proxiedServer.isInWhitelist(member.getPlayerName()))
				continue;

			HungerGamesServer server = (HungerGamesServer) proxiedServer;

			if (server.getTime() <= 0)
				continue;

			ItemBuilder builder = new ItemBuilder();
			builder.type(Material.INK_SACK);
			
			String name = proxiedServer.getServerId().substring(0, 2).toUpperCase();
			String nameId = "§a§l" + name;
			String loreId = null;

			switch (server.getState()) {
			case WAITING:
			case PREGAME: {
				loreId = "\n§7A partir §a§linicia§7 em §a§l%time%§7!\n\n§3§l%online% §7jogadores conectados";
				builder.durability(10);
				break;
			}
			case STARTING: {
				nameId = "§e§l" + name;
				loreId = "\n§7A partir §e§linicia§7 em §e§l%time%§7!\n\n§3§l%online% §7jogadores conectados";
				builder.durability(10);
				break;
			}
			case INVINCIBILITY: {
				nameId = "§e§l" + name;
				loreId = "\n§7A partida está na §e§linvencibilidade§7!\n§7O tempo de partida é §e§l%time%\n\n§3§l%online% §7jogadores conectados";
				builder.durability(14);
				break;
			}
			case GAMETIME: {
				nameId = "§c§l" + name;
				loreId = "\n§7A partida está §c§landamento§7!\n§7O tempo de partida é §c§l%time%§7\n\n§3§l%online% §7jogadores conectados";
				builder.durability(1);
				break;
			}
			case NONE: {
				if (!member.hasGroupPermission(Group.TRIAL))
					continue;

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
