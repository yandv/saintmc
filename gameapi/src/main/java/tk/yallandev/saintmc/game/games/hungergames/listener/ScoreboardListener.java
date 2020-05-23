package tk.yallandev.saintmc.game.games.hungergames.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import tk.yallandev.saintmc.bukkit.api.scoreboard.Score;
import tk.yallandev.saintmc.bukkit.api.scoreboard.Scoreboard;
import tk.yallandev.saintmc.bukkit.api.scoreboard.impl.SimpleScoreboard;
import tk.yallandev.saintmc.bukkit.event.admin.PlayerAdminModeEvent;
import tk.yallandev.saintmc.common.utils.string.StringUtils;
import tk.yallandev.saintmc.game.GameMain;
import tk.yallandev.saintmc.game.constructor.Gamer;
import tk.yallandev.saintmc.game.event.game.GameStageChangeEvent;
import tk.yallandev.saintmc.game.event.game.GameTimerEvent;
import tk.yallandev.saintmc.game.event.player.PlayerSelectedKitEvent;
import tk.yallandev.saintmc.game.games.hungergames.HungerGamesMode;
import tk.yallandev.saintmc.game.stage.GameStage;

public class ScoreboardListener extends tk.yallandev.saintmc.game.listener.GameListener {

	public static final Scoreboard SCOREBOARD;
	public static final Scoreboard GAME_SCOREBOARD;

	static {
		SCOREBOARD = new SimpleScoreboard("§b§lSINGLEKIT");

		SCOREBOARD.blankLine(12);
		SCOREBOARD.setScore(11, new Score("Iniciando em: §7", "time"));
		SCOREBOARD.setScore(10, new Score("Jogadores: §70/80", "players"));
		SCOREBOARD.blankLine(9);
		SCOREBOARD.setScore(8, new Score("Kit: §7Nenhum", "kit1"));
		SCOREBOARD.setScore(7, new Score("Kit 2: §7Nenhum", "kit2"));
		SCOREBOARD.blankLine(5);
		SCOREBOARD.setScore(4, new Score("Ranking: §7(§f-§7)", "ranking"));
		SCOREBOARD.setScore(3, new Score("Sala: §a#1", "room"));
		SCOREBOARD.blankLine(2);
		SCOREBOARD.setScore(1, new Score("§awww.saintmc.com.br", "site"));

		GAME_SCOREBOARD = new SimpleScoreboard("§b§lSINGLEKIT");

		GAME_SCOREBOARD.blankLine(12);
		GAME_SCOREBOARD.setScore(11, new Score("Tempo: §7", "time"));
		GAME_SCOREBOARD.setScore(10, new Score("Jogadores: §70/80", "players"));
		GAME_SCOREBOARD.blankLine(9);
		GAME_SCOREBOARD.setScore(8, new Score("Kit: §7Nenhum", "kit1"));
		GAME_SCOREBOARD.setScore(7, new Score("Kit 2: §7Nenhum", "kit2"));
		GAME_SCOREBOARD.setScore(6, new Score("Kills: §70", "kills"));
		GAME_SCOREBOARD.blankLine(5);
		GAME_SCOREBOARD.setScore(4, new Score("Ranking: §7(§f-§7)", "ranking"));
		GAME_SCOREBOARD.setScore(3, new Score("Sala: §a#1", "room"));
		GAME_SCOREBOARD.blankLine(2);
		GAME_SCOREBOARD.setScore(1, new Score("§awww.saintmc.com.br", "site"));
	}

	private HungerGamesMode mode;

	public ScoreboardListener(GameMain main, HungerGamesMode mode) {
		super(main);
		this.mode = mode;
	}

	@EventHandler
	public void onPlayerSelectedKit(PlayerSelectedKitEvent event) {
		(GameStage.isPregame(getGameMain().getGameStage()) ? SCOREBOARD : GAME_SCOREBOARD).updateScore(event.getPlayer(), new Score(
				"Kit" + (event.getKitIndex() == 1 ? "" : " " + event.getKitIndex()) + ": §7" + event.getKit().getName(),
				"kit" + (event.getKitIndex() == 1 ? "" : event.getKitIndex())));
		event.getPlayer().sendMessage("Salve!");
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onJoin(PlayerJoinEvent event) {
		createScoreboard(event.getPlayer());

		int players = getGameMain().playersLeft();

		if (GameStage.isPregame(getGameMain().getGameStage()))
			SCOREBOARD.updateScore(
					new Score("Jogadores: §7" + players + "/" + getGameMain().getTotalPlayers(), "players"));
		else
			GAME_SCOREBOARD.updateScore(new Score("Jogadores: §7" + players, "players"));
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onQuit(PlayerQuitEvent event) {
		int players = getGameMain().playersLeft();

		if (GameStage.isPregame(getGameMain().getGameStage()))
			SCOREBOARD.updateScore(
					new Score("Jogadores: §7" + players + "/" + getGameMain().getTotalPlayers(), "players"));
		else
			GAME_SCOREBOARD.updateScore(new Score("Jogadores: §7" + players, "players"));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onAdminMode(PlayerAdminModeEvent event) {
		new BukkitRunnable() {

			@Override
			public void run() {
				int players = getGameMain().playersLeft();

				if (GameStage.isPregame(getGameMain().getGameStage()))
					SCOREBOARD.updateScore(
							new Score("Jogadores: §7" + players + "/" + getGameMain().getTotalPlayers(), "players"));
				else
					GAME_SCOREBOARD.updateScore(new Score("Jogadores: §7" + players, "players"));
			}
		}.runTaskLater(GameMain.getPlugin(), 5l);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDeath(PlayerDeathEvent event) {
		if (event.getEntity().getKiller() != null) {
			Gamer gamer = Gamer.getGamer(event.getEntity().getUniqueId());
			GAME_SCOREBOARD.updateScore(event.getEntity().getKiller(),
					new Score("Kills: §7" + gamer.getMatchkills(), "kills"));
		}
	}

	@EventHandler
	public void onGameStage(GameTimerEvent event) {
		String str = "Iniciando em: §7";

		switch (mode.getGameMain().getGameStage()) {
		case FINAL:
		case GAMETIME:
		case WINNER:
			str = "Tempo: §7";
			break;
		case INVINCIBILITY:
			str = "Termina em: §7";
			break;
		default:
			break;
		}

		SCOREBOARD.updateScore(new Score(str + StringUtils.format(mode.getGameMain().getTimer()), "time"));
	}

	@EventHandler
	public void onGameStageChange(GameStageChangeEvent event) {
		if (event.getNewStage() == GameStage.GAMETIME)
			Bukkit.getOnlinePlayers().forEach(player -> GAME_SCOREBOARD.createScoreboard(player));
		else
			Bukkit.getOnlinePlayers().forEach(player -> SCOREBOARD.createScoreboard(player));
	}

	public void createScoreboard(Player player) {
		if (GameStage.isPregame(getStage()))
			SCOREBOARD.createScoreboard(player);
		else
			GAME_SCOREBOARD.createScoreboard(player);
	}

}
