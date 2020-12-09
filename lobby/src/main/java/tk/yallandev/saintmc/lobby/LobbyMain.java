package tk.yallandev.saintmc.lobby;

import java.io.File;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;
import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.command.BukkitCommandFramework;
import tk.yallandev.saintmc.bukkit.listener.register.MoveListener;
import tk.yallandev.saintmc.common.backend.database.redis.RedisDatabase.PubSubListener;
import tk.yallandev.saintmc.common.server.ServerType;
import tk.yallandev.saintmc.common.server.loadbalancer.server.MinigameServer;
import tk.yallandev.saintmc.common.server.loadbalancer.server.ProxiedServer;
import tk.yallandev.saintmc.lobby.collectable.Collectables;
import tk.yallandev.saintmc.lobby.listener.CharacterListener;
import tk.yallandev.saintmc.lobby.listener.CombatListener;
import tk.yallandev.saintmc.lobby.listener.HologramListener;
import tk.yallandev.saintmc.lobby.listener.LauncherListener;
import tk.yallandev.saintmc.lobby.listener.LoginListener;
import tk.yallandev.saintmc.lobby.listener.ParticleListener;
import tk.yallandev.saintmc.lobby.listener.PlayerListener;
import tk.yallandev.saintmc.lobby.listener.ScoreboardListener;
import tk.yallandev.saintmc.lobby.listener.TabListener;
import tk.yallandev.saintmc.lobby.listener.WorldListener;
import tk.yallandev.saintmc.lobby.manager.PlayerManager;
import tk.yallandev.saintmc.update.UpdatePlugin;

@Getter
public class LobbyMain extends JavaPlugin {

	@Getter
	private static LobbyMain instance;

	private PubSubListener pubSubListener;

	private PlayerManager playerManager;
	private Collectables collectables;

	private String lobbyAddress;

	@Override
	public void onLoad() {
		instance = this;
		super.onLoad();
	}

	@Override
	public void onEnable() {

		UpdatePlugin.Shutdown shutdown = new UpdatePlugin.Shutdown() {

			@Override
			public void stop() {
				Bukkit.shutdown();
			}

		};

		if (UpdatePlugin.update(new File(LobbyMain.class.getProtectionDomain().getCodeSource().getLocation().getPath()),
				"Lobby", CommonConst.DOWNLOAD_KEY, shutdown))
			return;

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

		getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

		BukkitCommandFramework.INSTANCE.loadCommands(this.getClass(), "tk.yallandev.saintmc.lobby.command");

		/*
		 * viadagem que o allan faz
		 */

		playerManager = new PlayerManager();
		collectables = new Collectables();

		lobbyAddress = CommonGeneral.getInstance().getServerId().contains(".")
				? CommonGeneral.getInstance().getServerId().split("\\.")[0].toUpperCase()
				: CommonGeneral.getInstance().getServerId().toUpperCase();

		if (lobbyAddress.equalsIgnoreCase("lobby") || lobbyAddress.length() >= 4)
			lobbyAddress = "§kA?";

		BukkitMain.getInstance().setServerLog(true);
		BukkitMain.getInstance().setRemovePlayerDat(true);

		Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
		Bukkit.getPluginManager().registerEvents(new ParticleListener(), this);
		Bukkit.getPluginManager().registerEvents(new MoveListener(), this);
		Bukkit.getPluginManager().registerEvents(new ScoreboardListener(), this);
		Bukkit.getPluginManager().registerEvents(new CombatListener(), this);
		Bukkit.getPluginManager().registerEvents(new CharacterListener(), this);
		Bukkit.getPluginManager().registerEvents(new HologramListener(), this);
		Bukkit.getPluginManager().registerEvents(new LauncherListener(), this);

		Bukkit.getPluginManager().registerEvents(new TabListener(), this);
		Bukkit.getPluginManager().registerEvents(new WorldListener(), this);

		if (!CommonGeneral.getInstance().isLoginServer())
			Bukkit.getPluginManager().registerEvents(new LoginListener(), this);

		super.onEnable();
	}

	public String getLobbyAddress() {
		return lobbyAddress == null ? "A1" : lobbyAddress;
	}

	public static LobbyMain getPlugin() {
		return instance;
	}

}
