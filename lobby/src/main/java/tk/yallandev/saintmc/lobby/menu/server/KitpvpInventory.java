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
import tk.yallandev.saintmc.common.server.loadbalancer.server.ProxiedServer;
import tk.yallandev.saintmc.lobby.menu.server.ServerInventory.SendClick;

public class KitpvpInventory {
	
	public KitpvpInventory(Player player) {
		Member member = CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId());
		List<ProxiedServer> battleServer = new ArrayList<>();
		int w = 10;
		
		battleServer.addAll(BukkitMain.getInstance().getServerManager().getBalancer(ServerType.FULLIRON).getList());
		battleServer.addAll(BukkitMain.getInstance().getServerManager().getBalancer(ServerType.SIMULATOR).getList());
		
		MenuInventory menu = new MenuInventory("§8Servidores de KitPvP", 2 + (battleServer.size() == 0 ? 1 : (battleServer.size() / 7) + 1));
		
		battleServer.sort(new Comparator<ProxiedServer>() {

			@Override
			public int compare(ProxiedServer o1, ProxiedServer o2) {
				int object = Boolean.valueOf(o1.isFull()).compareTo(o2.isFull());
				
				if (object != 0) 
					return object;
				
				if (o1.getOnlinePlayers() < o2.getOnlinePlayers())
					return 1;
				else if (o1.getOnlinePlayers() == o2.getOnlinePlayers())
					return 0;
				
				return -1;
			}

		});
		
		for (ProxiedServer server : battleServer) {
			if (!server.isJoinEnabled() && !member.hasGroupPermission(Group.TRIAL))
				continue;
			
			ItemBuilder builder = new ItemBuilder();
			
			builder.type(Material.INK_SACK);
			builder.lore("\n§7Modo: §e" + (server.getServerType() == ServerType.FULLIRON ? "FullIron" : "Simulator") + "\n");
			
			if (server.isFull()) {
				builder.name("§9§l> §c§lPVP-" + server.getServerId().substring(0, 2).toUpperCase() + " §9§l<");
				builder.lore("\n§a" + server.getOnlinePlayers() + " jogadores online\n§cEsse servidor está lotado!");
				builder.durability(1);
			} else {
				builder.name("§9§l> §a§lPVP-" + server.getServerId().substring(0, 2).toUpperCase() + " §9§l<");
				builder.lore("\n§a" + server.getOnlinePlayers() + " jogadores online");
				builder.durability(8);
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
				
				for (ProxiedServer server : battleServer) {
					if (!server.isJoinEnabled() && !member.hasGroupPermission(Group.DEV))
						continue;
					
					ItemBuilder builder = new ItemBuilder();
					
					builder.type(Material.INK_SACK);
					builder.lore("\n§7Modo: §e" + (server.getServerType() == ServerType.FULLIRON ? "FullIron" : "Simulator") + "\n");
					
					if (server.isFull()) {
						builder.name("§9§l> §c§lPVP-" + server.getServerId().substring(0, 2).toUpperCase() + " §9§l<");
						builder.lore("\n§a" + server.getOnlinePlayers() + " jogadores online\n§cEsse servidor está lotado!");
						builder.durability(1);
					} else {
						builder.name("§9§l> §a§lPVP-" + server.getServerId().substring(0, 2).toUpperCase() + " §9§l<");
						builder.lore("\n§a" + server.getOnlinePlayers() + " jogadores online");
						builder.durability(8);
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
