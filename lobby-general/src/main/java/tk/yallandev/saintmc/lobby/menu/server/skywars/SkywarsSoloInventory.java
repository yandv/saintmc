package tk.yallandev.saintmc.lobby.menu.server.skywars;

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
import tk.yallandev.saintmc.common.server.ServerType;
import tk.yallandev.saintmc.common.server.loadbalancer.server.ProxiedServer;
import tk.yallandev.saintmc.common.server.loadbalancer.server.SkywarsServer;
import tk.yallandev.saintmc.common.utils.string.StringUtils;
import tk.yallandev.saintmc.lobby.menu.server.ServerInventory.SendClick;

public class SkywarsSoloInventory {

	public SkywarsSoloInventory(Player player) {
		Member member = CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId());
		List<ProxiedServer> serverList = new ArrayList<>(
				BukkitMain.getInstance().getServerManager().getBalancer(ServerType.SW_SOLO).getList());

		MenuInventory menu = new MenuInventory("§7§nSelecionar o Mapa",
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
			SkywarsServer server = (SkywarsServer) proxiedServer;

			if (server.getTime() <= 0)
				continue;

			ItemBuilder builder = new ItemBuilder();
			builder.type(Material.STAINED_GLASS_PANE);

			String name = server.getServerId().substring(0, 2).toUpperCase();
			String nameId = "§a" + name;
			String loreId = null;

			switch (server.getState()) {
			case WAITING:
			case PREGAME: {
				loreId = "\n§aA partida iniciará em " + StringUtils.format(server.getTime()) + "\n§7Mapa: §f"
						+ server.getMap();
				builder.durability(5);
				break;
			}
			case STARTING: {
				nameId = "§e" + name;
				loreId = "\n§eA partida iniciará em " + StringUtils.format(server.getTime()) + "\n§7Mapa: §f"
						+ server.getMap();
				builder.durability(15);
				break;
			}
			case NONE: {
				continue;
			}
			default: {
				nameId = "§c" + name;
				loreId = "\n§aA partida iniciou há " + StringUtils.format(server.getTime()) + "\n§7Mapa: §f"
						+ server.getMap();
				builder.durability(15);
				break;
			}
			}

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
