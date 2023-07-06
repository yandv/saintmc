package tk.yallandev.saintmc.lobby;

import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

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
import tk.yallandev.saintmc.lobby.listener.PlayerListener;
import tk.yallandev.saintmc.lobby.listener.TabListener;
import tk.yallandev.saintmc.lobby.listener.WorldListener;
import tk.yallandev.saintmc.lobby.manager.PlayerManager;

@Getter
@RequiredArgsConstructor
public abstract class LobbyPlatform extends JavaPlugin {

	@Getter
	@Setter
	private static LobbyPlatform instance;

	private PlayerManager playerManager;

	private String lobbyAddress;

	@Override
	public void onEnable() {
		instance = this;
		playerManager = new PlayerManager();

		lobbyAddress = CommonGeneral.getInstance().getServerId().contains(".")
				? CommonGeneral.getInstance().getServerId().split("\\.")[0].toUpperCase()
				: CommonGeneral.getInstance().getServerId().toUpperCase();

		if (lobbyAddress.equalsIgnoreCase("lobby") || lobbyAddress.length() >= 4)
			lobbyAddress = "§kA?";

		BukkitMain.getInstance().setServerLog(true);
		BukkitMain.getInstance().setRemovePlayerDat(true);

		loadServers();

		getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		BukkitCommandFramework.INSTANCE.registerCommands(new FlyCommand());

		getServer().getPluginManager().registerEvents(new TabListener(), this);
		getServer().getPluginManager().registerEvents(new PlayerListener(), this);
		getServer().getPluginManager().registerEvents(new WorldListener(), this);
		getServer().getPluginManager().registerEvents(new HologramListener(), this);
		getServer().getPluginManager().registerEvents(new GamerListener(), this);

		if (!CommonGeneral.getInstance().isLoginServer())
			Bukkit.getPluginManager().registerEvents(new LoginListener(), this);
	}

	@Override
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
