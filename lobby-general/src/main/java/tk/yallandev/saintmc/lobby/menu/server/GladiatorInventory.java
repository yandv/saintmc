package tk.yallandev.saintmc.lobby.menu.server;

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
import tk.yallandev.saintmc.common.server.ServerType;
import tk.yallandev.saintmc.common.server.loadbalancer.server.ProxiedServer;
import tk.yallandev.saintmc.lobby.menu.server.ServerInventory.SendClick;

public class GladiatorInventory {

	public GladiatorInventory(Player player) {
		Member member = CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId());

		List<ProxiedServer> serverList = BukkitMain.getInstance().getServerManager().getBalancer(ServerType.GLADIATOR)
				.getList();

		MenuInventory menu = new MenuInventory("§7§nSelecionar Gladiator",
				2 + (serverList.size() == 0 ? 1 : (serverList.size() / 7) + 1));

		serverList.sort(new Comparator<ProxiedServer>() {

			@Override
			public int compare(ProxiedServer o1, ProxiedServer o2) {
				int object = Boolean.valueOf(o1.isFull()).compareTo(o2.isFull());

				if (object != 0)
					return object;

				if (o1.getOnlinePlayers() < o2.getOnlinePlayers())
					return 1;
				else if (o1.getOnlinePlayers() == o2.getOnlinePlayers())
					return 0;

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

		for (ProxiedServer server : serverList) {
			ItemBuilder builder = new ItemBuilder();
			builder.type(Material.STAINED_GLASS_PANE);

			if (server.isFull()) {
				builder.name("§eGladiator #" + server.getServerId().substring(1, 2).toUpperCase());
				builder.durability(7);
			} else {
				builder.name("§aGladiator #" + server.getServerId().substring(1, 2).toUpperCase());
				builder.durability(5);
			}

			builder.lore("", "§8" + server.getOnlinePlayers() + " jogadores conectados.",
					(server.isFull() ? "§c" : "§a") + "Clique para entrar.");

			if (w % 9 == 8) {
				w += 2;
			}

			menu.setItem(w, builder.build(), new SendClick(server.getServerId()));
			w++;
		}
	}

}
