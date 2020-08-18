package tk.yallandev.saintmc.bungee;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.io.ByteStreams;

import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import tk.yallandev.saintmc.BungeeConst;
import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bungee.command.BungeeCommandFramework;
import tk.yallandev.saintmc.bungee.controller.BotController;
import tk.yallandev.saintmc.bungee.controller.BungeePunishManager;
import tk.yallandev.saintmc.bungee.controller.BungeeServerManager;
import tk.yallandev.saintmc.bungee.controller.GiftcodeController;
import tk.yallandev.saintmc.bungee.controller.StoreController;
import tk.yallandev.saintmc.bungee.listener.AccountListener;
import tk.yallandev.saintmc.bungee.listener.ChatListener;
import tk.yallandev.saintmc.bungee.listener.ClientListener;
import tk.yallandev.saintmc.bungee.listener.ConnectionListener;
import tk.yallandev.saintmc.bungee.listener.LoginListener;
import tk.yallandev.saintmc.bungee.listener.MessageListener;
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
import tk.yallandev.saintmc.common.command.CommandSender;
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

	private BotController botController;

	private ServerManager serverManager;

	private StoreController storeController;
	private PacketController packetController;
	private GiftcodeController giftcodeController;

	private DiscordMain discord;

	private RedisDatabase redis;

	private PubSubListener pubSubListener;
	private CommandSender consoleSender = new CommandSender() {

		@Override
		public void sendMessage(BaseComponent[] fromLegacyText) {
			ProxyServer.getInstance().getConsole().sendMessage(fromLegacyText);
		}

		@Override
		public void sendMessage(BaseComponent str) {
			ProxyServer.getInstance().getConsole().sendMessage(str);
		}

		@Override
		public void sendMessage(String str) {
			ProxyServer.getInstance().getConsole().sendMessage(str);
		}

		@Override
		public boolean isPlayer() {
			return false;
		}

		@Override
		public UUID getUniqueId() {
			return UUID.randomUUID();
		}

		@Override
		public String getName() {
			return "CONSOLE";
		}
	};

	private Configuration config;

	@Setter
	private boolean maintenceMode;

	private ScheduledTask redisTask;

	@Override
	public void onLoad() {
		general = new CommonGeneral(ProxyServer.getInstance().getLogger());
		instance = this;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onEnable() {

		try {
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
		} catch (Exception ex) {
			CommonGeneral.getInstance().debug("Couldn't connect to http://apidata.saintmc.net/!");
		}

		loadConfiguration();

		/**
		 * Initializing Database
		 */

		try {

			/*
			 * Backend Initialize
			 */

			MongoConnection mongo = new MongoConnection(
					BungeeConst.MONGO_URL.replace("127.0.0.1", getConfig().getString("mongodb-address", "127.0.0.1")));
			redis = new RedisDatabase(BungeeConst.REDIS_HOSTNAME.replace("127.0.0.1",
					getConfig().getString("mongodb-address", "127.0.0.1")), BungeeConst.REDIS_PASSWORD, 6379);

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

		botController = new BotController();
		punishManager = new BungeePunishManager();
		serverManager = new BungeeServerManager();
		storeController = new StoreController();
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

		ProxyServer.getInstance().getScheduler().schedule(this, new Runnable() {

			@Override
			public void run() {
				CommonGeneral.getInstance().getCommonPlatform().runAsync(new Runnable() {

					@Override
					public void run() {

						if (ProxyServer.getInstance().getPlayers().size() > 0) {
							CommonGeneral.getInstance().debug("Estamos verificando os pedidos!");

							getStoreController().check(consoleSender);
						}

						String message = BungeeConst.BROADCAST_MESSAGES[CommonConst.RANDOM
								.nextInt(BungeeConst.BROADCAST_MESSAGES.length)];

						ProxyServer.getInstance().getPlayers().forEach(proxied -> proxied
								.sendMessage(TextComponent.fromLegacyText(message.replace("&", "§"))));
					}
				});
			}
		}, 10, 10, TimeUnit.MINUTES);

		ProxyServer.getInstance().getScheduler().runAsync(this, new Runnable() {

			@Override
			public void run() {
				discord = new DiscordMain();
			}
		});

		System.setProperty("DEBUG.MONGO", "false");
		System.setProperty("DB.TRACE", "false");

		registerListener();

		/*
		 * Server Network Info
		 */

		loadRedis();
	}

	public void loadRedis() {
		if (redisTask != null)
			redisTask.cancel();

		redisTask = getProxy().getScheduler().runAsync(this,
				pubSubListener = new PubSubListener(redis, new BungeePubSubHandler(), "server-info", "account-field",
						"clan-field", "report-field", "report-action"));
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
		ProxyServer.getInstance().getPluginManager().registerListener(getInstance(), new LoginListener());
		ProxyServer.getInstance().getPluginManager().registerListener(getInstance(), new PacketListener());
		ProxyServer.getInstance().getPluginManager().registerListener(getInstance(), new ClientListener());
		ProxyServer.getInstance().getPluginManager().registerListener(getInstance(),
				new ConnectionListener(serverManager));
		ProxyServer.getInstance().getPluginManager().registerListener(getInstance(),
				new MessageListener(serverManager));
	}

	private void loadConfiguration() {
		try {
			if (!getDataFolder().exists()) {
				getDataFolder().mkdir();
			}

			File configFile = new File(getDataFolder(), "config.yml");

			if (!configFile.exists()) {
				try {
					configFile.createNewFile();
					try (InputStream is = getResourceAsStream("config.yml");
							OutputStream os = new FileOutputStream(configFile)) {
						ByteStreams.copy(is, os);
					}
				} catch (IOException e) {
					throw new RuntimeException("Unable to create configuration file", e);
				}
			}

			config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public static BungeeMain getPlugin() {
		return instance;
	}

}
