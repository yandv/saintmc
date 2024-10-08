package tk.yallandev.saintmc.skwyars.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.api.scoreboard.Score;
import tk.yallandev.saintmc.bukkit.api.scoreboard.Scoreboard;
import tk.yallandev.saintmc.bukkit.api.scoreboard.impl.SimpleScoreboard;
import tk.yallandev.saintmc.bukkit.bukkit.BukkitMember;
import tk.yallandev.saintmc.common.utils.string.StringUtils;
import tk.yallandev.saintmc.skwyars.GameGeneral;
import tk.yallandev.saintmc.skwyars.GameMain;
import tk.yallandev.saintmc.skwyars.event.game.GameStateChangeEvent;
import tk.yallandev.saintmc.skwyars.event.game.GameTimeEvent;
import tk.yallandev.saintmc.skwyars.game.EventType;

public class ScoreboardListener implements Listener {

	private static final Scoreboard SCOREBOARD;
	private static final Scoreboard GAME_SCOREBOARD;

	static {
		SCOREBOARD = new SimpleScoreboard("§6§lSKYWARS");

		SCOREBOARD.blankLine(8);
		SCOREBOARD.setScore(7, new Score("§fIniciando em: §71:00", "time"));
		SCOREBOARD.setScore(6, new Score("§fJogadores: §70/12", "players"));
		SCOREBOARD.blankLine(5);
		SCOREBOARD.setScore(4, new Score("§fMapa: §e" + GameMain.getInstance().getMapName(), "map"));
		SCOREBOARD.setScore(3,
				new Score("§fModo: " + (GameMain.getInstance().isLuckyWars() ? "§6Lucky" : "§7Normal"), "mode"));
		SCOREBOARD.blankLine(2);
		SCOREBOARD.setScore(1, new Score("§6" + CommonConst.SITE, "site"));

		GAME_SCOREBOARD = new SimpleScoreboard("§6§lSKYWARS");

		GAME_SCOREBOARD.blankLine(11);
		GAME_SCOREBOARD.setScore(10, new Score("§fPróximo evento:", "event"));
		GAME_SCOREBOARD.setScore(9, new Score("§aRefil 3:00", "next"));
		GAME_SCOREBOARD.blankLine(8);
		GAME_SCOREBOARD.setScore(7, new Score("§fRestantes: §70", "players"));
		GAME_SCOREBOARD.setScore(6, new Score("§fKills: §70", "kills"));
		GAME_SCOREBOARD.blankLine(5);
		GAME_SCOREBOARD.setScore(4, new Score("§fMapa: §e" + GameMain.getInstance().getMapName(), "map"));
		GAME_SCOREBOARD.setScore(3, new Score("§fModo: §cInsano", "mode"));
		GAME_SCOREBOARD.blankLine(2);
		GAME_SCOREBOARD.setScore(1, new Score("§6" + CommonConst.SITE, "site"));
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		SCOREBOARD.createScoreboard(event.getPlayer());

		SCOREBOARD.updateScore(new Score(
				"§fJogadores: §7" + (GameGeneral.getInstance().getGamerController().count(gamer -> gamer.isPlaying()))
						+ "/" + GameMain.getInstance().getMaxPlayers(),
				"players"));
		GAME_SCOREBOARD.updateScore(new Score(
				"§fRestantes: §7" + (GameGeneral.getInstance().getGamerController().count(gamer -> gamer.isPlaying())),
				"players"));
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		SCOREBOARD.createScoreboard(event.getPlayer());

		SCOREBOARD.updateScore(new Score("§fJogadores: §7"
				+ (GameGeneral.getInstance().getGamerController().count(gamer -> gamer.isPlaying()) - 1) + "/"
				+ GameMain.getInstance().getMaxPlayers(), "players"));
		GAME_SCOREBOARD.updateScore(new Score(
				"§fRestantes: §7"
						+ (GameGeneral.getInstance().getGamerController().count(gamer -> gamer.isPlaying()) - 1),
				"players"));
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerDeath(PlayerDeathEvent event) {
		GAME_SCOREBOARD.updateScore(new Score(
				"§fRestantes: §7" + (GameGeneral.getInstance().getGamerController().count(gamer -> gamer.isPlaying())),
				"players"));

		if (event.getEntity().getPlayer().getKiller() instanceof Player)
			if (GameGeneral.getInstance().getGamerController().containsKey(event.getEntity().getKiller().getUniqueId()))
				GAME_SCOREBOARD.updateScore(event.getEntity().getKiller(),
						new Score("§fKills: §7" + GameGeneral.getInstance().getGamerController()
								.getGamer(event.getEntity().getKiller()).getMatchKills(), "kills"));
	}

	@EventHandler
	public void onGameTime(GameTimeEvent event) {
		SCOREBOARD.updateScore(new Score("§fIniciando em: §7" + StringUtils.format(event.getTime()), "time"));

		if (GameMain.getInstance().getGameGeneral().getEventController().getEventType() == EventType.REFIL)
			GAME_SCOREBOARD.updateScore(new Score(
					"§aRefil " + StringUtils
							.format(GameMain.getInstance().getGameGeneral().getEventController().getEventTime()),
					"next"));
		else
			GAME_SCOREBOARD.updateScore(new Score(
					"§aDeathmatch " + StringUtils
							.format(GameMain.getInstance().getGameGeneral().getEventController().getEventTime()),
					"next"));
	}

	@EventHandler
	public void onGameState(GameStateChangeEvent event) {
		if (event.getToState().isGametime())
			for (Player player : Bukkit.getOnlinePlayers()) {
				SCOREBOARD.removeViewer(
						(BukkitMember) CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId()));

				GAME_SCOREBOARD.createScoreboard(player);
			}
	}

}
