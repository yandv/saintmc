package tk.yallandev.saintmc.bungee;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.plugin.Plugin;
import tk.yallandev.saintmc.BungeeConst;
import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bungee.command.BungeeCommandFramework;
import tk.yallandev.saintmc.bungee.listener.AccountListener;
import tk.yallandev.saintmc.bungee.listener.ChatListener;
import tk.yallandev.saintmc.bungee.listener.ConnectionListener;
import tk.yallandev.saintmc.bungee.listener.MessageListener;
import tk.yallandev.saintmc.bungee.listener.MultiserverTeleport;
import tk.yallandev.saintmc.bungee.manager.BungeePunishManager;
import tk.yallandev.saintmc.bungee.manager.BungeeServerManager;
import tk.yallandev.saintmc.bungee.manager.StoreManager;
import tk.yallandev.saintmc.bungee.redis.BungeePubSubHandler;
import tk.yallandev.saintmc.common.backend.data.PlayerData;
import tk.yallandev.saintmc.common.backend.data.ReportData;
import tk.yallandev.saintmc.common.backend.data.ServerData;
import tk.yallandev.saintmc.common.backend.database.mongodb.MongoDatabase;
import tk.yallandev.saintmc.common.backend.database.redis.RedisDatabase;
import tk.yallandev.saintmc.common.backend.database.redis.RedisDatabase.PubSubListener;
import tk.yallandev.saintmc.common.command.CommandLoader;
import tk.yallandev.saintmc.common.controller.PunishManager;
import tk.yallandev.saintmc.common.data.PlayerDataImpl;
import tk.yallandev.saintmc.common.data.ReportDataImpl;
import tk.yallandev.saintmc.common.data.ServerDataImpl;
import tk.yallandev.saintmc.common.report.Report;
import tk.yallandev.saintmc.common.server.ServerManager;
import tk.yallandev.saintmc.common.server.ServerType;
import tk.yallandev.saintmc.discord.DiscordMain;

@Getter
public class BungeeMain extends Plugin {

	@Getter
	private static BungeeMain instance;

	private CommonGeneral general;

	private PunishManager punishManager;
	private ServerManager serverManager;
	private StoreManager storeManager;

	private DiscordMain discord;

	private PubSubListener pubSubListener;

	@Override
	public void onLoad() {
		general = new CommonGeneral(ProxyServer.getInstance().getLogger());
		instance = this;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onEnable() {

		/**
		 * Initializing Database
		 */

		try {

			/*
			 * Backend Initialize
			 */

			MongoDatabase mongo = new MongoDatabase(CommonConst.MONGO_URL);
			RedisDatabase redis = new RedisDatabase("127.0.0.1", "", 6379);

			mongo.connect();
			redis.connect();

			PlayerData playerData = new PlayerDataImpl(mongo, redis);
			ServerData serverData = new ServerDataImpl(mongo, redis);
			ReportData reportData = new ReportDataImpl(mongo, redis);

			general.setPlayerData(playerData);
			general.setServerData(serverData);
			general.setReportData(reportData);

			/*
			 * Server Network Info
			 */

			getProxy().getScheduler().runAsync(getInstance(), pubSubListener = new PubSubListener(redis,
					new BungeePubSubHandler(), "server-info", "account-field", "report-field", "report-action"));

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		/**
		 * Initializing Constructor
		 */

		general.setCommonPlatform(new BungeePlatform());

		try {
			new CommandLoader(new BungeeCommandFramework(getInstance()))
					.loadCommandsFromPackage("tk.yallandev.saintmc.bungee.command.register");
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e1) {
			e1.printStackTrace();
		}

		punishManager = new BungeePunishManager();
		serverManager = new BungeeServerManager();
		storeManager = new StoreManager();

		ProxyServer.getInstance().getServers().remove("lobby");

		/**
		 * Server Info
		 */

		ListenerInfo info = getProxy().getConfig().getListeners().iterator().next();
		general.setServerAddress(info.getHost().getHostString() + ":" + info.getHost().getPort());
		general.setServerId(general.getServerData().getServerId(general.getServerAddress()));
		general.setServerType(general.getServerData().getServerType(general.getServerAddress()));

		general.debug("The server has been loaded " + general.getServerAddress() + " (" + general.getServerId() + " - "
				+ general.getServerType().toString() + ")");

		general.getServerData().startServer(info.getMaxPlayers());

		general.debug("The server has been sent the start message to redis!");

		for (Entry<String, Map<String, String>> entry : general.getServerData().loadServers().entrySet()) {
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

				BungeeMain.getPlugin().getServerManager().addActiveServer(entry.getValue().get("address"),
						entry.getKey(), ServerType.valueOf(entry.getValue().get("type").toUpperCase()),
						Integer.valueOf(entry.getValue().get("maxplayers")));
				BungeeMain.getPlugin().getServerManager().getServer(entry.getKey())
						.setOnlinePlayers(general.getServerData().getPlayers(entry.getKey()));
			} catch (Exception e) {
			}
		}

		general.debug("The server has been loaded all the servers!");

		for (Report report : general.getReportData().loadReports()) {
			general.getReportManager().loadReport(report);
		}

		general.debug("The server has been loaded all the reports!");

		ProxyServer.getInstance().getScheduler().schedule(getInstance(), new Runnable() {

			@Override
			public void run() {
				CommonGeneral.getInstance().getCommonPlatform().runAsync(new Runnable() {

					@Override
					public void run() {

						if (ProxyServer.getInstance().getPlayers().size() > 0) {
							CommonGeneral.getInstance().debug("Estamos verificando os pedidos!");

							boolean check = BungeeMain.getInstance().getStoreManager().check();

							if (check)
								CommonGeneral.getInstance().debug("O(s) pedido(s) foram processado(s)");
							else
								CommonGeneral.getInstance().debug("Nenhum pedido foi encontrado!");
						}

						String message = BungeeConst.BROADCAST_MESSAGES[CommonConst.RANDOM.nextInt(BungeeConst.BROADCAST_MESSAGES.length)];

						ProxyServer.getInstance().getPlayers().forEach(proxied -> proxied
								.sendMessage(TextComponent.fromLegacyText(message.replace("&", "§"))));
					}
				});
			}
		}, 0, 5, TimeUnit.MINUTES);

		discord = new DiscordMain();

		System.setProperty("DEBUG.MONGO", "false");
		System.setProperty("DB.TRACE", "false");
		
		registerListener();
	}

	@Override
	public void onDisable() {
		general.getServerData().stopServer();

		general.getServerData().closeConnection();
		general.getPlayerData().closeConnection();
	}

	private void registerListener() {
		ProxyServer.getInstance().getPluginManager().registerListener(getInstance(), new AccountListener());
		ProxyServer.getInstance().getPluginManager().registerListener(getInstance(),
				new ConnectionListener(serverManager));
		ProxyServer.getInstance().getPluginManager().registerListener(getInstance(),
				new MessageListener(serverManager));
		ProxyServer.getInstance().getPluginManager().registerListener(getInstance(), new ChatListener());
		ProxyServer.getInstance().getPluginManager().registerListener(getInstance(), new MultiserverTeleport());
	}

	public static BungeeMain getPlugin() {
		return instance;
	}

}
