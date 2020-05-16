package tk.yallandev.saintmc.game.games.hungergames.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.api.scoreboard.Scoreboard;
import tk.yallandev.saintmc.bukkit.api.scoreboard.types.SimpleScoreboard;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.utils.string.NameUtils;
import tk.yallandev.saintmc.game.constructor.Gamer;
import tk.yallandev.saintmc.game.games.hungergames.HungerGamesMode;

public class ScoreboardManager {

	private HungerGamesMode mode;
	private String title;

	public ScoreboardManager(HungerGamesMode mode) {
		this.mode = mode;
	}

	public void createScoreboard(Player player) {
		Gamer gamer = Gamer.getGamer(player);
		Member member = CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId());
		
		if (!member.getAccountConfiguration().isScoreboardEnabled())
			return;
		
		Scoreboard scoreboard = new SimpleScoreboard(player, "main", "§8§l>> §6§lgui é viado §8§l<<") {
			
			@Override
			public void createScoreboard() {
				addScore("v3", 8, "");
				addScore("timer", 7, T.t(BukkitMain.getInstance(), lang, "scoreboard-starts-in") + StringTimeUtils.format(mode.getGameMain().getTimer()));
				addScore("players", 6, T.t(BukkitMain.getInstance(), lang, "scoreboard-players") + mode.getGameMain().playersLeft());
				addScore("v2", 5, "");
				addScore("kit", 4, T.t(BukkitMain.getInstance(), lang, "scoreboard-kit") + NameUtils.formatString(Gamer.getGamer(player).getKitName()));
				addScore("kills", 3, T.t(BukkitMain.getInstance(), lang, "scoreboard-kills") + gamer.getMatchkills());
				addScore("v1", 2, "");
				addScore("site", 1, "§6" + CommonConst.WEBSITE);
			}
			
		};
		
		scoreboard.createScoreboard();
	}

	public void updateTimer() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			updateTimer(p);
		}
	}

	public void updateTimer(Player p) {
		Language lang = BattlePlayer.getLanguage(p.getUniqueId());
		String str = T.t(BukkitMain.getInstance(), lang, "scoreboard-starts-in");
		
		switch (mode.getGameMain().getGameStage()) {
		case FINAL:
		case GAMETIME:
		case WINNER:
			str = T.t(BukkitMain.getInstance(), lang, "scoreboard-current-time");
			break;
		case INVINCIBILITY:
			str = T.t(BukkitMain.getInstance(), lang, "scoreboard-invincibility");
			break;
		default:
			break;
		}
		
		updateScore(p, "timer", str + StringTimeUtils.format(mode.getGameMain().getTimer()));
	}

	public void addFeastTimer(Location loc, int time) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			addFeastTimer(p, loc, time);
		}
	}

	public void addFeastTimer(Player player, Location loc, int time) {
		Language lang = BattlePlayer.getLanguage(player.getUniqueId());
		
		Scoreboard scoreboard = BukkitMain.getInstance().getScoreboardManager().getScoreboard(player);
		
		if (scoreboard == null || !scoreboard.isActived())
			return;
		
		scoreboard.addScore("feastTimer", 7,T.t(BukkitMain.getInstance(),lang, "scoreboard-feastTimer") + StringTimeUtils.format(time));
		scoreboard.addScore("feastLocation", 6, T.t(BukkitMain.getInstance(),lang, "scoreboard-feastLocation")+ loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ());
		scoreboard.addScore("timer", 10, T.t(BukkitMain.getInstance(),lang, "scoreboard-current-time")+ StringTimeUtils.format(mode.getGameMain().getTimer()));
		scoreboard.addScore("players", 9, T.t(BukkitMain.getInstance(),lang, "scoreboard-players")+ mode.getGameMain().playersLeft() + "/" + mode.getGameMain().getTotalPlayers());
		scoreboard.addScore("v4", 11, "");
	}

	public void updateFeastTimer(int time) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			updateFeastTimer(p, time);
		}
	}

	public void updateFeastTimer(Player player, int time) {
		updateScore(player, "feastTimer",
				T.t(BukkitMain.getInstance(),BattlePlayer.getLanguage(player.getUniqueId()), "scoreboard-feastTimer")
						+ StringTimeUtils.format(time));
	}

	public void removeFeastTimer(Location loc) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			removeFeastTimer(p, loc);
		}
	}

	public void removeFeastTimer(Player player, Location loc) {
		Language lang = BattlePlayer.getLanguage(player.getUniqueId());
		updateScore(player, "feastLocation", T.t(BukkitMain.getInstance(),lang, "scoreboard-feastTimer") + loc.getBlockX()
				+ ", " + loc.getBlockY() + ", " + loc.getBlockZ());
		addScore(player, "v6", 7, "");
		addScore(player, "timer", 9, T.t(BukkitMain.getInstance(),lang, "scoreboard-current-time")
				+ StringTimeUtils.format(mode.getGameMain().getTimer()));
		addScore(player, "players", 8, T.t(BukkitMain.getInstance(),lang, "scoreboard-players")
				+ mode.getGameMain().playersLeft());
		addScore(player, "v5", 10, "");
		player.getScoreboard().resetScores("§1§1");
	}

	public void updatePlayerKit(Player player) {
		updateScore(player, "kit",
				T.t(BukkitMain.getInstance(),BattlePlayer.getLanguage(player.getUniqueId()), "scoreboard-kit")
						+ NameUtils.formatString(Gamer.getGamer(player).getKitName()));
	}

	public void updatePlayerKills(Player player) {
		updateScore(player, "kills",
				T.t(BukkitMain.getInstance(),BattlePlayer.getLanguage(player.getUniqueId()), "scoreboard-kills")
						+ Gamer.getGamer(player).getMatchkills());
	}
	
	public void updatePlayersLeft() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			updateScore(p, "players",
					T.t(BukkitMain.getInstance(),BattlePlayer.getLanguage(p.getUniqueId()), "scoreboard-players")
							+ mode.getGameMain().playersLeft());
		}
	}

	public void updatePlayersLeft(int less) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			updateScore(p, "players",
					T.t(BukkitMain.getInstance(),BattlePlayer.getLanguage(p.getUniqueId()), "scoreboard-players")
							+ (mode.getGameMain().playersLeft() - 1));
		}
	}

}
