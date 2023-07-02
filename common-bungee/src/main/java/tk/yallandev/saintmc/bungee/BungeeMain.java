package tk.yallandev.saintmc.bungee;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.io.ByteStreams;

import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bungee.command.BungeeCommandFramework;
import tk.yallandev.saintmc.bungee.controller.BotController;
import tk.yallandev.saintmc.bungee.controller.BungeePunishManager;
import tk.yallandev.saintmc.bungee.controller.BungeeServerManager;
import tk.yallandev.saintmc.bungee.controller.GiftcodeController;
import tk.yallandev.saintmc.bungee.listener.ChatListener;
import tk.yallandev.saintmc.bungee.listener.LoginListener;
import tk.yallandev.saintmc.bungee.listener.MessageListener;
import tk.yallandev.saintmc.bungee.listener.PacketListener;
import tk.yallandev.saintmc.bungee.listener.ServerListener;
import tk.yallandev.saintmc.bungee.listener.StoreListener;
import tk.yallandev.saintmc.bungee.networking.packet.BungeePacketHandler;
import tk.yallandev.saintmc.bungee.networking.redis.BungeePubSubHandler;
import tk.yallandev.saintmc.common.backend.Credentials;
import tk.yallandev.saintmc.common.backend.data.ClanData;
import tk.yallandev.saintmc.common.backend.data.IpData;
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
import tk.yallandev.saintmc.common.data.impl.IpDataImpl;
import tk.yallandev.saintmc.common.data.impl.PlayerDataImpl;
import tk.yallandev.saintmc.common.data.impl.PunishDataImpl;
import tk.yallandev.saintmc.common.data.impl.ReportDataImpl;
import tk.yallandev.saintmc.common.data.impl.ServerDataImpl;
import tk.yallandev.saintmc.common.report.Report;
import tk.yallandev.saintmc.common.server.ServerManager;
import tk.yallandev.saintmc.common.server.ServerType;
import tk.yallandev.saintmc.common.server.loadbalancer.server.MinigameServer;
import tk.yallandev.saintmc.common.server.loadbalancer.server.ProxiedServer;
import tk.yallandev.saintmc.common.utils.string.MessageBuilder;

@Getter
public class BungeeMain extends Plugin {

    private static final String BROADCAST_PREFIX = "§6§lPENTAMC §e> ";

    private static final TextComponent[] BROADCAST = new TextComponent[]{
            new MessageBuilder(BROADCAST_PREFIX + "§fAcesse nosso ")
                    .addExtre(new MessageBuilder("§bdiscord")
                                      .setClickEvent(ClickEvent.Action.OPEN_URL, "https://" + CommonConst.DISCORD)
                                      .create())
                    .addExtre(new TextComponent(" §fe fique por dentro das novidades!")).create(),
            new MessageBuilder(BROADCAST_PREFIX + "§fUse §a/report <player>§f para denunciar um jogador!").create(),
            new MessageBuilder(BROADCAST_PREFIX + "§fCompre vip em nossa ")
                    .addExtre(new MessageBuilder("§aloja")
                                      .setClickEvent(ClickEvent.Action.OPEN_URL, "https://" + CommonConst.STORE)
                                      .create())
                    .addExtre(new TextComponent("§f!")).create(),
            new MessageBuilder(BROADCAST_PREFIX
                               + "§fO servidor está em fase §1§lBETA§f, caso encontre algum bug reporte em nosso ")
                    .addExtre(new MessageBuilder("§bdiscord!")
                                      .setClickEvent(ClickEvent.Action.OPEN_URL, "https://" + CommonConst.DISCORD)
                                      .create())
                    .create()};

    @Getter
    private static BungeeMain instance;

    private CommonGeneral general;

    private PunishManager punishManager;
    private BotController botController;
    private ServerManager serverManager;

    private PacketController packetController;
    private GiftcodeController giftcodeController;

    private PubSubListener pubSubListener;

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
        loadConfiguration();

