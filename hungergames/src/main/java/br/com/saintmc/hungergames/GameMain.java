package br.com.saintmc.hungergames;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.saintmc.hungergames.constructor.Gamer;
import br.com.saintmc.hungergames.event.scoreboard.ScoreboardTitleChangeEvent;
import br.com.saintmc.hungergames.game.Game;
import br.com.saintmc.hungergames.listener.register.BorderListener;
import br.com.saintmc.hungergames.listener.register.GamerListener;
import br.com.saintmc.hungergames.listener.register.KitListener;
import br.com.saintmc.hungergames.listener.register.RestoreListener;
import br.com.saintmc.hungergames.listener.register.ScoreboardListener;
import br.com.saintmc.hungergames.listener.register.UpdateListener;
import br.com.saintmc.hungergames.scheduler.SchedulerListener;
import br.com.saintmc.hungergames.utils.ServerConfig;
import lombok.Getter;
import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.bukkit.BukkitMember;
import tk.yallandev.saintmc.bukkit.command.BukkitCommandFramework;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.server.ServerType;
import tk.yallandev.saintmc.common.server.loadbalancer.server.MinigameState;
import tk.yallandev.saintmc.common.tag.Tag;
import tk.yallandev.saintmc.common.tag.TagWrapper;
import tk.yallandev.saintmc.common.utils.string.StringUtils;
import tk.yallandev.saintmc.update.UpdatePlugin;

@Getter
public class GameMain extends JavaPlugin {

	public static boolean DOUBLEKIT = false;
	public static final Game GAME = new Game(0, 90);

	public static final Map<Group, List<String>> KITROTATE;

	public static final Tag WINNER = TagWrapper.create("WINNER", "§2§lWINNER§2", null, 24).setCustom(true);
	public static final Tag CHAMPION = TagWrapper.create("CHAMPION", "§6§lCHAMPION§6", null, 24).setCustom(true);

	static {
		KITROTATE = new HashMap<>();
		KITROTATE.put(Group.MEMBRO, Arrays.asList("surprise", "lumberjack", "miner", "lumberjack", "reaper", "magma",
				"kaya", "endermage", "worm"));
		KITROTATE.put(Group.PRO, Arrays.asList("snail", "thor", "anchor", "ninja", "stomper", "grappler", "kangaroo",
				"boxer", "ironman", "gladiator", "endermage", "ultimato"));
		KITROTATE.put(Group.PRO, Arrays.asList("turtle", "viper", "viking", "tank", "specialist", "poseidon"));
	}

	@Getter
	private static GameMain instance;

	private GameGeneral general;
	private String roomId;

	@Override
	public void onLoad() {
		UpdatePlugin.Shutdown shutdown = new UpdatePlugin.Shutdown() {

			@Override
			public void stop() {
				Bukkit.shutdown();
			}

		};

		if (UpdatePlugin.update(new File(GameMain.class.getProtectionDomain().getCodeSource().getLocation().getPath()),
				"HungerGames", CommonConst.DOWNLOAD_KEY, shutdown))
			return;

		instance = this;

		general = new GameGeneral();
		general.onLoad();

		super.onLoad();
	}

