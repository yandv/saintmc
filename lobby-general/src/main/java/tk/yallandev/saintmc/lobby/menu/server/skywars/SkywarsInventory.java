package tk.yallandev.saintmc.lobby.menu.server.skywars;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.bukkit.api.menu.MenuInventory;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.server.ServerType;

public class SkywarsInventory {

	public SkywarsInventory(Player player) {
		MenuInventory menuInventory = new MenuInventory("§7§nSelecionar Skywars", 3);

		menuInventory.setItem(11,
				new ItemBuilder().name("§aSky Wars (Solo)").type(Material.EYE_OF_ENDER)
						.lore("§8" + BukkitMain.getInstance().getServerManager().getBalancer(ServerType.SW_SOLO)
								.getTotalNumber() + " jogadores", "", "§aClique para conectar.")
						.build(),
				(p, inv, type, stack, slot) -> {
					p.closeInventory();
					BukkitMain.getInstance().sendServer(p, "SWSolo");
				});

		menuInventory.setItem(12,
				new ItemBuilder().name("§aSelecionar mapa (Solo)").type(Material.PAPER)
						.lore("§7Escolha o mapa que você deseja jogar\n\n§cDisponível apenas para VIPs!").build(),
				(p, inv, type, stack, slot) -> {
					if (Member.hasGroupPermission(player.getUniqueId(), Group.PRO))
						new SkywarsSoloInventory(player);
					else {
						player.sendMessage("§cSomente VIPs podem escolher o mapa do SkyWars!");
						player.closeInventory();
					}
				});

		menuInventory.setItem(14,
				new ItemBuilder().name("§cSky Wars (Dupla)").type(Material.EYE_OF_ENDER)
						.lore("§8" + BukkitMain.getInstance().getServerManager().getBalancer(ServerType.SW_TEAM)
								.getTotalNumber() + " jogadores", "", "§aClique para conectar.")
						.build(),
				(p, inv, type, stack, slot) -> {
					p.closeInventory();
					BukkitMain.getInstance().sendServer(p, "SWTeam");
				});

		menuInventory.setItem(15,
				new ItemBuilder().name("§cSelecionar mapa (Dupla)").type(Material.PAPER)
						.lore("§7Escolha o mapa que você deseja jogar\n\n§cDisponível apenas para VIPs!").build(),
				(p, inv, type, stack, slot) -> {
					if (!Member.hasGroupPermission(player.getUniqueId(), Group.PRO)) {
						player.sendMessage("§cSomente VIPs podem escolher o mapa do SkyWars!");
						player.closeInventory();
					}
				});

		menuInventory.open(player);
	}

}
