package tk.yallandev.saintmc.kitpvp.warp;

import org.bukkit.Bukkit;

import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.bukkit.api.scoreboard.Score;
import tk.yallandev.saintmc.bukkit.api.scoreboard.Scoreboard;
import tk.yallandev.saintmc.bukkit.api.scoreboard.impl.SimpleScoreboard;

public class ScoreboardConst {

	public static final Scoreboard DEFAULT_SCOREBOARD;
	
	public static final Scoreboard SHADOW_SCOREBOARD;
	public static final Scoreboard SHADOW_FIGHT_SCOREBOARD;
	public static final Scoreboard SHADOW_SEARCHING_SCOREBOARD;

	static {
		DEFAULT_SCOREBOARD = new SimpleScoreboard("§6§lKITPVP");

		DEFAULT_SCOREBOARD.blankLine(12);
		DEFAULT_SCOREBOARD.setScore(11, new Score("§fKills: §e0", "kills"));
		DEFAULT_SCOREBOARD.setScore(10, new Score("§fDeaths: §e0", "deaths"));
		DEFAULT_SCOREBOARD.setScore(9, new Score("§fKillstreak: §e0", "killstreak"));
		DEFAULT_SCOREBOARD.blankLine(8);
		DEFAULT_SCOREBOARD.setScore(7, new Score("§fRanking: §7(§f-§7)", "rank"));
		DEFAULT_SCOREBOARD.setScore(6, new Score("§fXp: §a0", "xp"));
		DEFAULT_SCOREBOARD.blankLine(5);
		DEFAULT_SCOREBOARD.setScore(4, new Score("§fMoney: §60", "coins"));
		DEFAULT_SCOREBOARD.setScore(3, new Score("§fJogadores: §b" + Bukkit.getOnlinePlayers().size(), "players"));
		DEFAULT_SCOREBOARD.blankLine(2);
		DEFAULT_SCOREBOARD.setScore(1, new Score("§6" + CommonConst.SITE, "site"));

		SHADOW_SCOREBOARD = new SimpleScoreboard("§6§lKITPVP");

		SHADOW_SCOREBOARD.blankLine(11);
		SHADOW_SCOREBOARD.setScore(10, new Score("§fVitórias: §e0", "wins"));
		SHADOW_SCOREBOARD.setScore(9, new Score("§fDerrotas: §e0", "loses"));
		SHADOW_SCOREBOARD.setScore(8, new Score("§fWinstreak: §e0", "winstreak"));
		SHADOW_SCOREBOARD.blankLine(7);
		SHADOW_SCOREBOARD.setScore(6, new Score("§fRanking: §7(§f-§7)", "rank"));
		SHADOW_SCOREBOARD.setScore(5, new Score("§fXp: §a0", "xp"));
		SHADOW_SCOREBOARD.blankLine(4);
		SHADOW_SCOREBOARD.setScore(3, new Score("§fJogadores: §b" + Bukkit.getOnlinePlayers().size(), "players"));
		SHADOW_SCOREBOARD.blankLine(2);
		SHADOW_SCOREBOARD.setScore(1, new Score("§6" + CommonConst.SITE, "site"));

		SHADOW_SEARCHING_SCOREBOARD = new SimpleScoreboard("§6§lKITPVP");

		SHADOW_SEARCHING_SCOREBOARD.blankLine(10);
		SHADOW_SEARCHING_SCOREBOARD.setScore(9, new Score("§fProcurando: §e", "searching"));
		SHADOW_SEARCHING_SCOREBOARD.setScore(8, new Score("§fTempo: §e0s", "time"));
		SHADOW_SEARCHING_SCOREBOARD.blankLine(7);
		SHADOW_SEARCHING_SCOREBOARD.setScore(6, new Score("§fRanking: §a-/-", "rank"));
		SHADOW_SEARCHING_SCOREBOARD.setScore(5, new Score("§fXp: §a0", "xp"));
		SHADOW_SEARCHING_SCOREBOARD.blankLine(4);
		SHADOW_SEARCHING_SCOREBOARD.setScore(3, new Score("§fJogadores: §b" + Bukkit.getOnlinePlayers().size(), "players"));
		SHADOW_SEARCHING_SCOREBOARD.blankLine(2);
		SHADOW_SEARCHING_SCOREBOARD.setScore(1, new Score("§6" + CommonConst.SITE, "site"));

		SHADOW_FIGHT_SCOREBOARD = new SimpleScoreboard("§6§lKITPVP");

		SHADOW_FIGHT_SCOREBOARD.blankLine(11);
		SHADOW_FIGHT_SCOREBOARD.setScore(10, new Score("§9Ninguém: §e0ms", "playerPing"));
		SHADOW_FIGHT_SCOREBOARD.setScore(9, new Score("§cNinguém: §e0ms", "targetPing"));
		SHADOW_FIGHT_SCOREBOARD.blankLine(8);
		SHADOW_FIGHT_SCOREBOARD.setScore(7, new Score("§fRanking: §a-/-", "rank"));
		SHADOW_FIGHT_SCOREBOARD.setScore(6, new Score("§fXp: §a0", "xp"));
		SHADOW_FIGHT_SCOREBOARD.blankLine(5);
		SHADOW_FIGHT_SCOREBOARD.setScore(4, new Score("§fWarp: §a1v1", "warp"));
		SHADOW_FIGHT_SCOREBOARD.setScore(3, new Score("§fWinstreak: §70", "winstreak"));
		SHADOW_FIGHT_SCOREBOARD.blankLine(2);
		SHADOW_FIGHT_SCOREBOARD.setScore(1, new Score("§6" + CommonConst.SITE, "site"));
	}

}
