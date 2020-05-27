package tk.yallandev.saintmc.game.games.hungergames;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.api.vanish.VanishAPI;
import tk.yallandev.saintmc.bukkit.command.BukkitCommandFramework;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.command.CommandLoader;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.server.loadbalancer.server.MinigameState;
import tk.yallandev.saintmc.game.GameMain;
import tk.yallandev.saintmc.game.GameMode;
import tk.yallandev.saintmc.game.GameType;
import tk.yallandev.saintmc.game.constructor.Ability;
import tk.yallandev.saintmc.game.constructor.Gamer;
import tk.yallandev.saintmc.game.constructor.Kit;
import tk.yallandev.saintmc.game.constructor.ScheduleArgs;
import tk.yallandev.saintmc.game.gameevents.GameEventType;
import tk.yallandev.saintmc.game.games.hungergames.listener.BlockListener;
import tk.yallandev.saintmc.game.games.hungergames.listener.BorderListener;
import tk.yallandev.saintmc.game.games.hungergames.listener.CombatLogListener;
import tk.yallandev.saintmc.game.games.hungergames.listener.DeathListener;
import tk.yallandev.saintmc.game.games.hungergames.listener.GameListener;
import tk.yallandev.saintmc.game.games.hungergames.listener.GameStageChangeListener;
import tk.yallandev.saintmc.game.games.hungergames.listener.InventoryListener;
import tk.yallandev.saintmc.game.games.hungergames.listener.InvincibilityListener;
import tk.yallandev.saintmc.game.games.hungergames.listener.PregameListener;
import tk.yallandev.saintmc.game.games.hungergames.listener.ScoreboardListener;
import tk.yallandev.saintmc.game.games.hungergames.listener.SelectKitListener;
import tk.yallandev.saintmc.game.games.hungergames.listener.SpectatorListener;
import tk.yallandev.saintmc.game.games.hungergames.listener.UpdateListener;
import tk.yallandev.saintmc.game.games.hungergames.manager.ColiseumManager;
import tk.yallandev.saintmc.game.games.hungergames.schedule.InvincibilityScheduler;
import tk.yallandev.saintmc.game.stage.GameStage;

public class HungerGamesMode extends GameMode {

	public static int MINIMUM_PLAYERS = 1;
	public static int INVINCIBILITY_TIME = 120;
	public static int FEAST_SPAWN = (17 * 60) + 30;
	public static int BONUSFEAST_SPAWN = 30 * 60;
	public static int FINALBATTLE_TIME = 45 * 60;
	public static boolean FINISHED;

	public static final HashMap<Group, List<String>> KITROTATE;

	static {
		KITROTATE = new HashMap<>();
		KITROTATE.put(Group.MEMBRO, Arrays.asList("surprise", "snail", "thor", "timelord", "reaper", "worm"));
		KITROTATE.put(Group.LIGHT, Arrays.asList("kangaroo", "boxer", "ninja", "stomper", "endermage", "cannibal"));
		KITROTATE.put(Group.BLIZZARD, Arrays.asList("viper", "grappler", "viking", "hotpotato", "", ""));
	}

	public HungerGamesMode(GameMain main) {
		super(main, GameType.HUNGERGAMES);

	}

	@Override
	public void onLoad() {
		MapUtils.deleteWorld("world");
		getGameMain().setGameStage(GameStage.WAITING);
		getGameMain().setMinimumPlayers(MINIMUM_PLAYERS);
	}

