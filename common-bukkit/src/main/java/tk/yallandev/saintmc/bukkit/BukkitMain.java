package tk.yallandev.saintmc.bukkit;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import lombok.Getter;
import lombok.Setter;
import net.saintmc.anticheat.AnticheatController;
import net.saintmc.anticheat.check.CheckController;
import net.saintmc.anticheat.check.register.CombatCheck;
import net.saintmc.anticheat.check.register.MovementCheck;
import net.saintmc.anticheat.listener.StorageListener;
import tk.yallandev.hologramapi.controller.HologramController;
import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.anticheat.alert.AlertController;
import tk.yallandev.saintmc.bukkit.api.character.CharacterListener;
import tk.yallandev.saintmc.bukkit.api.cooldown.CooldownController;
import tk.yallandev.saintmc.bukkit.api.item.ActionItemListener;
import tk.yallandev.saintmc.bukkit.api.listener.ManualRegisterableListener;
import tk.yallandev.saintmc.bukkit.api.menu.MenuListener;
import tk.yallandev.saintmc.bukkit.api.protocol.ProtocolGetter;
import tk.yallandev.saintmc.bukkit.api.server.Server;
import tk.yallandev.saintmc.bukkit.api.server.impl.ServerImpl;
import tk.yallandev.saintmc.bukkit.api.vanish.AdminMode;
import tk.yallandev.saintmc.bukkit.api.worldedit.WorldeditController;
import tk.yallandev.saintmc.bukkit.command.BukkitCommandFramework;
import tk.yallandev.saintmc.bukkit.controller.SkinController;
import tk.yallandev.saintmc.bukkit.event.LocationChangeEvent;
import tk.yallandev.saintmc.bukkit.exploit.Exploit;
import tk.yallandev.saintmc.bukkit.networking.packet.BukkitPacketController;
import tk.yallandev.saintmc.bukkit.networking.redis.BukkitPubSubHandler;
import tk.yallandev.saintmc.bukkit.permission.PermissionManager;
import tk.yallandev.saintmc.bukkit.scheduler.UpdateScheduler;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.backend.data.ClanData;
import tk.yallandev.saintmc.common.backend.data.PlayerData;
import tk.yallandev.saintmc.common.backend.data.ReportData;
import tk.yallandev.saintmc.common.backend.data.ServerData;
import tk.yallandev.saintmc.common.backend.data.StatusData;
import tk.yallandev.saintmc.common.backend.database.mongodb.MongoConnection;
import tk.yallandev.saintmc.common.backend.database.redis.RedisDatabase;
import tk.yallandev.saintmc.common.backend.database.redis.RedisDatabase.PubSubListener;
import tk.yallandev.saintmc.common.data.impl.ClanDataImpl;
import tk.yallandev.saintmc.common.data.impl.PlayerDataImpl;
import tk.yallandev.saintmc.common.data.impl.ReportDataImpl;
import tk.yallandev.saintmc.common.data.impl.ServerDataImpl;
import tk.yallandev.saintmc.common.data.impl.StatusDataImpl;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.report.Report;
import tk.yallandev.saintmc.common.server.ServerManager;
import tk.yallandev.saintmc.common.server.ServerType;
import tk.yallandev.saintmc.common.server.loadbalancer.server.MinigameState;
import tk.yallandev.saintmc.common.utils.ClassGetter;
import tk.yallandev.saintmc.update.UpdatePlugin;

@Getter
@SuppressWarnings("deprecation")
public class BukkitMain extends JavaPlugin {

	public static final boolean BUNGEECORD = true;
	public static final boolean IP_WHITELIST = true;

	@Getter
	private static BukkitMain instance;

	private CommonGeneral general;
	private ProtocolManager procotolManager;

	private SkinController skinManager;
	private WorldeditController worldeditController;
	private AnticheatController anticheatController;

	private PermissionManager permissionManager;
	private ServerManager serverManager;

	private HologramController hologramController;
	private BukkitPacketController packetController;

	private PubSubListener pubSubListener;
	private Map<String, Location> location = new HashMap<>();

	@Setter
	private Server serverConfig;

	@Setter
	private boolean tagControl = true;
	@Setter
	private boolean oldTag = false;
	@Setter
	private boolean removePlayerDat = true;
	@Setter
	private boolean serverLog = false;

