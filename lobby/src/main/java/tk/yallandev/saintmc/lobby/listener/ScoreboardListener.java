package tk.yallandev.saintmc.lobby.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.api.scoreboard.Score;
import tk.yallandev.saintmc.bukkit.api.scoreboard.Scoreboard;
import tk.yallandev.saintmc.bukkit.api.scoreboard.impl.SimpleScoreboard;
import tk.yallandev.saintmc.bukkit.event.account.PlayerChangeGroupEvent;
import tk.yallandev.saintmc.bukkit.event.account.PlayerChangeLeagueEvent;
import tk.yallandev.saintmc.common.account.League;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.permission.Tag;
import tk.yallandev.saintmc.lobby.LobbyMain;

public class ScoreboardListener implements Listener {

	public static final Scoreboard DEFAULT_SCOREBOARD;

	static {

		DEFAULT_SCOREBOARD = new SimpleScoreboard("§b§lLOBBY");

		DEFAULT_SCOREBOARD.blankLine(10);
		DEFAULT_SCOREBOARD.setScore(9, new Score("§fGrupo: §7§lMEMBRO", "group"));
		DEFAULT_SCOREBOARD.setScore(8, new Score("§fRanking: §7(§f-§7)", "ranking"));
		DEFAULT_SCOREBOARD.blankLine(7);
		DEFAULT_SCOREBOARD.setScore(6, new Score("§fXp: §70", "xp"));
		DEFAULT_SCOREBOARD.setScore(5, new Score("§fMoney: §70", "money"));
		DEFAULT_SCOREBOARD.blankLine(4);
		DEFAULT_SCOREBOARD.setScore(3,
				new Score("§fJogadores: §e" + BukkitMain.getInstance().getServerManager().getTotalNumber(), "online"));
		DEFAULT_SCOREBOARD.blankLine(2);
		DEFAULT_SCOREBOARD.setScore(1, new Score("§a" + CommonConst.STORE, "site"));
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		Member member = CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId());

		DEFAULT_SCOREBOARD.createScoreboard(player);

		Group group = member.getGroup();
		League league = member.getLeague();

		DEFAULT_SCOREBOARD.updateScore(player, new Score(
				"§fGrupo: §f§l" + (group == Group.MEMBRO ? "§7§lMEMBRO" : Tag.valueOf(group.name()).getPrefix()), "group"));
		DEFAULT_SCOREBOARD.updateScore(player,
				new Score("§fRanking: §7(" + league.getColor() + league.getSymbol() + "§7)", "ranking"));
		DEFAULT_SCOREBOARD.updateScore(player,
				new Score("§fXp: §7" + member.getXp(), "xp"));
		DEFAULT_SCOREBOARD.updateScore(player,
				new Score("§fMoney: §7" + member.getMoney(), "money"));

		new BukkitRunnable() {

			@Override
			public void run() {
				DEFAULT_SCOREBOARD.updateScore(new Score(
						"§fJogadores: §e" + BukkitMain.getInstance().getServerManager().getTotalNumber(), "online"));
			}
		}.runTaskLater(LobbyMain.getInstance(), 20l);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerQuitEvent e) {
		new BukkitRunnable() {

			@Override
			public void run() {
				DEFAULT_SCOREBOARD.updateScore(new Score(
						"§fJogadores: §e" + BukkitMain.getInstance().getServerManager().getTotalNumber(), "online"));
			}
		}.runTaskLater(LobbyMain.getInstance(), 20l);
	}

	@EventHandler
	public void onPlayerChangeGroup(PlayerChangeGroupEvent event) {
		new BukkitRunnable() {

			@Override
			public void run() {
				Group group = event.getGroup();

				DEFAULT_SCOREBOARD.updateScore(new Score(
						"§fGrupo: §f§l" + (group == Group.MEMBRO ? "§7§lMEMBRO" : Tag.valueOf(group.name()).getPrefix()),
						"group"));
			}
		}.runTaskLater(LobbyMain.getInstance(), 10l);
	}

	@EventHandler
	public void onPlayerChangeLeague(PlayerChangeLeagueEvent event) {
		new BukkitRunnable() {

			@Override
			public void run() {
				League league = event.getNewLeague();

				DEFAULT_SCOREBOARD.updateScore(
						new Score("§fRanking: §7(" + league.getColor() + league.getSymbol() + "§7)", "ranking"));
			}
		}.runTaskLater(LobbyMain.getInstance(), 10l);
	}

}