	@Override
	public void onEnable() {
		Listener listener = new Listener() {

			@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
			public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
				if (event.getLoginResult() != Result.ALLOWED)
					return;

				if (!ServerConfig.getInstance().isJoinEnabled())
					event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
							GameGeneral.getInstance().getGameState().isPregame() ? "§cO servidor está carregando!"
									: "§cO servidor não está permitindo que jogadores entre no momento!");
			}

		};

		Bukkit.getPluginManager().registerEvents(listener, getInstance());
		BukkitCommandFramework.INSTANCE.loadCommands(this.getClass(), "br.com.saintmc.hungergames.command");
		BukkitMain.getInstance().setRemovePlayerDat(false);

		if (CommonGeneral.getInstance().getServerType() == ServerType.EVENTO) {
			TagWrapper.registerTag(CHAMPION);
			BukkitMain.getInstance().getServerConfig().setWhitelist(true);
		} else
			TagWrapper.registerTag(WINNER);

		HandlerList.unregisterAll(BukkitMain.getInstance().getHologramController().getListener());

		if (roomId == null) {
			String[] split = CommonGeneral.getInstance().getServerId().split("\\.");

			if (split.length > 1) {
				roomId = split[0].toUpperCase();
			} else {
				roomId = CommonGeneral.getInstance().getServerId();
			}

			ServerConfig.getInstance()
					.setTitle(CommonGeneral.getInstance().getServerType() == ServerType.EVENTO ? "§6§lEVENTO"
							: "§6§lHG-" + roomId.toUpperCase());
			Bukkit.getPluginManager().callEvent(new ScoreboardTitleChangeEvent(ServerConfig.getInstance().getTitle()));
		}

		if (roomId.startsWith("A"))
			DOUBLEKIT = true;
		else
			DOUBLEKIT = false;

		loadListener();
		saveResource("cake.png", true);
		general.onEnable();

		getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		getServer().getScheduler().scheduleSyncDelayedTask(getInstance(), new Runnable() {
			public void run() {
				CommonGeneral.getInstance().debug("[World] Initializing the world configuration!");
				World world = getServer().getWorld("world");
				world.setSpawnLocation(0, getServer().getWorlds().get(0).getHighestBlockYAt(0, 0) + 5, 0);

				world.setAutoSave(false);
				((CraftWorld) world).getHandle().savingDisabled = true;

				CommonGeneral.getInstance().debug("[World] Loading the chunks!");

				long pid = getPID();
				long time = System.currentTimeMillis();

				try {
					for (int x = 0; x <= 32; x++) {
						for (int z = 0; z <= 32; z++) {
							world.getSpawnLocation().clone().add(x * 16, 0, z * 16).getChunk().load();
							world.getSpawnLocation().clone().add(x * -16, 0, z * -16).getChunk().load();
							world.getSpawnLocation().clone().add(x * 16, 0, z * -16).getChunk().load();
							world.getSpawnLocation().clone().add(x * -16, 0, z * 16).getChunk().load();
						}

						if (x % 2 == 0)
							CommonGeneral.getInstance()
									.debug("[World] "
											+ StringUtils.formatTime((int) ((System.currentTimeMillis() - time) / 1000))
											+ " have passed! PID: " + pid + " - used mem: "
											+ ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())
													/ 2L / 1048576L));
					}
				} catch (OutOfMemoryError ex) {

				}

				CommonGeneral.getInstance().debug("[World] All chunks has been loaded!");

				world.setDifficulty(Difficulty.NORMAL);

				if (world.hasStorm())
					world.setStorm(false);

				world.setTime(0l);
				world.setWeatherDuration(999999999);
				world.setGameRuleValue("doDaylightCycle", "false");
				world.setGameRuleValue("announceAdvancements", "false");
				org.bukkit.WorldBorder border = world.getWorldBorder();
				border.setCenter(0, 0);
				border.setSize(1000);

				CommonGeneral.getInstance().debug("[World] World has been loaded!");

				for (Entity e : world.getEntities())
					e.remove();

				CommonGeneral.getInstance().getServerData().updateStatus(MinigameState.WAITING, 300);
				ServerConfig.getInstance().setJoinEnabled(true);
				HandlerList.unregisterAll(listener);
			}
		});

		super.onEnable();
	}

	@Override
	public void onDisable() {
		general.onDisable();
		super.onDisable();
	}

	public void loadListener() {
		Bukkit.getPluginManager().registerEvents(new UpdateListener(), this);
		Bukkit.getPluginManager().registerEvents(new BorderListener(), this);
		Bukkit.getPluginManager().registerEvents(new GamerListener(), this);
		Bukkit.getPluginManager().registerEvents(new ScoreboardListener(), this);
		Bukkit.getPluginManager().registerEvents(new KitListener(), this);
		Bukkit.getPluginManager().registerEvents(new RestoreListener(), this);
		Bukkit.getPluginManager().registerEvents(new SchedulerListener(getGeneral()), this);
	}

	public static long getPID() {
		String processName = java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
		return Long.parseLong(processName.split("@")[0]);
	}

	public void registerListener(Listener listener) {
		Bukkit.getPluginManager().registerEvents(listener, getInstance());
	}

	public void sendPlayerToHungerGames(Player p) {
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);

		try {
			out.writeUTF("Hungergames");
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}

		p.sendPluginMessage(this, "BungeeCord", b.toByteArray());
	}

	public static GameMain getPlugin() {
		return instance;
	}

	/**
	 * 
	 * Check if the player has won the last game and apply the tag
	 * 
	 * @param gamer
	 */

	public void checkWinner(Gamer gamer) {
		BukkitMember member = (BukkitMember) CommonGeneral.getInstance().getMemberManager()
				.getMember(gamer.getUniqueId());

		Tag verifyTag = CommonGeneral.getInstance().getServerType() == ServerType.EVENTO ? CHAMPION : WINNER;

		if (member.getTags().contains(verifyTag)) {
			gamer.setWinner(true);
			member.removePermission("tag." + verifyTag.getName().toLowerCase());

			member.sendMessage("§aVocê ganhou a tag " + verifyTag.getPrefix() + "§a "
					+ (verifyTag == CHAMPION ? "por ter ganhado o ultimo evento que participou"
							: "por ter ganhado a ultima partida")
					+ "!");
			member.sendMessage("§aVocê terá todos os kits nessa partida!");
			member.sendMessage("§cCaso você saia da partida, você perderá suas vantagens!");

			new BukkitRunnable() {

				@Override
				public void run() {
					member.setTag(verifyTag);
				}
			}.runTaskLater(GameMain.getInstance(), 20l);
		}
	}

}
