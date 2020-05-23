package tk.yallandev.saintmc.bukkit;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import lombok.Getter;
import lombok.Setter;
import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.api.cooldown.CooldownAPI;
import tk.yallandev.saintmc.bukkit.api.hologram.HologramListener;
import tk.yallandev.saintmc.bukkit.api.item.ActionItemListener;
import tk.yallandev.saintmc.bukkit.api.menu.MenuListener;
import tk.yallandev.saintmc.bukkit.api.vanish.AdminMode;
import tk.yallandev.saintmc.bukkit.command.BukkitCommandFramework;
import tk.yallandev.saintmc.bukkit.controller.SkinController;
import tk.yallandev.saintmc.bukkit.permission.PermissionManager;
import tk.yallandev.saintmc.bukkit.protocol.ProtocolGetter;
import tk.yallandev.saintmc.bukkit.redis.BukkitPubSubHandler;
import tk.yallandev.saintmc.bukkit.scheduler.UpdateScheduler;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.backend.data.PlayerData;
import tk.yallandev.saintmc.common.backend.data.ReportData;
import tk.yallandev.saintmc.common.backend.data.ServerData;
import tk.yallandev.saintmc.common.backend.data.StatusData;
import tk.yallandev.saintmc.common.backend.database.mongodb.MongoDatabase;
import tk.yallandev.saintmc.common.backend.database.redis.RedisDatabase;
import tk.yallandev.saintmc.common.backend.database.redis.RedisDatabase.PubSubListener;
import tk.yallandev.saintmc.common.command.CommandLoader;
import tk.yallandev.saintmc.common.data.PlayerDataImpl;
import tk.yallandev.saintmc.common.data.ReportDataImpl;
import tk.yallandev.saintmc.common.data.ServerDataImpl;
import tk.yallandev.saintmc.common.data.StatusDataImpl;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.report.Report;
import tk.yallandev.saintmc.common.server.ServerManager;
import tk.yallandev.saintmc.common.utils.ClassGetter;

@Getter
@SuppressWarnings("deprecation")
public class BukkitMain extends JavaPlugin {

	@Getter
	private static BukkitMain instance;

	private CommonGeneral general;
	private ProtocolManager procotolManager;
	private SkinController skinManager;

	private PermissionManager permissionManager;
	private ServerManager serverManager;

	private PubSubListener pubSubListener;
	private Map<String, Location> location = new HashMap<>();

	@Setter
	private boolean tagControl = true;
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

		try {

			MongoDatabase mongo = new MongoDatabase(CommonConst.MONGO_URL);
			RedisDatabase redis = new RedisDatabase("127.0.0.1", "", 6379);

			mongo.connect();
			redis.connect();

			PlayerData playerData = new PlayerDataImpl(mongo, redis);
			ServerData serverData = new ServerDataImpl(mongo, redis);
			ReportData reportData = new ReportDataImpl(mongo, redis);
			StatusData statusData = new StatusDataImpl(mongo);

			general.setPlayerData(playerData);
			general.setServerData(serverData);
			general.setReportData(reportData);
			general.setStatusData(statusData);

			getServer().getScheduler().runTaskAsynchronously(getInstance(), pubSubListener = new PubSubListener(redis,
					new BukkitPubSubHandler(), "account-field", "report-field", "report-action", "server-info"));

		} catch (Exception ex) {
			ex.printStackTrace();
			Bukkit.shutdown();
			return;
		}

		/*
		 * Server Info
		 */

		general.setServerAddress(
				(Bukkit.getIp().equals("127.0.0.1") ? "186.221.185.74" : "127.0.0.1") + ":" + Bukkit.getPort());
		general.setServerId(general.getServerData().getServerId(general.getServerAddress()));
		general.setServerType(general.getServerData().getServerType(general.getServerAddress()));

		general.debug("The server has been loaded " + general.getServerAddress() + " (" + general.getServerId() + " - "
				+ general.getServerType().toString() + ")");

		general.getServerData().startServer(Bukkit.getMaxPlayers());

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
		serverManager = new ServerManager();
		(permissionManager = new PermissionManager(this)).onEnable();
		ProtocolGetter.foundDependencies();

		/*
		 * BungeeCord Message Listener
		 */

