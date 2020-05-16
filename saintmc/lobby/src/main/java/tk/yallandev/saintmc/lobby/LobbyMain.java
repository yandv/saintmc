package tk.yallandev.saintmc.lobby;

import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.command.BukkitCommandFramework;
import tk.yallandev.saintmc.common.backend.database.redis.RedisDatabase;
import tk.yallandev.saintmc.common.backend.database.redis.RedisDatabase.PubSubListener;
import tk.yallandev.saintmc.common.command.CommandLoader;
import tk.yallandev.saintmc.common.server.ServerType;
import tk.yallandev.saintmc.lobby.collectable.Collectables;
import tk.yallandev.saintmc.lobby.listener.LoginListener;
import tk.yallandev.saintmc.lobby.listener.MoveListener;
import tk.yallandev.saintmc.lobby.listener.ParticleListener;
import tk.yallandev.saintmc.lobby.listener.PlayerListener;
import tk.yallandev.saintmc.lobby.listener.ScoreboardListener;
import tk.yallandev.saintmc.lobby.manager.PlayerManager;

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
		
		try {

			/*
			 * Backend Initialize
			 */

			RedisDatabase redis = new RedisDatabase("localhost", "", 6379);

			redis.connect();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		Bukkit.getPluginManager().registerEvents(new PlayerListener(), getInstance());
		Bukkit.getPluginManager().registerEvents(new ParticleListener(), getInstance());
		Bukkit.getPluginManager().registerEvents(new MoveListener(), getInstance());
		Bukkit.getPluginManager().registerEvents(new LoginListener(), getInstance());
		Bukkit.getPluginManager().registerEvents(new ScoreboardListener(), getInstance());
		
		for (Entry<String, Map<String, String>> entry : CommonGeneral.getInstance().getServerData().loadServers().entrySet()) {
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
		
		new CommandLoader(new BukkitCommandFramework(getInstance())).loadCommandsFromPackage("tk.yallandev.saintmc.lobby.command");
		
		/*
		 *  viadagem que o allan faz
		 */
		
		playerManager = new PlayerManager();
		collectables = new Collectables();
		
		lobbyAddress = CommonGeneral.getInstance().getServerId().contains(".") ? CommonGeneral.getInstance().getServerId().split("\\.")[0].toUpperCase() : CommonGeneral.getInstance().getServerId().toUpperCase();
		
		if (lobbyAddress.equalsIgnoreCase("lobby") || lobbyAddress.length() >= 4)
			lobbyAddress = "§kA?";
		
		BukkitMain.getInstance().setServerLog(true);
		
		super.onEnable();
	}
	
	public static String getLobbyAddress() {
		return lobbyAddress == null ? "A1" : lobbyAddress;
	}
	
	public static LobbyMain getPlugin() {
		return instance;
	}

}
