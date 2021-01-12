package tk.yallandev.saintmc.gladiator.listener;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.api.scoreboard.Score;
import tk.yallandev.saintmc.bukkit.api.scoreboard.Scoreboard;
import tk.yallandev.saintmc.bukkit.api.scoreboard.impl.SimpleScoreboard;
import tk.yallandev.saintmc.bukkit.bukkit.BukkitMember;
import tk.yallandev.saintmc.bukkit.event.player.PlayerScoreboardStateEvent;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.account.status.StatusType;
import tk.yallandev.saintmc.common.account.status.types.normal.NormalStatus;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.tag.Tag;
import tk.yallandev.saintmc.common.utils.string.StringUtils;
import tk.yallandev.saintmc.gladiator.GameMain;
import tk.yallandev.saintmc.gladiator.challenge.Challenge;
import tk.yallandev.saintmc.gladiator.event.GladiatorFinishEvent;
import tk.yallandev.saintmc.gladiator.event.GladiatorPulseEvent;
import tk.yallandev.saintmc.gladiator.event.GladiatorSpectatorEvent;
import tk.yallandev.saintmc.gladiator.event.GladiatorSpectatorEvent.Action;
import tk.yallandev.saintmc.gladiator.event.GladiatorStartEvent;

public class ScoreboardListener implements Listener {

	private static final Scoreboard SCOREBOARD = new SimpleScoreboard("§6§lGLADIATOR");
	private static final Scoreboard FIGHT_SCOREBOARD = new SimpleScoreboard("§6§lGLADIATOR");
	private static final Scoreboard QUEUE_SCOREBOARD = new SimpleScoreboard("§6§lGLADIATOR");

	{
		SCOREBOARD.blankLine(8);
		SCOREBOARD.setScore(7, new Score("Vitórias: 0", "wins"));
		SCOREBOARD.setScore(6, new Score("Derrotas: 0", "loses"));
		SCOREBOARD.setScore(5, new Score("Winstreak: 0", "winstreak"));
		SCOREBOARD.blankLine(4);
		SCOREBOARD.setScore(3, new Score("Ranking: §7(§f-§7)", "rank"));
		SCOREBOARD.setScore(2, new Score("Jogadores: 0", "players"));
		SCOREBOARD.blankLine(1);
		SCOREBOARD.setScore(0, new Score("§e" + CommonConst.SITE, "site"));

		FIGHT_SCOREBOARD.blankLine(9);
		FIGHT_SCOREBOARD.setScore(8, new Score("§9Ninguém: §e0ms", "firstPing"));
		FIGHT_SCOREBOARD.setScore(7, new Score("§cNinguém: §e0ms", "secondPing"));
		FIGHT_SCOREBOARD.blankLine(6);
		FIGHT_SCOREBOARD.setScore(5, new Score("Tempo: §a", "time"));
		FIGHT_SCOREBOARD.blankLine(4);
		FIGHT_SCOREBOARD.setScore(3, new Score("Ranking: §7(§f-§7)", "rank"));
		FIGHT_SCOREBOARD.setScore(2, new Score("Jogadores: 0", "players"));
		FIGHT_SCOREBOARD.blankLine(1);
		FIGHT_SCOREBOARD.setScore(0, new Score("§e" + CommonConst.SITE, "site"));

		QUEUE_SCOREBOARD.blankLine(8);
		QUEUE_SCOREBOARD.setScore(7, new Score("§9Ninguém: 0ms", "firstPing"));
		QUEUE_SCOREBOARD.setScore(6, new Score("§cNinguém: 0ms", "secondPing"));
		QUEUE_SCOREBOARD.blankLine(4);
		QUEUE_SCOREBOARD.setScore(3, new Score("Ranking: §7(§f-§7)", "rank"));
		QUEUE_SCOREBOARD.setScore(2, new Score("Jogadores: 0", "players"));
		QUEUE_SCOREBOARD.blankLine(1);
		QUEUE_SCOREBOARD.setScore(0, new Score("§e" + CommonConst.SITE, "site"));
	}

