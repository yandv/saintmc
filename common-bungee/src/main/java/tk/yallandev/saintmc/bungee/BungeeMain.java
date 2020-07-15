package tk.yallandev.saintmc.bungee;

import java.io.File;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.plugin.Plugin;
import tk.yallandev.saintmc.BungeeConst;
import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bungee.command.BungeeCommandFramework;
import tk.yallandev.saintmc.bungee.controller.BungeePunishManager;
import tk.yallandev.saintmc.bungee.controller.BungeeServerManager;
import tk.yallandev.saintmc.bungee.controller.GiftcodeController;
import tk.yallandev.saintmc.bungee.controller.StoreController;
import tk.yallandev.saintmc.bungee.listener.AccountListener;
import tk.yallandev.saintmc.bungee.listener.ChatListener;
import tk.yallandev.saintmc.bungee.listener.ConnectionListener;
import tk.yallandev.saintmc.bungee.listener.LoginListener;
import tk.yallandev.saintmc.bungee.listener.MessageListener;
import tk.yallandev.saintmc.bungee.listener.MultiserverTeleport;
import tk.yallandev.saintmc.bungee.listener.PacketListener;
import tk.yallandev.saintmc.bungee.networking.packet.BungeePacketHandler;
import tk.yallandev.saintmc.bungee.networking.redis.BungeePubSubHandler;
import tk.yallandev.saintmc.common.backend.data.ClanData;
import tk.yallandev.saintmc.common.backend.data.PlayerData;
import tk.yallandev.saintmc.common.backend.data.PunishData;
import tk.yallandev.saintmc.common.backend.data.ReportData;
import tk.yallandev.saintmc.common.backend.data.ServerData;
import tk.yallandev.saintmc.common.backend.database.mongodb.MongoConnection;
import tk.yallandev.saintmc.common.backend.database.redis.RedisDatabase;
import tk.yallandev.saintmc.common.backend.database.redis.RedisDatabase.PubSubListener;
import tk.yallandev.saintmc.common.controller.PacketController;
import tk.yallandev.saintmc.common.controller.PunishManager;
import tk.yallandev.saintmc.common.data.impl.ClanDataImpl;
import tk.yallandev.saintmc.common.data.impl.PlayerDataImpl;
import tk.yallandev.saintmc.common.data.impl.PunishDataImpl;
import tk.yallandev.saintmc.common.data.impl.ReportDataImpl;
import tk.yallandev.saintmc.common.data.impl.ServerDataImpl;
import tk.yallandev.saintmc.common.report.Report;
import tk.yallandev.saintmc.common.server.ServerManager;
import tk.yallandev.saintmc.common.server.ServerType;
import tk.yallandev.saintmc.common.server.loadbalancer.server.MinigameServer;
import tk.yallandev.saintmc.common.server.loadbalancer.server.ProxiedServer;
import tk.yallandev.saintmc.discord.DiscordMain;
import tk.yallandev.saintmc.update.UpdatePlugin;

@Getter
public class BungeeMain extends Plugin {

	@Getter
	private static BungeeMain instance;

	private CommonGeneral general;

	private PunishManager punishManager;
	private ServerManager serverManager;
	private StoreController storeManager;
	private PacketController packetController;
	private GiftcodeController giftcodeController;

	private DiscordMain discord;

	private PubSubListener pubSubListener;

	@Setter
	private boolean maintenceMode;