	@Override
	public void onLoad() {
		instance = this;
		procotolManager = ProtocolLibrary.getProtocolManager();
		general = new CommonGeneral(Bukkit.getLogger());
	}

	@Override
	public void onEnable() {
		UpdatePlugin.Shutdown shutdown = new UpdatePlugin.Shutdown() {

			@Override
			public void stop() {
				Bukkit.shutdown();
			}

		};

		if (UpdatePlugin.update(
				new File(BukkitMain.class.getProtectionDomain().getCodeSource().getLocation().getPath()),
				"BukkitCommon", CommonConst.DOWNLOAD_KEY, shutdown))
			return;

		try {

			MongoConnection mongo = new MongoConnection(Bukkit.getIp().equals("0.0.0.0") ? CommonConst.MONGO_URL
					: CommonConst.MONGO_URL.replace("localhost", "35.198.32.68"));
			RedisDatabase redis = new RedisDatabase(
					Bukkit.getIp().equals("0.0.0.0") ? CommonConst.REDIS_HOSTNAME
							: CommonConst.REDIS_HOSTNAME.replace("localhost", "35.198.32.68"),
					CommonConst.REDIS_PASSWORD, 6379);

			mongo.connect();
			redis.connect();

			PlayerData playerData = new PlayerDataImpl(mongo, redis);
			ServerData serverData = new ServerDataImpl(mongo, redis);
			ReportData reportData = new ReportDataImpl(mongo, redis);
			StatusData statusData = new StatusDataImpl(mongo);
			ClanData clanData = new ClanDataImpl(mongo, redis);

			general.setPlayerData(playerData);
			general.setServerData(serverData);
			general.setReportData(reportData);
			general.setStatusData(statusData);
			general.setClanData(clanData);

			general.setServerAddress(getConfig().getString("serverAddress", "0.0.0.0") + ":" + Bukkit.getPort());
			general.setServerId(general.getServerData().getServerId("127.0.0.1:" + Bukkit.getPort()));
			general.setServerType(general.getServerData().getServerType("127.0.0.1:" + Bukkit.getPort()));

			if (general.getServerType().canSendData())
				getServer().getScheduler().runTaskAsynchronously(getInstance(),
						pubSubListener = new PubSubListener(redis, new BukkitPubSubHandler(), "account-field",
								"clan-field", "report-field", "report-action", "server-info"));

		} catch (Exception ex) {
			ex.printStackTrace();
			Bukkit.shutdown();
			return;
		}

		/*
		 * Server Info
		 */

		saveDefaultConfig();

		general.debug("The server has been loaded " + general.getServerAddress() + " (" + general.getServerId() + " - "
				+ general.getServerType().toString() + ")");

		general.getServerData().startServer(Bukkit.getMaxPlayers());

		if (general.getServerType() == ServerType.HUNGERGAMES)
			general.getServerData().updateStatus(MinigameState.NONE, 300);

		general.debug("The server has been sent the start message to redis!");

		for (Report report : general.getReportData().loadReports()) {
			general.getReportManager().loadReport(report);
		}

		general.debug("The server has been loaded all the reports!");

		/*
		 * Initializing Constructor
		 */

		general.setCommonPlatform(new BukkitPlatform());

		skinManager = new SkinController();
		worldeditController = new WorldeditController();

		CheckController checkController = new CheckController();
		anticheatController = new AnticheatController(checkController, new AlertController());

		checkController.registerCheck(new MovementCheck());
		checkController.registerCheck(new CombatCheck(this));

		hologramController = new HologramController(getInstance());
		packetController = new BukkitPacketController();

		permissionManager = new PermissionManager(this);
		permissionManager.onEnable();

		serverManager = new ServerManager();
		serverConfig = new ServerImpl();
		general.getServerData().setJoinEnabled(!serverConfig.isWhitelist());

		ProtocolGetter.foundDependencies();

		/*
		 * BungeeCord Message Listener
		 */

		getServer().getMessenger().registerOutgoingPluginChannel(this, "server:packet");

		getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord",
				(String channel, Player player, byte[] message) -> {
					ByteArrayDataInput in = ByteStreams.newDataInput(message);
					String subchannel = in.readUTF();

					if (subchannel.equalsIgnoreCase("BungeeTeleport")) {
						String uniqueId = in.readUTF();

						if (!Member.hasGroupPermission(player.getUniqueId(), Group.YOUTUBERPLUS)) {
							player.sendMessage("§c§l> §fVocê não tem §cpermissão§f para teletransportar!");
							return;
						}

						AdminMode.getInstance().setAdmin(player, Member.getMember(player.getUniqueId()));
						Player p = BukkitMain.getInstance().getServer().getPlayer(UUID.fromString(uniqueId));
						player.chat("/tp " + p.getName());
					}

				});