	@EventHandler
	public void onPlayerWarpJoin(PlayerScoreboardStateEvent event) {
		if (event.isScoreboardEnabled())
			new BukkitRunnable() {

				@Override
				public void run() {
					loadScoreboard(event.getPlayer());
				}
			}.runTaskLater(GameMain.getInstance(), 5l);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event) {
		new BukkitRunnable() {

			@Override
			public void run() {
				loadScoreboard(event.getPlayer());
			}
		}.runTaskLater(GameMain.getInstance(), 7l);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerJoin(PlayerQuitEvent event) {
		SCOREBOARD.removeViewer((BukkitMember) CommonGeneral.getInstance().getMemberManager()
				.getMember(event.getPlayer().getUniqueId()));
		QUEUE_SCOREBOARD.removeViewer((BukkitMember) CommonGeneral.getInstance().getMemberManager()
				.getMember(event.getPlayer().getUniqueId()));
		FIGHT_SCOREBOARD.removeViewer((BukkitMember) CommonGeneral.getInstance().getMemberManager()
				.getMember(event.getPlayer().getUniqueId()));
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerQuit(PlayerQuitEvent event) {
		new BukkitRunnable() {

			@Override
			public void run() {
				SCOREBOARD.updateScore(new Score("Jogadores: §b" + Bukkit.getOnlinePlayers().size(), "players"));
			}
		}.runTaskLater(GameMain.getInstance(), 7l);
	}

	@EventHandler
	public void onGladiatorFinish(GladiatorStartEvent event) {
		Player player = event.getChallenge().getPlayer();
		Player enimy = event.getChallenge().getEnimy();

		SCOREBOARD.removeViewer(
				(BukkitMember) CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId()));
		QUEUE_SCOREBOARD.removeViewer(
				(BukkitMember) CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId()));

		SCOREBOARD.removeViewer(
				(BukkitMember) CommonGeneral.getInstance().getMemberManager().getMember(enimy.getUniqueId()));
		QUEUE_SCOREBOARD.removeViewer(
				(BukkitMember) CommonGeneral.getInstance().getMemberManager().getMember(enimy.getUniqueId()));

		FIGHT_SCOREBOARD.createScoreboard(player);
		FIGHT_SCOREBOARD.createScoreboard(enimy);

		updateScore(player, event.getChallenge());
		updateScore(enimy, event.getChallenge());
	}

	@EventHandler
	public void onGladiatorSpectator(GladiatorSpectatorEvent event) {
		if (event.getAction() == Action.JOIN) {
			SCOREBOARD.removeViewer((BukkitMember) CommonGeneral.getInstance().getMemberManager()
					.getMember(event.getPlayer().getUniqueId()));
			QUEUE_SCOREBOARD.removeViewer((BukkitMember) CommonGeneral.getInstance().getMemberManager()
					.getMember(event.getPlayer().getUniqueId()));

			FIGHT_SCOREBOARD.createScoreboard(event.getPlayer());
			updateScore(event.getPlayer(), event.getChallenge());
		} else
			loadScoreboard(event.getPlayer());
	}

	@EventHandler
	public void onGladiatorPulse(GladiatorPulseEvent event) {
		updateScore(event.getChallenge().getEnimy(), event.getChallenge());
		updateScore(event.getChallenge().getPlayer(), event.getChallenge());

		event.getChallenge().getSpectatorSet().forEach(player -> updateScore(player, event.getChallenge()));
	}