		this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new PluginMessageListener() {

			@Override
			public void onPluginMessageReceived(String channel, Player player, byte[] message) {
				if (!channel.equals("BungeeCord"))
					return;
				ByteArrayDataInput in = ByteStreams.newDataInput(message);
				String subchannel = in.readUTF();
				if (subchannel.equalsIgnoreCase("BungeeTeleport")) {
					String uuidStr = in.readUTF();

					if (!Member.hasGroupPermission(player.getUniqueId(), Group.YOUTUBERPLUS)) {
						player.sendMessage("§c§l> §fVocê não tem §cpermissão§f para teletransportar!");
						return;
					}

					AdminMode.getInstance().setAdmin(player, Member.getMember(player.getUniqueId()));
					UUID uuid = UUID.fromString(uuidStr);
					Player p = BukkitMain.getInstance().getServer().getPlayer(uuid);
					player.chat("/tp " + p.getName());
				}
			}

		});

		/*
		 * Register Listener
		 */

		registerListener();

		/*
		 * Initializing Command
		 */

		getServer().getScheduler().runTaskLater(this, () -> {

			unregisterCommands("icanhasbukkit", "ver", "version", "?", "about", "help", "ban", "ban-ip", "banlist",
					"clear", "deop", "stop", "op", "difficulty", "effect", "enchant", "give", "kick", "kill", "list",
					"me", "say", "scoreboard", "seed", "spawnpoint", "spreadplayers", "summon", "tell", "tellraw",
					"testfor", "testforblocks", "tp", "weather", "xp", "reload", "rl", "worldborder", "achievement",
					"blockdata", "clone", "debug", "defaultgamemode", "entitydata", "execute", "fill", "gamemode",
					"pardon", "pardon-ip", "particle", "replaceitem", "setidletimeout", "stats", "testforblock",
					"title", "trigger", "viaversion", "viaver", "vvbukkit", "protocolsupport", "ps", "holograms", "hd",
					"holo", "hologram", "restart", "protocol", "stop", "filter", "packet_filter", "packetlog", "pl",
					"plugins", "timings");

			try {
				new CommandLoader(new BukkitCommandFramework(this)).loadCommandsFromPackage(getFile(),
						"tk.yallandev.saintmc.bukkit.command.register");
			} catch (Exception e) {
				CommonGeneral.getInstance().getLogger().warning("Erro ao carregar o commandFramework!");
				e.printStackTrace();
			}

		}, 2L);

		getServer().getScheduler().runTaskTimer(this, new UpdateScheduler(), 1, 1);
	}

	@Override
	public void onDisable() {
		general.getServerData().stopServer();

		general.getServerData().closeConnection();
		general.getPlayerData().closeConnection();

		permissionManager.onDisable();
	}

	private void registerListener() {
		PluginManager pm = Bukkit.getPluginManager();

		for (Class<?> classes : ClassGetter.getClassesForPackage(getClass(), "tk.yallandev.saintmc.bukkit.listener")) {
			if (Listener.class.isAssignableFrom(classes)) {

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
		pm.registerEvents(new HologramListener(), getInstance());
		pm.registerEvents(new MenuListener(), getInstance());
		pm.registerEvents(new CooldownAPI(), getInstance());
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

	public void unregisterCommands(String... commands) {
		try {
			Field f1 = Bukkit.getServer().getClass().getDeclaredField("commandMap");
			f1.setAccessible(true);

			CommandMap commandMap = (CommandMap) f1.get(Bukkit.getServer());
			Field f2 = commandMap.getClass().getDeclaredField("knownCommands");

			f2.setAccessible(true);
			HashMap<String, Command> knownCommands = (HashMap<String, Command>) f2.get(commandMap);

			for (String cmdLabel : knownCommands.keySet()) {
				
				if (knownCommands.containsKey(cmdLabel)) {
					knownCommands.remove(cmdLabel);

					List<String> aliases = new ArrayList<>();

					for (String key : knownCommands.keySet()) {
						if (!key.contains(":"))
							continue;

						String substr = key.substring(key.indexOf(":") + 1);

						if (substr.equalsIgnoreCase(cmdLabel)) {
							aliases.add(key);
						}
					}

					for (String alias : aliases) {
						knownCommands.remove(alias);
					}
				}
			}

			for (Entry<String, Command> entry : knownCommands.entrySet()) {
				entry.getValue().unregister(commandMap);
				if (!entry.getKey().contains(":"))
					knownCommands.put(entry.getKey(), entry.getValue());
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