	@Override
	public void onLoad() {
		general = new CommonGeneral(ProxyServer.getInstance().getLogger());
		instance = this;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onEnable() {

		UpdatePlugin.Shutdown shutdown = new UpdatePlugin.Shutdown() {

			@Override
			public void stop() {
				System.exit(0);
			}

		};

		if (UpdatePlugin.update(
				new File(BungeeMain.class.getProtectionDomain().getCodeSource().getLocation().getPath()),
				"BungeeCommon", CommonConst.DOWNLOAD_KEY, shutdown))
			return;

		/**
		 * Initializing Database
		 */

		try {

			/*
			 * Backend Initialize
			 */

			MongoConnection mongo = new MongoConnection(BungeeConst.MONGO_URL);
			RedisDatabase redis = new RedisDatabase(BungeeConst.REDIS_HOSTNAME, BungeeConst.REDIS_PASSWORD, 6379);

			mongo.connect();
			redis.connect();

			PlayerData playerData = new PlayerDataImpl(mongo, redis);
			ServerData serverData = new ServerDataImpl(mongo, redis);
			ReportData reportData = new ReportDataImpl(mongo, redis);
			ClanData clanData = new ClanDataImpl(mongo, redis);
			PunishData punishData = new PunishDataImpl(mongo);

			general.setPlayerData(playerData);
			general.setServerData(serverData);
			general.setReportData(reportData);
			general.setPunishData(punishData);
			general.setClanData(clanData);

			/*
			 * Server Network Info
			 */

			getProxy().getScheduler().runAsync(getInstance(),
					pubSubListener = new PubSubListener(redis, new BungeePubSubHandler(), "server-info",
							"account-field", "clan-field", "report-field", "report-action"));

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		Logger logger = ProxyServer.getInstance().getLogger();
		Logger newLogger = new Logger("BungeeCord", null) {
			public void log(Level level, String msg, Object param1) {
				if (msg.contains("<->") || msg.contains("->"))
					return;

				super.log(level, msg, param1);
			}
		};

		newLogger.setParent(logger);

		/**
		 * Initializing Constructor
		 */

		general.setCommonPlatform(new BungeePlatform());

		new BungeeCommandFramework(this).loadCommands("tk.yallandev.saintmc.bungee.command.register");

		punishManager = new BungeePunishManager();
		serverManager = new BungeeServerManager();
		storeManager = new StoreController();
		giftcodeController = new GiftcodeController();

		packetController = new PacketController();
		packetController.registerHandler(new BungeePacketHandler());

		ProxyServer.getInstance().getServers().remove("lobby");

		/**
		 * Server Info
		 */

		ProxyServer.getInstance().registerChannel("server:packet");

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

				ProxiedServer server = getServerManager().addActiveServer(entry.getValue().get("address"),
						entry.getKey(), ServerType.valueOf(entry.getValue().get("type").toUpperCase()),
						Integer.valueOf(entry.getValue().get("maxplayers")));

				getServerManager().getServer(entry.getKey())
						.setOnlinePlayers(general.getServerData().getPlayers(entry.getKey()));
				getServerManager().getServer(entry.getKey())
						.setJoinEnabled(Boolean.valueOf(entry.getValue().get("joinenabled")));

				if (server instanceof MinigameServer) {
					MinigameServer minigameServer = (MinigameServer) server;

					minigameServer.setTime(general.getServerData().getTime(entry.getKey()));
					minigameServer.setMap(general.getServerData().getMap(entry.getKey()));
					minigameServer.setState(general.getServerData().getState(entry.getKey()));
				}

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

							BungeeMain.getInstance().getStoreManager().check();
						}

						String message = BungeeConst.BROADCAST_MESSAGES[CommonConst.RANDOM
								.nextInt(BungeeConst.BROADCAST_MESSAGES.length)];

						ProxyServer.getInstance().getPlayers().forEach(proxied -> proxied
								.sendMessage(TextComponent.fromLegacyText(message.replace("&", "§"))));
					}
				});
			}
		}, 10, 10, TimeUnit.MINUTES);

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
		ProxyServer.getInstance().getPluginManager().registerListener(getInstance(), new ChatListener());
		ProxyServer.getInstance().getPluginManager().registerListener(getInstance(), new MultiserverTeleport());
		ProxyServer.getInstance().getPluginManager().registerListener(getInstance(), new LoginListener());
		ProxyServer.getInstance().getPluginManager().registerListener(getInstance(), new PacketListener());
		ProxyServer.getInstance().getPluginManager().registerListener(getInstance(),
				new ConnectionListener(serverManager));
		ProxyServer.getInstance().getPluginManager().registerListener(getInstance(),
				new MessageListener(serverManager));
	}

	public static BungeeMain getPlugin() {
		return instance;
	}

}
