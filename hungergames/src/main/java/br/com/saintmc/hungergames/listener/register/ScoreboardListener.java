package br.com.saintmc.hungergames.listener.register;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.GameMain;
import br.com.saintmc.hungergames.constructor.Gamer;
import br.com.saintmc.hungergames.event.game.GameStartEvent;
import br.com.saintmc.hungergames.event.game.GameTimeEvent;
import br.com.saintmc.hungergames.event.kit.PlayerSelectedKitEvent;
import br.com.saintmc.hungergames.event.scoreboard.ScoreboardTitleChangeEvent;
import br.com.saintmc.hungergames.kit.KitType;
import br.com.saintmc.hungergames.listener.GameListener;
import br.com.saintmc.hungergames.utils.ServerConfig;
import tk.yallandev.saintmc.bukkit.api.scoreboard.Score;
import tk.yallandev.saintmc.bukkit.api.scoreboard.Scoreboard;
import tk.yallandev.saintmc.bukkit.api.scoreboard.impl.SimpleScoreboard;
import tk.yallandev.saintmc.bukkit.event.admin.PlayerAdminModeEvent;
import tk.yallandev.saintmc.common.utils.string.NameUtils;
import tk.yallandev.saintmc.common.utils.string.StringUtils;

public class ScoreboardListener extends GameListener {

	public static final Scoreboard SCOREBOARD;

	static {
		if (GameMain.DOUBLEKIT) {
			SCOREBOARD = new SimpleScoreboard(ServerConfig.getInstance().getTitle());

			SCOREBOARD.blankLine(12);
			SCOREBOARD.setScore(11, new Score("Iniciando em: §75:00", "time"));
			SCOREBOARD.setScore(10, new Score("Jogadores: §70/80", "players"));
			SCOREBOARD.blankLine(9);
			SCOREBOARD.setScore(8, new Score("Kit 1: §7Nenhum", "kit1"));
			SCOREBOARD.setScore(7, new Score("Kit 2: §7Nenhum", "kit2"));
			SCOREBOARD.blankLine(5);
			SCOREBOARD.setScore(4, new Score("Ranking: §7(§f-§7)", "ranking"));
			SCOREBOARD.setScore(3, new Score("Sala: §a#1", "room"));
			SCOREBOARD.blankLine(2);
			SCOREBOARD.setScore(1, new Score("§awww.saintmc.com.br", "site"));
		} else {
			SCOREBOARD = new SimpleScoreboard(ServerConfig.getInstance().getTitle());

			SCOREBOARD.blankLine(10);
			SCOREBOARD.setScore(9, new Score("Iniciando em: §75:00", "time"));
			SCOREBOARD.setScore(8, new Score("Jogadores: §70/80", "players"));
			SCOREBOARD.blankLine(7);
			SCOREBOARD.setScore(6, new Score("Kit: §7Nenhum", "kit1"));
			SCOREBOARD.blankLine(5);
			SCOREBOARD.setScore(4, new Score("Ranking: §7(§f-§7)", "ranking"));
			SCOREBOARD.setScore(3, new Score("Sala: §a#1", "room"));
			SCOREBOARD.blankLine(2);
			SCOREBOARD.setScore(1, new Score("§awww.saintmc.com.br", "site"));
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event) {
		createScoreboard(event.getPlayer());

		int players = getGameGeneral().getPlayersInGame();

		if (isPregame())
			SCOREBOARD.updateScore(new Score("Jogadores: §7" + players + "/" + Bukkit.getMaxPlayers(), "players"));
		else
			SCOREBOARD.updateScore(new Score("Jogadores: §7" + players, "players"));
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player player = event.getEntity().getKiller();

		if (player != null) {
			Gamer gamer = GameGeneral.getInstance().getGamerController().getGamer(player);
			SCOREBOARD.updateScore(player, new Score("Kills: §7" + gamer.getMatchKills(), "kills"));
		}
	}

	@EventHandler
	public void onPlayerSelectedKit(PlayerSelectedKitEvent event) {
		if (event.getKit() == null)
			return;

		Player player = event.getPlayer();

		if (event.getKitType() == KitType.PRIMARY)
			SCOREBOARD.updateScore(player,
					new Score("Kit 1: §7" + NameUtils.formatString(event.getKit().getName()), "kit1"));
		else
			SCOREBOARD.updateScore(player,
					new Score("Kit 2: §7" + NameUtils.formatString(event.getKit().getName()), "kit2"));
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerAdminMode(PlayerAdminModeEvent event) {
		new BukkitRunnable() {

			@Override
			public void run() {
				int players = getGameGeneral().getPlayersInGame();

				SCOREBOARD.updateScore(new Score("Jogadores: §7" + players + "/" + Bukkit.getMaxPlayers(), "players"));
			}

		}.runTaskLater(GameMain.getInstance(), 7l);
	}

	@EventHandler
	public void onGameStage(GameTimeEvent event) {
		String str = "Iniciando em: §7";

		switch (getGameGeneral().getGameState()) {
		case WAITING: {
			str = "Aguardando: §7";
			break;
		}
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

		(isPregame() ? SCOREBOARD : SCOREBOARD)
				.updateScore(new Score(str + StringUtils.format(getGameGeneral().getTime()), "time"));
	}

	@EventHandler
	public void onGameStageChange(GameStartEvent event) {
		SCOREBOARD.clear();

		SCOREBOARD.blankLine(11);
		SCOREBOARD.setScore(10, new Score("Invencivel por: §7", "time"));
		SCOREBOARD.setScore(9, new Score("Jogadores: §70/80", "players"));
		SCOREBOARD.blankLine(8);
		SCOREBOARD.setScore(7, new Score("Kit 1: §7Nenhum", "kit1"));
		SCOREBOARD.setScore(6, new Score("Kit 2: §7Nenhum", "kit2"));
		SCOREBOARD.setScore(5, new Score("Kills: §70", "kills"));
		SCOREBOARD.blankLine(2);
		SCOREBOARD.setScore(1, new Score("§awww.saintmc.com.br", "site"));

		for (Player player : Bukkit.getOnlinePlayers()) {
			SCOREBOARD.createScoreboard(player);

			Gamer gamer = GameGeneral.getInstance().getGamerController().getGamer(player);

			SCOREBOARD.updateScore(player,
					new Score("Kit 1: §7" + NameUtils.formatString(gamer.getKitName(KitType.PRIMARY)), "kit1"));
			SCOREBOARD.updateScore(player,
					new Score("Kit 2: §7" + NameUtils.formatString(gamer.getKitName(KitType.SECONDARY)), "kit2"));
		}

		SCOREBOARD.updateScore(new Score("Jogadores: §7" + getGameGeneral().getPlayersInGame(), "players"));
	}

	@EventHandler
	public void onScoreboardTitleChange(ScoreboardTitleChangeEvent event) {
		SCOREBOARD.setDisplayName(event.getNewTitle());
		SCOREBOARD.setDisplayName(event.getNewTitle());
	}

	public void createScoreboard(Player player) {
		SCOREBOARD.createScoreboard(player);
	}
}