	@Override
	public void onEnable() {
		getGameMain().getGameEventManager().newEvent(GameEventType.SERVER_START);

		new CommandLoader(new BukkitCommandFramework(getGameMain()))
				.loadCommandsFromPackage("tk.yallandev.saintmc.game.hungergames.command");
		loadListeners();

		getGameMain().getServer().getScheduler().scheduleSyncDelayedTask(getGameMain(), new Runnable() {
			public void run() {
				World world = getServer().getWorld("world");
				world.setSpawnLocation(0, getServer().getWorlds().get(0).getHighestBlockYAt(0, 0), 0);

				for (int x = -30; x <= 30; x++)
					for (int z = -30; z <= 30; z++)
						world.getSpawnLocation().clone().add(x * 16, 0, z * 16).getChunk().load();

				world.setDifficulty(Difficulty.NORMAL);

				if (world.hasStorm())
					world.setStorm(false);

				world.setWeatherDuration(999999999);
				world.setGameRuleValue("doDaylightCycle", "false");
				org.bukkit.WorldBorder border = world.getWorldBorder();
				border.setCenter(0, 0);
				border.setSize(1000);

				for (Entity e : world.getEntities()) {
					e.remove();
				}
			}
		});

		new BukkitRunnable() {
			@Override
			public void run() {
				CommonGeneral.getInstance().getServerData().updateStatus(MinigameState.WAITING,
						GameMain.getPlugin().getTimer());
			}
		}.runTaskLaterAsynchronously(getGameMain(), 1);
	}

	@Override
	public void onDisable() {

	}

	private void loadListeners() {
		getGameMain().getServer().getPluginManager().registerEvents(new GameStageChangeListener(getGameMain()),
				getGameMain());
		getGameMain().getServer().getPluginManager().registerEvents(new PregameListener(getGameMain()), getGameMain());
		getGameMain().getServer().getPluginManager().registerEvents(new InventoryListener(getGameMain()),
				getGameMain());
		getGameMain().getServer().getPluginManager().registerEvents(new BorderListener(getGameMain()), getGameMain());
		getGameMain().getServer().getPluginManager().registerEvents(new ScoreboardListener(getGameMain(), this),
				getGameMain());
		getGameMain().getServer().getPluginManager().registerEvents(new SelectKitListener(), getGameMain());
		getGameMain().getServer().getPluginManager().registerEvents(new UpdateListener(getGameMain()), getGameMain());
		getGameMain().getServer().getPluginManager().registerEvents(new SpectatorListener(getGameMain()),
				getGameMain());
	}

	@Override
	public void startGame() {
		Bukkit.broadcastMessage("§a§l> §fO torneio iniciou!");
		getGameMain().getServer().getPluginManager().registerEvents(new GameListener(getGameMain()), getGameMain());
		getGameMain().getServer().getPluginManager().registerEvents(new CombatLogListener(), getGameMain());
		getGameMain().getServer().getPluginManager().registerEvents(new DeathListener(getGameMain(), this),
				getGameMain());
		getGameMain().getServer().getPluginManager().registerEvents(new BlockListener(getGameMain()), getGameMain());

		InvincibilityScheduler scheduler = new InvincibilityScheduler();

		scheduler.pulse(
				new ScheduleArgs(getGameMain().getGameType(), getGameMain().getGameStage(), getGameMain().getTimer()));

		getGameMain().getSchedulerManager().addScheduler("invincibility", scheduler);
		getGameMain().setTimer(INVINCIBILITY_TIME);
		getGameMain().setGameStage(GameStage.INVINCIBILITY);
		getGameMain().getServer().getPluginManager().registerEvents(new InvincibilityListener(getGameMain()),
				getGameMain());

		for (Player p : getGameMain().getServer().getOnlinePlayers()) {
			Gamer gamer = getGameMain().getGamerManager().getGamer(p.getUniqueId());

			if (gamer.isGamemaker())
				continue;

			if (gamer.isSpectator())
				continue;

			if (!ColiseumManager.isInsideColiseum(p))
				teleportToSpawn(p);

			if (gamer.isInvisible()) {
				VanishAPI.getInstance().removeVanish(p);

				for (Player online : Bukkit.getOnlinePlayers())
					if (!online.canSee(p))
						online.showPlayer(p);
			}

			p.playSound(p.getLocation(), Sound.AMBIENCE_THUNDER, 1f, 1f);
			p.playSound(p.getLocation(), Sound.ENDERDRAGON_GROWL, 1f, 1f);
			p.closeInventory();
			p.getInventory().clear();
			p.setGameMode(org.bukkit.GameMode.SURVIVAL);
			p.setAllowFlight(false);

			for (Kit playerKit : getGameMain().getKitManager().getPlayerKit(p)) {
				for (Ability ability : playerKit.getAbilities()) {
					ability.giveItems(p);
				}
//				if (playerKit != null)
//				else
//					if (Member.hasGroupPermission(p.getUniqueId(), Group.LIGHT))
//						gamer.setNoKit(true);
			}

			p.getInventory().addItem(new ItemStack(Material.COMPASS));
//TODO			Member.getMember(p.getUniqueId()).removePermission("tag.winner");
		}
	}

