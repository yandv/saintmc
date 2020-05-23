package br.com.saintmc.hungergames.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.saintmc.hungergames.GameMain;
import br.com.saintmc.hungergames.event.game.GameStartEvent;
import br.com.saintmc.hungergames.event.game.GameTimeEvent;
import br.com.saintmc.hungergames.game.GameState;
import tk.yallandev.saintmc.bukkit.api.scoreboard.Score;
import tk.yallandev.saintmc.bukkit.api.scoreboard.Scoreboard;
import tk.yallandev.saintmc.bukkit.api.scoreboard.impl.SimpleScoreboard;
import tk.yallandev.saintmc.bukkit.event.admin.PlayerAdminModeEvent;
import tk.yallandev.saintmc.common.utils.string.StringUtils;

public class ScoreboardListener extends GameListener {

	public static final Scoreboard SCOREBOARD;
	public static final Scoreboard GAME_SCOREBOARD;

	static {
		SCOREBOARD = new SimpleScoreboard("§b§lSINGLEKIT");

		SCOREBOARD.blankLine(12);
		SCOREBOARD.setScore(11, new Score("Iniciando em: §75:00", "time"));
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

		GAME_SCOREBOARD.blankLine(11);
		GAME_SCOREBOARD.setScore(10, new Score("Tempo: §72:00", "time"));
		GAME_SCOREBOARD.setScore(9, new Score("Jogadores: §70/80", "players"));
		GAME_SCOREBOARD.blankLine(8);
		GAME_SCOREBOARD.setScore(7, new Score("Kit: §7Nenhum", "kit1"));
		GAME_SCOREBOARD.setScore(6, new Score("Kit 2: §7Nenhum", "kit2"));
		GAME_SCOREBOARD.setScore(5, new Score("Kills: §70", "kills"));
		GAME_SCOREBOARD.blankLine(4);
		GAME_SCOREBOARD.setScore(3, new Score("Ranking: §7(§f-§7)", "ranking"));
		GAME_SCOREBOARD.blankLine(2);
		GAME_SCOREBOARD.setScore(1, new Score("§awww.saintmc.com.br", "site"));
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event) {
		createScoreboard(event.getPlayer());

		int players = getGameGeneral().getPlayersInGame();

		if (isPregame())
			SCOREBOARD.updateScore(
					new Score("Jogadores: §7" + players + "/" + Bukkit.getMaxPlayers(), "players"));
		else
			GAME_SCOREBOARD.updateScore(new Score("Jogadores: §7" + players, "players"));
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerAdminMode(PlayerAdminModeEvent event) {
		new BukkitRunnable() {
			
			@Override
			public void run() {
				int players = getGameGeneral().getPlayersInGame();

				if (isPregame())
					SCOREBOARD.updateScore(
							new Score("Jogadores: §7" + players + "/" + Bukkit.getMaxPlayers(), "players"));
				else
					GAME_SCOREBOARD.updateScore(new Score("Jogadores: §7" + players, "players"));				
			}
		}.runTaskLater(GameMain.getInstance(), 7l);
	}
	
	@EventHandler
	public void onGameStage(GameTimeEvent event) {
		String str = "Iniciando em: §7";

		switch (getGameGeneral().getGameState()) {
		case WINNING:
		case GAMETIME:
			str = "Tempo: §7";
			break;
		case INVINCIBILITY:
			str = "Invencivel por: §7";
			break;
		default:
			break;
		}

		(isPregame() ? SCOREBOARD : GAME_SCOREBOARD).updateScore(new Score(str + StringUtils.format(getGameGeneral().getTime()), "time"));
	}

	@EventHandler
	public void onGameStageChange(GameStartEvent event) {
		Bukkit.getOnlinePlayers().forEach(player -> GAME_SCOREBOARD.createScoreboard(player));
	}

	public void createScoreboard(Player player) {
		if (GameState.isPregame(getGameGeneral().getGameState()))
			SCOREBOARD.createScoreboard(player);
		else
			GAME_SCOREBOARD.createScoreboard(player);
	}
}
