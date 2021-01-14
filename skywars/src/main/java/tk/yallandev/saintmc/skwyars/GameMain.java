package tk.yallandev.saintmc.skwyars;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import lombok.Getter;
import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.command.BukkitCommandFramework;
import tk.yallandev.saintmc.common.server.ServerType;
import tk.yallandev.saintmc.common.server.loadbalancer.server.MinigameState;
import tk.yallandev.saintmc.skwyars.command.ModeratorCommand;
import tk.yallandev.saintmc.skwyars.game.ModeType;
import tk.yallandev.saintmc.skwyars.game.SkywarsType;
import tk.yallandev.saintmc.skwyars.listener.GamerListener;
import tk.yallandev.saintmc.skwyars.listener.LuckyListener;
import tk.yallandev.saintmc.skwyars.listener.MessageListener;
import tk.yallandev.saintmc.skwyars.listener.PlayerListener;
import tk.yallandev.saintmc.skwyars.listener.ScoreboardListener;
import tk.yallandev.saintmc.skwyars.listener.StatusListener;
import tk.yallandev.saintmc.skwyars.listener.UpdateListener;
import tk.yallandev.saintmc.skwyars.listener.WorldListener;
import tk.yallandev.saintmc.skwyars.scheduler.SchedulerListener;
import tk.yallandev.saintmc.update.UpdatePlugin;

@Getter
public class GameMain extends JavaPlugin {

	@Getter
	private static GameMain instance;

	private GameGeneral gameGeneral;

	private SkywarsType skywarsType = SkywarsType.SOLO;
	private ModeType modeType = ModeType.INSANE;

	private boolean luckyWars = true;

	private String mapName;
	private int y;
	private int maxY;
	private int maxDistance;

	private Map<String, Location> location = new HashMap<>();

	@Override
	public void onLoad() {
		instance = this;

		gameGeneral = new GameGeneral();
		gameGeneral.onLoad();
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

		if (UpdatePlugin.update(new File(GameMain.class.getProtectionDomain().getCodeSource().getLocation().getPath()),
				"Skywars", CommonConst.DOWNLOAD_KEY, shutdown))
			return;

		WorldCreator worldCreator = new WorldCreator("lobby");

		worldCreator.type(WorldType.FLAT);
		worldCreator.generatorSettings("0;0");
		worldCreator.generateStructures(false);

		getServer().createWorld(worldCreator);

		for (World world : Bukkit.getWorlds()) {
			world.setAutoSave(false);
			world.setThundering(false);
			world.setStorm(false);
			world.setWeatherDuration(1000000000);
			world.setTime(6000);
			world.setGameRuleValue("doDaylightCycle", "false");
			world.setGameRuleValue("doMobSpawning", "false");
		}

		saveDefaultConfig();
		mapName = getConfig().getString("mapName", "-/-");
		y = getConfig().getInt("y", 90);
		maxY = getConfig().getInt("maxY", 130);
		maxDistance = getConfig().getInt("maxDistance", 130);

		WorldBorder worldBorder = Bukkit.getWorlds().stream().findFirst().orElse(null).getWorldBorder();

		worldBorder.setCenter(0, 0);
		worldBorder.setSize(400);

		if (CommonGeneral.getInstance().getServerType() == ServerType.SW_TEAM)
			skywarsType = SkywarsType.TEAM;
		else if (CommonGeneral.getInstance().getServerType() == ServerType.SW_SQUAD)
			skywarsType = SkywarsType.SQUAD;

		if (CommonGeneral.getInstance().getServerType().name().startsWith("SK"))
			luckyWars = true;

		getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

		gameGeneral.onEnable();
		loadListener();
		BukkitCommandFramework.INSTANCE.registerCommands(new ModeratorCommand());

		new BukkitRunnable() {
			@Override
			public void run() {
				CommonGeneral.getInstance().getServerData().updateStatus(MinigameState.WAITING, mapName, 60);
			}
		}.runTaskAsynchronously(getInstance());
		super.onEnable();
	}

	@Override
	public void onDisable() {
		gameGeneral.onDisable();
		super.onDisable();
	}

	private void loadListener() {
		Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
		Bukkit.getPluginManager().registerEvents(new ScoreboardListener(), this);
		Bukkit.getPluginManager().registerEvents(new SchedulerListener(), this);
		Bukkit.getPluginManager().registerEvents(new StatusListener(), this);
		Bukkit.getPluginManager().registerEvents(new GamerListener(), this);
		Bukkit.getPluginManager().registerEvents(new UpdateListener(), this);
		Bukkit.getPluginManager().registerEvents(new MessageListener(), this);
		Bukkit.getPluginManager().registerEvents(new WorldListener(), this);

		if (isLuckyWars())
			Bukkit.getPluginManager().registerEvents(new LuckyListener(), this);
	}

	public int getMaxPlayers() {
		return skywarsType.getMaxPlayers();
	}

	public static GameMain getPlugin() {
		return instance;
	}

	public void sendPlayAgain(Player player) {
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);

		try {
			out.writeUTF(skywarsType == SkywarsType.SOLO ? "SWSolo" : "SWTeam");
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}

		player.sendPluginMessage(getInstance(), "BungeeCord", b.toByteArray());
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
			getLogger().info("The world " + world.getName() + " has loaded successfully.");
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
//			LocationChangeEvent event = new LocationChangeEvent(config, this.location.get(config), location);
//			Bukkit.getPluginManager().callEvent(event);
//
//			if (event.isCancelled())
//				return;
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

}
