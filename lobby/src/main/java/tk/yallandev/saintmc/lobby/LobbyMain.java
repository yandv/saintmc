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
import tk.yallandev.saintmc.common.command.CommandLoader;
import tk.yallandev.saintmc.common.server.ServerType;
import tk.yallandev.saintmc.lobby.collectable.Collectables;
import tk.yallandev.saintmc.lobby.listener.CharacterListener;
import tk.yallandev.saintmc.lobby.listener.HologramListener;
import tk.yallandev.saintmc.lobby.listener.LoginListener;
import tk.yallandev.saintmc.lobby.listener.ParticleListener;
import tk.yallandev.saintmc.lobby.listener.PlayerListener;
import tk.yallandev.saintmc.lobby.listener.ScoreboardListener;
import tk.yallandev.saintmc.lobby.manager.PlayerManager;
import tk.yallandev.saintmc.update.UpdatePlugin;

@Getter
public class LobbyMain extends JavaPlugin {

	@Getter
	private static LobbyMain instance;

	private static String lobbyAddress;

	private PubSubListener pubSubListener;

	private PlayerManager playerManager;
	private Collectables collectables;

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

		Bukkit.getPluginManager().registerEvents(new PlayerListener(), getInstance());
		Bukkit.getPluginManager().registerEvents(new ParticleListener(), getInstance());
		Bukkit.getPluginManager().registerEvents(new MoveListener(), getInstance());
		Bukkit.getPluginManager().registerEvents(new ScoreboardListener(), getInstance());
		Bukkit.getPluginManager().registerEvents(new CharacterListener(), getInstance());
		Bukkit.getPluginManager().registerEvents(new HologramListener(), getInstance());

		if (CommonGeneral.getInstance().isLoginServer())
			Bukkit.getPluginManager().registerEvents(new LoginListener(), getInstance());
		
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

				BukkitMain.getInstance().getServerManager().addActiveServer(entry.getValue().get("address"),
						entry.getKey(), ServerType.valueOf(entry.getValue().get("type").toUpperCase()),
						Integer.valueOf(entry.getValue().get("maxplayers")));
				BukkitMain.getInstance().getServerManager().getServer(entry.getKey())
						.setOnlinePlayers(CommonGeneral.getInstance().getServerData().getPlayers(entry.getKey()));
			} catch (Exception e) {
			}
		}

		getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

		new CommandLoader(new BukkitCommandFramework(getInstance()))
				.loadCommandsFromPackage("tk.yallandev.saintmc.lobby.command");

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

		super.onEnable();
	}

	public static String getLobbyAddress() {
		return lobbyAddress == null ? "A1" : lobbyAddress;
	}

	public static LobbyMain getPlugin() {
		return instance;
	}

}