        /**
         * Initializing Database
         */

        try {

            /*
             * Backend Initialize
             */

            MongoConnection mongoConnection = new MongoConnection(new Credentials(
                    getConfig().getString("mongodb.hostname", "127.0.0.1"),
                    getConfig().getString("mongodb.username", "root"), getConfig().getString("mongodb.password", ""),
                    getConfig().getString("mongodb.database", "admin"), 27017));
            RedisDatabase redisDatabase = new RedisDatabase(getConfig().getString("redis.hostname", "127.0.0.1"),
                                                            getConfig().getString("redis.password", ""), 6379);

            mongoConnection.connect();
            redisDatabase.connect();

            PlayerData playerData = new PlayerDataImpl(mongoConnection, redisDatabase);
            ServerData serverData = new ServerDataImpl(mongoConnection, redisDatabase);
            ReportData reportData = new ReportDataImpl(mongoConnection, redisDatabase);
            ClanData clanData = new ClanDataImpl(mongoConnection, redisDatabase);
            PunishData punishData = new PunishDataImpl(mongoConnection);
            IpData ipData = new IpDataImpl(mongoConnection);

            general.setPlayerData(playerData);
            general.setServerData(serverData);
            general.setReportData(reportData);
            general.setClanData(clanData);
            general.setPunishData(punishData);
            general.setIpData(ipData);

            loadRedis(redisDatabase);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        Logger logger = ProxyServer.getInstance().getLogger();
        Logger newLogger = new Logger("BungeeCord", null) {

            public void log(Level level, String msg, Object param1) {
				if (msg.contains("<->") || msg.contains("->")) {
					return;
				}

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
				if (!entry.getValue().containsKey("type")) {
					continue;
				}

				if (!entry.getValue().containsKey("address")) {
					continue;
				}

				if (!entry.getValue().containsKey("maxplayers")) {
					continue;
				}

				if (!entry.getValue().containsKey("onlineplayers")) {
					continue;
				}

				if (ServerType.valueOf(entry.getValue().get("type").toUpperCase()) == ServerType.NETWORK) {
					continue;
				}

                ProxiedServer server = getServerManager().addActiveServer(entry.getValue().get("address"),
                                                                          entry.getKey(), ServerType.valueOf(
                                entry.getValue().get("type").toUpperCase()),
                                                                          Integer.valueOf(
                                                                                  entry.getValue().get("maxplayers")));

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

        ProxyServer.getInstance().getScheduler().schedule(this, () -> {
            TextComponent message = BROADCAST[CommonConst.RANDOM.nextInt(BROADCAST.length)];

            ProxyServer.getInstance().broadcast(message);
        }, 0, 5, TimeUnit.MINUTES);

        System.setProperty("DEBUG.MONGO", "false");
        System.setProperty("DB.TRACE", "false");

        registerListener();
    }

    public void loadRedis(RedisDatabase redisDatabase) {
		if (redisTask != null) {
			redisTask.cancel();
		}

        redisTask = getProxy().getScheduler().runAsync(this,
                                                       pubSubListener = new PubSubListener(redisDatabase,
                                                                                           new BungeePubSubHandler(),
                                                                                           "server-info",
                                                                                           "account-field",
                                                                                           "clan-field", "report-field",
                                                                                           "report-action",
                                                                                           "server-members"));
    }

    @Override
    public void onDisable() {
        general.getServerData().stopServer();

        general.getServerData().closeConnection();
        general.getPlayerData().closeConnection();
    }

    private void registerListener() {
        getProxy().getPluginManager().registerListener(this, new LoginListener());
        getProxy().getPluginManager().registerListener(this, new ChatListener());
        getProxy().getPluginManager().registerListener(this, new PacketListener());
        getProxy().getPluginManager().registerListener(this, new MessageListener(serverManager));
        getProxy().getPluginManager().registerListener(this, new ServerListener(serverManager));
        getProxy().getPluginManager().registerListener(this, new StoreListener());
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
