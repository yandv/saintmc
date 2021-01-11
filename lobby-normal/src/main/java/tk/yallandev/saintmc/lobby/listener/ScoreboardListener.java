package tk.yallandev.saintmc.lobby.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.api.scoreboard.Score;
import tk.yallandev.saintmc.bukkit.api.scoreboard.Scoreboard;
import tk.yallandev.saintmc.bukkit.api.scoreboard.impl.SimpleScoreboard;
import tk.yallandev.saintmc.bukkit.event.account.PlayerChangeGroupEvent;
import tk.yallandev.saintmc.bukkit.event.account.PlayerChangeLeagueEvent;
import tk.yallandev.saintmc.bukkit.event.player.PlayerScoreboardStateEvent;
import tk.yallandev.saintmc.bukkit.event.server.PlayerChangeEvent;
import tk.yallandev.saintmc.common.account.League;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.tag.Tag;
import tk.yallandev.saintmc.lobby.LobbyPlatform;

public class ScoreboardListener implements Listener {

	public static final Scoreboard DEFAULT_SCOREBOARD;

	static {
		DEFAULT_SCOREBOARD = new SimpleScoreboard("§6§lLOBBY");

		DEFAULT_SCOREBOARD.blankLine(11);
		DEFAULT_SCOREBOARD.setScore(10, new Score("§fGrupo: §7§lMEMBRO", "group"));
		DEFAULT_SCOREBOARD.setScore(9, new Score("§fRanking: §7(§f-§7)", "ranking"));
		DEFAULT_SCOREBOARD.setScore(8, new Score("§fClan: §7-/-", "clan"));
		DEFAULT_SCOREBOARD.blankLine(7);
		DEFAULT_SCOREBOARD.setScore(6, new Score("§fXp: §a0", "xp"));
		DEFAULT_SCOREBOARD.setScore(5, new Score("§fMoedas: §a0", "money"));
		DEFAULT_SCOREBOARD.blankLine(4);
		DEFAULT_SCOREBOARD.setScore(3,
				new Score("§fJogadores: §a" + BukkitMain.getInstance().getServerManager().getTotalNumber(), "online"));
		DEFAULT_SCOREBOARD.blankLine(2);
		DEFAULT_SCOREBOARD.setScore(1, new Score("§e" + CommonConst.SITE, "site"));
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event) {
		handleScoreboard(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerChangeEvent event) {
		DEFAULT_SCOREBOARD.updateScore(new Score("Jogadores: §a" + event.getTotalMembers(), "online"));
	}

	@EventHandler
	public void onPlayerChangeGroup(PlayerChangeGroupEvent event) {
		new BukkitRunnable() {

			@Override
			public void run() {
				Group group = event.getGroup();

				DEFAULT_SCOREBOARD.updateScore(event.getPlayer(),
						new Score("§fGrupo: "
								+ (group == Group.MEMBRO ? "§7§lMEMBRO" : Tag.valueOf(group.name()).getPrefix()),
								"group"));
			}
		}.runTaskLater(LobbyPlatform.getInstance(), 10l);
	}

	@EventHandler
	public void onPlayerChangeLeague(PlayerChangeLeagueEvent event) {
		new BukkitRunnable() {

			@Override
			public void run() {
				League league = event.getNewLeague();

				DEFAULT_SCOREBOARD.updateScore(event.getPlayer(),
						new Score("§fRanking: §7(" + league.getColor() + league.getSymbol() + "§7)", "ranking"));
			}
		}.runTaskLater(LobbyPlatform.getInstance(), 10l);
	}

	@EventHandler
	public void onPlayerScoreboardState(PlayerScoreboardStateEvent event) {
		if (event.isScoreboardEnabled())
			handleScoreboard(event.getPlayer());
	}

	private void handleScoreboard(Player player) {
		Member member = CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId());

		if (member == null) {
			player.kickPlayer("§cSua conta não foi carregada!");
			return;
		}

		DEFAULT_SCOREBOARD.createScoreboard(player);

		Group group = member.getGroup();
		League league = member.getLeague();

		DEFAULT_SCOREBOARD.updateScore(player, new Score(
				"§fGrupo: " + (group == Group.MEMBRO ? "§7§lMEMBRO" : Tag.valueOf(group.name()).getPrefix()), "group"));
		DEFAULT_SCOREBOARD.updateScore(player,
				new Score("§fRanking: §7(" + league.getColor() + league.getSymbol() + "§7)", "ranking"));
		DEFAULT_SCOREBOARD.updateScore(player, new Score("§fXp: §a" + member.getXp(), "xp"));
		DEFAULT_SCOREBOARD.updateScore(player, new Score("§fMoedas: §a" + member.getMoney(), "money"));
		DEFAULT_SCOREBOARD.updateScore(player,
				new Score("§fClan: §7" + (member.hasClan() ? member.getClan().getClanAbbreviation() : "-/-"), "clan"));

		new BukkitRunnable() {

			@Override
			public void run() {
				DEFAULT_SCOREBOARD.updateScore(new Score(
						"§fJogadores: §a" + BukkitMain.getInstance().getServerManager().getTotalNumber(), "online"));
			}
		}.runTaskLater(LobbyPlatform.getInstance(), 20l);
	}

}
