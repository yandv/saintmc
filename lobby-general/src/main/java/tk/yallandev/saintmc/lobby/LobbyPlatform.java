package tk.yallandev.saintmc.lobby;

import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.command.BukkitCommandFramework;
import tk.yallandev.saintmc.common.server.ServerType;
import tk.yallandev.saintmc.common.server.loadbalancer.server.MinigameServer;
import tk.yallandev.saintmc.common.server.loadbalancer.server.ProxiedServer;
import tk.yallandev.saintmc.lobby.command.FlyCommand;
import tk.yallandev.saintmc.lobby.listener.GamerListener;
import tk.yallandev.saintmc.lobby.listener.HologramListener;
import tk.yallandev.saintmc.lobby.listener.LoginListener;
import tk.yallandev.saintmc.lobby.listener.TabListener;
import tk.yallandev.saintmc.lobby.listener.WorldListener;
import tk.yallandev.saintmc.lobby.manager.PlayerManager;

@Getter
@RequiredArgsConstructor
public class LobbyPlatform {

	@Getter
	@Setter
	private static LobbyPlatform instance;

	private final Plugin plugin;

	private PlayerManager playerManager;

	private String lobbyAddress = CommonGeneral.getInstance().getServerId().contains(".")
			? CommonGeneral.getInstance().getServerId().split("\\.")[0].toUpperCase()
			: CommonGeneral.getInstance().getServerId().toUpperCase();

	public void onEnable() {
		instance = this;
		playerManager = new PlayerManager();

		if (lobbyAddress.equalsIgnoreCase("lobby") || lobbyAddress.length() >= 4)
			lobbyAddress = "§kA?";

		BukkitMain.getInstance().setServerLog(true);
		BukkitMain.getInstance().setRemovePlayerDat(true);

		loadServers();

		plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
		BukkitCommandFramework.INSTANCE.registerCommands(new FlyCommand());

		plugin.getServer().getPluginManager().registerEvents(new TabListener(), plugin);
		plugin.getServer().getPluginManager().registerEvents(new WorldListener(), plugin);
		plugin.getServer().getPluginManager().registerEvents(new WorldListener(), plugin);
		plugin.getServer().getPluginManager().registerEvents(new HologramListener(), plugin);
		plugin.getServer().getPluginManager().registerEvents(new GamerListener(), plugin);

		if (!CommonGeneral.getInstance().isLoginServer())
			Bukkit.getPluginManager().registerEvents(new LoginListener(), plugin);
	}

	public void onDisable() {

	}

	void loadServers() {
		for (Entry<String, Map<String, String>> entry : CommonGeneral.getInstance().getServerData().loadServers()
				.entrySet()) {
			try {
				if (!entry.getValue().containsKey("type"))
					continue;

				if (!entry.getValue().containsKey("address"))
					continue;

				if (!entry.getValue().containsKey("maxplayers"))
					continue;

				if (!entry.getValue().containsKey("onlineplayers"))
					continue;

				if (ServerType.valueOf(entry.getValue().get("type").toUpperCase()) == ServerType.NETWORK)
					continue;

				ProxiedServer server = BukkitMain.getInstance().getServerManager().addActiveServer(
						entry.getValue().get("address"), entry.getKey(),
						ServerType.valueOf(entry.getValue().get("type").toUpperCase()),
						Integer.valueOf(entry.getValue().get("maxplayers")));

				BukkitMain.getInstance().getServerManager().getServer(entry.getKey())
						.setOnlinePlayers(CommonGeneral.getInstance().getServerData().getPlayers(entry.getKey()));
				BukkitMain.getInstance().getServerManager().getServer(entry.getKey())
						.setJoinEnabled(Boolean.valueOf(entry.getValue().get("joinenabled")));

				if (server instanceof MinigameServer) {
					MinigameServer minigameServer = (MinigameServer) server;

					minigameServer.setTime(CommonGeneral.getInstance().getServerData().getTime(entry.getKey()));
					minigameServer.setMap(CommonGeneral.getInstance().getServerData().getMap(entry.getKey()));
					minigameServer.setState(CommonGeneral.getInstance().getServerData().getState(entry.getKey()));
				}
			} catch (Exception e) {
			}
		}
	}

}