	@EventHandler
	public void onGladiatorFinish(GladiatorFinishEvent event) {
		loadScoreboard(event.getChallenge().getEnimy());
		loadScoreboard(event.getChallenge().getPlayer());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerWarpDeath(GladiatorFinishEvent event) {
		boolean updatePlayer = true;
		boolean updateKiller = event.getWinner() != null;

		if (updatePlayer) {
			Player player = event.getLoser();
			NormalStatus playerStatus = CommonGeneral.getInstance().getStatusManager().loadStatus(player.getUniqueId(),
					StatusType.GLADIATOR, NormalStatus.class);

			SCOREBOARD.updateScore(player, new Score("Vitórias: §a" + playerStatus.getKills(), "wins"));
			SCOREBOARD.updateScore(player, new Score("Derrotas: §a" + playerStatus.getDeaths(), "loses"));
			SCOREBOARD.updateScore(player, new Score("Winstreak: §a" + playerStatus.getKillstreak(), "winstreak"));
		}

		if (updateKiller) {
			Player killer = event.getWinner();
			NormalStatus killerStatus = CommonGeneral.getInstance().getStatusManager().loadStatus(killer.getUniqueId(),
					StatusType.GLADIATOR, NormalStatus.class);

			SCOREBOARD.updateScore(killer, new Score("Vitórias: §a" + killerStatus.getKills(), "wins"));
			SCOREBOARD.updateScore(killer, new Score("Derrotas: §a" + killerStatus.getDeaths(), "loses"));
			SCOREBOARD.updateScore(killer, new Score("Winstreak: §a" + killerStatus.getKillstreak(), "winstreak"));
		}
	}

	public void loadScoreboard(Player player) {
		FIGHT_SCOREBOARD.removeViewer(
				(BukkitMember) CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId()));
		QUEUE_SCOREBOARD.removeViewer(
				(BukkitMember) CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId()));

		SCOREBOARD.createScoreboard(player);
		updateScore(player);
	}

	public void updateScore(Player player) {
		Member member = CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId());
		NormalStatus playerStatus = CommonGeneral.getInstance().getStatusManager().loadStatus(player.getUniqueId(),
				StatusType.GLADIATOR, NormalStatus.class);

		SCOREBOARD.updateScore(player, new Score("Vitórias: §a" + playerStatus.getKills(), "wins"));
		SCOREBOARD.updateScore(player, new Score("Derrotas: §a" + playerStatus.getDeaths(), "loses"));
		SCOREBOARD.updateScore(player, new Score("Winstreak: §a" + playerStatus.getKillstreak(), "winstreak"));

		SCOREBOARD.updateScore(player, new Score(
				"Ranking: §7(" + member.getLeague().getColor() + member.getLeague().getSymbol() + "§7)", "rank"));
		SCOREBOARD.updateScore(player, new Score("Grupo: §7"
				+ (member.getGroup() == Group.MEMBRO ? "Membro" : Tag.valueOf(member.getGroup().name()).getPrefix()),
				"group"));
		SCOREBOARD.updateScore(player, new Score("Xp: §a" + member.getXp(), "xp"));

		SCOREBOARD.updateScore(player, new Score("§fMoney: §6" + member.getMoney(), "coins"));
		SCOREBOARD.updateScore(new Score("Jogadores: §b" + Bukkit.getOnlinePlayers().size(), "players"));
		FIGHT_SCOREBOARD.updateScore(new Score("Jogadores: §b" + Bukkit.getOnlinePlayers().size(), "players"));
		QUEUE_SCOREBOARD.updateScore(new Score("Jogadores: §b" + Bukkit.getOnlinePlayers().size(), "players"));
	}

	private void updateScore(Player player, Challenge challenge) {
		Player enimy = challenge.getPlayer() == player ? challenge.getEnimy() : challenge.getPlayer();
		Player target = challenge.getPlayer() == player ? challenge.getPlayer() : challenge.getEnimy();

		FIGHT_SCOREBOARD.updateScore(new Score("Jogadores: §b" + Bukkit.getOnlinePlayers().size(), "players"));
		FIGHT_SCOREBOARD.updateScore(player, new Score("§9" + target.getName() + ": §e"
				+ (((CraftPlayer) target).getHandle().ping >= 1000 ? "1000+" : ((CraftPlayer) target).getHandle().ping)
				+ "ms", "firstPing"));
		FIGHT_SCOREBOARD.updateScore(player, new Score("§c" + enimy.getName() + ": §e"
				+ (((CraftPlayer) enimy).getHandle().ping >= 1000 ? "1000+" : ((CraftPlayer) enimy).getHandle().ping)
				+ "ms", "secondPing"));
		FIGHT_SCOREBOARD.updateScore(player, new Score("Tempo: §a" + StringUtils.format(challenge.getTime()), "time"));

		Member member = CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId());

		FIGHT_SCOREBOARD.updateScore(player, new Score(
				"Ranking: §7(" + member.getLeague().getColor() + member.getLeague().getSymbol() + "§7)", "rank"));
	}

}