	public void checkWinner() {
		if (getGameMain().playersLeft() > 1) {
			return;
		}

		Player pWin = null;
		for (Player p : Bukkit.getOnlinePlayers()) {
			Gamer gamer = Gamer.getGamer(p);

			if (gamer.isGamemaker())
				continue;

			if (gamer.isSpectator())
				continue;

			if (!p.isOnline())
				continue;

			pWin = p;
			break;
		}

		if (pWin == null) {
			Bukkit.broadcastMessage("�%no-winner-kick%�");
			Bukkit.shutdown();
			return;
		}

		getGameMain().setGameStage(GameStage.WINNER);
		FINISHED = true;
		final Player winner = pWin;
		Gamer win = Gamer.getGamer(winner);

		win.addWin();

//		for (Player player : getGameMain().getServer().getOnlinePlayers()) {
//			player.sendMessage(T.t(BukkitMain.getInstance(),BattlePlayer.getLanguage(player.getUniqueId()), "player-winner").replace("%player%", winner.getName()).replace("%kills%", win.getMatchkills() + "").replace("%kit%", (win.getKit() != null ? ((win.getKit() instanceof CustomKit) ? ChatColor.DARK_GRAY : "") : "") + NameUtils.formatString(win.getKitName())).replace("%time%", StringTimeUtils.format(getGameMain().getTimer())));
//		}

		Member battlePlayer = CommonGeneral.getInstance().getMemberManager().getMember(win.getUniqueId());
		battlePlayer.addPermission("tag.winner");
		battlePlayer.addMoney(new Random().nextInt(50));

		int xp = new Random().nextInt(win.getMatchkills() == 0 ? 10 : 15 * win.getMatchkills());

		battlePlayer.addXp(xp);
//		winner.sendMessage(T.t(BukkitMain.getInstance(), battlePlayer.getLanguage(), "earned-xp").replace("%amount%", "" + xp));

		new BukkitRunnable() {

			@Override
			public void run() {
//				winner.sendMessage(T.t(BukkitMain.getInstance(),BattlePlayer.getLanguage(winner.getUniqueId()), "player-winner-kick").replace("%player%", winner.getName()).replace("%kills%", win.getMatchkills() + ""));
				GameMain.getPlugin().sendPlayerToLobby(winner);

				for (Player player : getGameMain().getServer().getOnlinePlayers()) {
//					player.sendMessage(T.t(BukkitMain.getInstance(),BattlePlayer.getLanguage(player.getUniqueId()), "other-winner-kick").replace("%player%", winner.getName()).replace("%kills%", win.getMatchkills() + ""));
					GameMain.getPlugin().sendPlayerToLobby(player);
				}

				new BukkitRunnable() {

					@Override
					public void run() {
						Bukkit.shutdown();
					}
				}.runTaskLater(getGameMain(), 20 * 2);
			}
		}.runTaskLater(getGameMain(), 20 * 15);
	}

	public static void teleportToSpawn(Player player) {
		player.teleport(getSpawnLocation());
	}

	public static Location getSpawnLocation() {
		Random r = new Random();
		int x = r.nextInt(30);
		int z = r.nextInt(30);

		if (r.nextBoolean())
			x = -x;

		if (r.nextBoolean())
			z = -z;

		World world = Bukkit.getWorlds().get(0);
		int y = world.getHighestBlockYAt(x, z);
		Location loc = new Location(world, x, y + 1, z);

		if (!loc.getChunk().isLoaded()) {
			loc.getChunk().load();
		}

		return loc;
	}

}