		getServer().getMessenger().registerOutgoingPluginChannel(this, "Lunar-Client");

		/*
		 * Register Listener
		 */

		registerListener();
		registerExploit();

		/*
		 * Initializing Command
		 */

		new BukkitRunnable() {

			@Override
			public void run() {
				unregisterCommands("icanhasbukkit", "?", "about", "help", "ban", "ban-ip", "banlist", "clear", "deop",
						"stop", "op", "difficulty", "effect", "enchant", "give", "kick", "list", "me", "say",
						"scoreboard", "seed", "spawnpoint", "spreadplayers", "summon", "tell", "tellraw", "testfor",
						"testforblocks", "tp", "weather", "xp", "reload", "rl", "worldborder", "achievement",
						"blockdata", "clone", "debug", "defaultgamemode", "entitydata", "execute", "fill", "gamemode",
						"pardon", "pardon-ip", "replaceitem", "setidletimeout", "stats", "testforblock", "title",
						"trigger", "viaver", "protocolsupport", "ps", "holograms", "hd", "holo", "hologram", "restart",
						"stop", "filter", "packetlog", "pl", "plugins", "whitelist");

				BukkitCommandFramework.INSTANCE.loadCommands("tk.yallandev.saintmc.bukkit.command.register");

				Bukkit.setWhitelist(false);
			}
		}.runTaskLater(this, 3l);

