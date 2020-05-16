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
import tk.yallandev.saintmc.common.server.loadbalancer.server.BattleServer;
import tk.yallandev.saintmc.lobby.menu.server.ServerInventory.SendClick;

public class LobbyInventory {
	
	public LobbyInventory(Player player) {
		Member member = CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId());
		int w = 10;
		
		List<BattleServer> battleServer = new ArrayList<>(BukkitMain.getInstance().getServerManager().getBalancer(ServerType.LOBBY).getList());
		
		MenuInventory menu = new MenuInventory("§7Servidores de Lobby", 2 + (battleServer.size() == 0 ? 1 : (battleServer.size() / 7) + 1));
		
		battleServer.sort((o1, o2) -> Integer.valueOf(o1.getOnlinePlayers()).compareTo(o2.getOnlinePlayers()));
		
		for (BattleServer server : battleServer) {
			if (!server.isJoinEnabled() && !member.hasGroupPermission(Group.DEV))
				continue;
			
			ItemBuilder builder = new ItemBuilder();
			builder.type(Material.INK_SACK);
			
			String lobbyName = server.getServerId().substring(1, 2);
			
			if (CommonGeneral.getInstance().getServerId().equalsIgnoreCase(server.getServerId()))
				builder.glow();
			
			if (server.isFull()) {
				builder.name("§9§l> §cLobby " + lobbyName + " §9§l<");
				builder.durability(8);
				builder.lore("\n§a" + server.getOnlinePlayers() + " jogadores online\n§cEsse servidor está lotado!");
			} else {
				builder.name("§9§l> §aLobby " + lobbyName + " §9§l<");		
				builder.durability(10);
				builder.lore("\n§a" + server.getOnlinePlayers() + " jogadores online");
			}
			
			builder.amount(server.getOnlinePlayers());
			
			if (w % 9 == 8) {
				w+=2;
			}
			
			menu.setItem(w, builder.build(), new SendClick(server.getServerId()));
			w++;
		}
		
		menu.setUpdateHandler(new MenuUpdateHandler() {
			
			@Override
			public void onUpdate(Player player, MenuInventory menu) {
				int w = 10;
				
				for (BattleServer server : battleServer) {
					if (!server.isJoinEnabled() && !member.hasGroupPermission(Group.DEV))
						continue;
					
					ItemBuilder builder = new ItemBuilder();
					builder.type(Material.INK_SACK);
					
					String lobbyName = server.getServerId().substring(1, 2);
					
					if (CommonGeneral.getInstance().getServerId().equalsIgnoreCase(server.getServerId()))
						builder.glow();
					
					if (server.isFull()) {
						builder.name("§9§l> §cLobby " + lobbyName + " §9§l<");
						builder.durability(8);
						builder.lore("\n§a" + server.getOnlinePlayers() + " jogadores online\n§cEsse servidor está lotado!");
					} else {
						builder.name("§9§l> §aLobby " + lobbyName + " §9§l<");		
						builder.durability(10);
						builder.lore("\n§a" + server.getOnlinePlayers() + " jogadores online");
					}
					
					builder.amount(server.getOnlinePlayers());
					
					if (w % 9 == 8) {
						w+=2;
					}
					menu.setItem(w, builder.build(), new SendClick(server.getServerId()));
					w++;
				}
			}
		});
		
		menu.open(player);
	}

}