		getServer().getScheduler().runTaskTimer(this, new UpdateScheduler(), 1, 1);
	}

	@Override
	public void onDisable() {
		general.getServerData().stopServer();

		general.getServerData().closeConnection();
		general.getPlayerData().closeConnection();

		permissionManager.onDisable();
	}

	private void registerExploit() {
		for (Class<?> classes : ClassGetter.getClassesForPackage(getClass(),
				"tk.yallandev.saintmc.bukkit.exploit.register")) {
			if (Exploit.class.isAssignableFrom(classes)) {
				try {
					((Exploit) classes.newInstance()).register();
				} catch (Exception e) {
					e.printStackTrace();
					CommonGeneral.getInstance().getLogger()
							.warning("Couldn't load " + classes.getSimpleName() + " listener!");
				}
			}
		}
	}

	private void registerListener() {
		PluginManager pm = Bukkit.getPluginManager();

		for (Class<?> classes : ClassGetter.getClassesForPackage(getClass(),
				"tk.yallandev.saintmc.bukkit.listener.register")) {
			if (Listener.class.isAssignableFrom(classes)
					&& !ManualRegisterableListener.class.isAssignableFrom(classes)) {
				try {
					Listener listener = (Listener) classes.newInstance();
					Bukkit.getPluginManager().registerEvents(listener, getInstance());
				} catch (Exception e) {
					e.printStackTrace();
					CommonGeneral.getInstance().getLogger()
							.warning("Couldn't load " + classes.getSimpleName() + " listener!");
				}
			}
		}

		pm.registerEvents(new ActionItemListener(), getInstance());
		pm.registerEvents(new CharacterListener(), getInstance());
		pm.registerEvents(new MenuListener(), getInstance());
		pm.registerEvents(new CooldownController(), getInstance());

		pm.registerEvents(new StorageListener(), getInstance());
	}

	public void sendPlayerToLobby(Player p) {
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);

		try {
			out.writeUTF("Lobby");
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}

		p.sendPluginMessage(getInstance(), "BungeeCord", b.toByteArray());
	}

	public void sendPlayerToEvent(Player p) {
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);

		try {
			out.writeUTF("Event");
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}

		p.sendPluginMessage(getInstance(), "BungeeCord", b.toByteArray());
	}

	public void sendPlayer(Player p, String server) {
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);

		try {
			out.writeUTF("Connect");
			out.writeUTF(server);
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}

		p.sendPluginMessage(getInstance(), "BungeeCord", b.toByteArray());
	}

	public Location getLocationFromConfig(String config) {
		config = config.toLowerCase();

		if (location.containsKey(config))
			return location.get(config);

		FileConfiguration file = getConfig();

		if (!file.contains(config + ".x")) {
			return Bukkit.getWorlds().get(0).getSpawnLocation();
		}

		World world = Bukkit.getWorld(file.getString(config + ".world"));

		if (world == null) {
			world = getServer().createWorld(new WorldCreator(file.getString(config + ".world")));
			CommonGeneral.getInstance().getLogger().info("The world " + world.getName() + " has loaded successfully.");
		}

		Location location = new Location(world, file.getDouble(config + ".x"), file.getDouble(config + ".y"),
				file.getDouble(config + ".z"));

		location.setPitch(file.getLong(config + ".pitch"));
		location.setYaw(file.getLong(config + ".yaw"));
		this.location.put(config, location);

		return location;
	}

	public void registerLocationInConfig(Location location, String config) {
		if (this.location.containsKey(config)) {
			LocationChangeEvent event = new LocationChangeEvent(config, this.location.get(config), location);
			Bukkit.getPluginManager().callEvent(event);

			if (event.isCancelled())
				return;
		}

		config = config.toLowerCase();
		this.location.put(config, location);

		FileConfiguration file = getConfig();

		file.set(config + ".world", location.getWorld().getName());
		file.set(config + ".x", location.getX());
		file.set(config + ".y", location.getY());
		file.set(config + ".z", location.getZ());
		file.set(config + ".pitch", location.getPitch());
		file.set(config + ".yaw", location.getYaw());

		saveConfig();
	}

	public void removeLocationInConfig(String config) {
		this.location.remove(config);
		FileConfiguration file = getConfig();

		file.set(config + ".world", null);
		file.set(config + ".x", null);
		file.set(config + ".y", null);
		file.set(config + ".z", null);
		file.set(config + ".pitch", null);
		file.set(config + ".yaw", null);

		saveConfig();
	}

	public void unregisterCommands(String... commands) {
		try {
			Field f1 = Bukkit.getServer().getClass().getDeclaredField("commandMap");
			f1.setAccessible(true);

			CommandMap commandMap = (CommandMap) f1.get(Bukkit.getServer());
			Field f2 = commandMap.getClass().getDeclaredField("knownCommands");

			f2.setAccessible(true);
			Map<String, Command> knownCommands = (HashMap<String, Command>) f2.get(commandMap);

			for (String command : commands) {
				if (knownCommands.containsKey(command)) {
					knownCommands.remove(command);

					List<String> aliases = new ArrayList<>();

					for (String key : knownCommands.keySet()) {
						if (!key.contains(":"))
							continue;

						String substr = key.substring(key.indexOf(":") + 1);

						if (substr.equalsIgnoreCase(command)) {
							aliases.add(key);
						}
					}

					for (String alias : aliases) {
						knownCommands.remove(alias);
					}
				}
			}

			Iterator<Entry<String, Command>> iterator = knownCommands.entrySet().iterator();

			while (iterator.hasNext()) {
				Entry<String, Command> entry = iterator.next();

				if (entry.getKey().contains(":") || entry.getValue().getLabel().contains(":")) {
					entry.getValue().unregister(commandMap);
					iterator.remove();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
		return new ChunkGenerator() {

			@Override
			public List<BlockPopulator> getDefaultPopulators(World world) {
				return Arrays.asList(new BlockPopulator[0]);
			}

			@Override
			public boolean canSpawn(World world, int x, int z) {
				return true;
			}

			public int xyzToByte(int x, int y, int z) {
				return (x * 16 + z) * 128 + y;
			}

			@Override
			public byte[] generate(World world, Random rand, int chunkx, int chunkz) {
				byte[] result = new byte[32768];
				if ((chunkx == 0) && (chunkz == 0)) {
					result[xyzToByte(0, 64, 0)] = ((byte) Material.BEDROCK.getId());
				}
				return result;
			}

			@Override
			public Location getFixedSpawnLocation(World world, Random random) {
				return new Location(world, 0.0D, 66.0D, 0.0D);
			}

		};
	}

}
