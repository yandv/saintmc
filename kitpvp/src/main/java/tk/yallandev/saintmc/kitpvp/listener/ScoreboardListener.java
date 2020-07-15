package tk.yallandev.saintmc.kitpvp.listener;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.api.scoreboard.Score;
import tk.yallandev.saintmc.bukkit.api.scoreboard.Scoreboard;
import tk.yallandev.saintmc.bukkit.api.scoreboard.impl.SimpleScoreboard;
import tk.yallandev.saintmc.bukkit.event.account.PlayerChangeGroupEvent;
import tk.yallandev.saintmc.bukkit.event.player.PlayerScoreboardStateEvent;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent.UpdateType;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.account.status.Status;
import tk.yallandev.saintmc.common.account.status.StatusType;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.common.tag.Tag;
import tk.yallandev.saintmc.kitpvp.GameMain;
import tk.yallandev.saintmc.kitpvp.event.challenge.FightFinishEvent;
import tk.yallandev.saintmc.kitpvp.event.challenge.FightStartEvent;
import tk.yallandev.saintmc.kitpvp.event.challenge.SearchingStartEvent;
import tk.yallandev.saintmc.kitpvp.event.challenge.SearchingStopEvent;
import tk.yallandev.saintmc.kitpvp.event.challenge.shadow.ShadowFightFinishEvent;
import tk.yallandev.saintmc.kitpvp.event.challenge.shadow.ShadowFightStartEvent;
import tk.yallandev.saintmc.kitpvp.event.kit.PlayerSelectKitEvent;
import tk.yallandev.saintmc.kitpvp.event.warp.PlayerWarpDeathEvent;
import tk.yallandev.saintmc.kitpvp.event.warp.PlayerWarpJoinEvent;
import tk.yallandev.saintmc.kitpvp.event.warp.PlayerWarpRespawnEvent;
import tk.yallandev.saintmc.kitpvp.kit.Kit;
import tk.yallandev.saintmc.kitpvp.warp.DuelWarp;
import tk.yallandev.saintmc.kitpvp.warp.Warp;
import tk.yallandev.saintmc.kitpvp.warp.types.ShadowWarp;

public class ScoreboardListener implements Listener {

	public static final Scoreboard DEFAULT_SCOREBOARD;
	public static final Scoreboard SHADOW_SCOREBOARD;
	public static final Scoreboard FIGHT_SCOREBOARD;
	public static final Scoreboard SEARCHING_SCOREBOARD;

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

		SEARCHING_SCOREBOARD = new SimpleScoreboard("§6§lKITPVP");

		SEARCHING_SCOREBOARD.blankLine(10);
		SEARCHING_SCOREBOARD.setScore(9, new Score("§fProcurando: §e", "searching"));
		SEARCHING_SCOREBOARD.setScore(8, new Score("§fTempo: §e0s", "time"));
		SEARCHING_SCOREBOARD.blankLine(7);
		SEARCHING_SCOREBOARD.setScore(6, new Score("§fRanking: §a-/-", "rank"));
		SEARCHING_SCOREBOARD.setScore(5, new Score("§fXp: §a0", "xp"));
		SEARCHING_SCOREBOARD.blankLine(4);
		SEARCHING_SCOREBOARD.setScore(3, new Score("§fJogadores: §b" + Bukkit.getOnlinePlayers().size(), "players"));
		SEARCHING_SCOREBOARD.blankLine(2);
		SEARCHING_SCOREBOARD.setScore(1, new Score("§6" + CommonConst.SITE, "site"));

		FIGHT_SCOREBOARD = new SimpleScoreboard("§6§lKITPVP");

		FIGHT_SCOREBOARD.blankLine(11);
		FIGHT_SCOREBOARD.setScore(10, new Score("§9Ninguém: §e0ms", "playerPing"));
		FIGHT_SCOREBOARD.setScore(9, new Score("§cNinguém: §e0ms", "targetPing"));
		FIGHT_SCOREBOARD.blankLine(8);
		FIGHT_SCOREBOARD.setScore(7, new Score("§fRanking: §a-/-", "rank"));
		FIGHT_SCOREBOARD.setScore(6, new Score("§fXp: §a0", "xp"));
		FIGHT_SCOREBOARD.blankLine(5);
		FIGHT_SCOREBOARD.setScore(4, new Score("§fWarp: §a1v1", "warp"));
		FIGHT_SCOREBOARD.setScore(3, new Score("§fWinstreak: §70", "winstreak"));
		FIGHT_SCOREBOARD.blankLine(2);
		FIGHT_SCOREBOARD.setScore(1, new Score("§6" + CommonConst.SITE, "site"));
	}

	private List<FightPingUpdate> observersList;

	public ScoreboardListener() {
		observersList = new ArrayList<>();
	}

	@EventHandler
	public void onUpdate(UpdateEvent event) {
		if (event.getType() == UpdateType.SECOND)
			observersList.forEach(FightPingUpdate::check);
	}

	/*
	 * Bukkit Default
	 */

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		new BukkitRunnable() {

			@Override
			public void run() {
				if (!event.getPlayer().isOnline())
					return;

				DEFAULT_SCOREBOARD
						.updateScore(new Score("§fJogadores: §b" + (Bukkit.getOnlinePlayers().size()), "players"));
				SHADOW_SCOREBOARD
						.updateScore(new Score("§fJogadores: §b" + (Bukkit.getOnlinePlayers().size()), "players"));
				FIGHT_SCOREBOARD
						.updateScore(new Score("§fJogadores: §b" + (Bukkit.getOnlinePlayers().size()), "players"));
				SEARCHING_SCOREBOARD
						.updateScore(new Score("§fJogadores: §b" + (Bukkit.getOnlinePlayers().size()), "players"));

				updateScore(event.getPlayer(), DEFAULT_SCOREBOARD,
						GameMain.getInstance().getWarpManager().getWarpByName("spawn"));
			}
		}.runTaskLater(GameMain.getInstance(), 20l);
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		new BukkitRunnable() {

			@Override
			public void run() {
				if (event.getPlayer().isOnline())
					return;

				DEFAULT_SCOREBOARD
						.updateScore(new Score("§fJogadores: §b" + (Bukkit.getOnlinePlayers().size()), "players"));
				SHADOW_SCOREBOARD
						.updateScore(new Score("§fJogadores: §b" + (Bukkit.getOnlinePlayers().size()), "players"));
				FIGHT_SCOREBOARD
						.updateScore(new Score("§fJogadores: §b" + (Bukkit.getOnlinePlayers().size()), "players"));
				SEARCHING_SCOREBOARD
						.updateScore(new Score("§fJogadores: §b" + (Bukkit.getOnlinePlayers().size()), "players"));

			}
		}.runTaskLater(GameMain.getInstance(), 20l);
	}

	/*
	 * Warp
	 */

	@EventHandler
	public void onPlayerWarpJoin(PlayerWarpJoinEvent event) {
		loadScoreboard(event.getPlayer(), event.getWarp());
	}

	@EventHandler
	public void onPlayerWarpJoin(PlayerScoreboardStateEvent event) {
		if (event.isScoreboardEnabled())
			loadScoreboard(event.getPlayer(),
					GameMain.getInstance().getGamerManager().getGamer(event.getPlayer().getUniqueId()).getWarp());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerWarpDeath(PlayerWarpDeathEvent event) {
		boolean duels = event.getWarp() instanceof DuelWarp;
		StatusType statusType = duels ? StatusType.SHADOW : StatusType.PVP;

		boolean updatePlayer = true;
		boolean updateKiller = event.getKiller() != null;

		Scoreboard scoreboard = DEFAULT_SCOREBOARD;

		if (updatePlayer) {
			Player player = event.getPlayer();
			Status playerStatus = CommonGeneral.getInstance().getStatusManager().loadStatus(player.getUniqueId(),
					statusType);

			if (duels) {
				scoreboard.updateScore(player, new Score("§fVitórias: §e" + playerStatus.getKills(), "wins"));
				scoreboard.updateScore(player, new Score("§fDerrotas: §e" + playerStatus.getDeaths(), "loses"));
				scoreboard.updateScore(player,
						new Score("§fWinstreak: §e" + playerStatus.getKillstreak(), "winstreak"));
			} else {
				scoreboard.updateScore(player, new Score("§fKills: §e" + playerStatus.getKills(), "kills"));
				scoreboard.updateScore(player, new Score("§fDeaths: §e" + playerStatus.getDeaths(), "deaths"));
				scoreboard.updateScore(player,
						new Score("§fKillstreak: §e" + playerStatus.getKillstreak(), "killstreak"));
			}
		}

		if (updateKiller) {
			Player killer = event.getKiller();
			Status killerStatus = CommonGeneral.getInstance().getStatusManager().loadStatus(killer.getUniqueId(),
					statusType);

			if (duels) {
				scoreboard.updateScore(killer, new Score("§fVitórias: §e" + killerStatus.getKills(), "wins"));
				scoreboard.updateScore(killer, new Score("§fDerrotas: §e" + killerStatus.getDeaths(), "loses"));
				scoreboard.updateScore(killer,
						new Score("§fWinstreak: §e" + killerStatus.getKillstreak(), "winstreak"));
			} else {
				scoreboard.updateScore(killer, new Score("§fKills: §e" + killerStatus.getKills(), "kills"));
				scoreboard.updateScore(killer, new Score("§fDeaths: §e" + killerStatus.getDeaths(), "deaths"));
				scoreboard.updateScore(killer,
						new Score("§fKillstreak: §e" + killerStatus.getKillstreak(), "killstreak"));
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void respawn(PlayerWarpRespawnEvent event) {
		Member member = CommonGeneral.getInstance().getMemberManager().getMember(event.getPlayer().getUniqueId());
		DEFAULT_SCOREBOARD.updateScore(event.getPlayer(), new Score("§fMoney: §6" + member.getMoney(), "coins"));
	}

	/*
	 * Member update
	 */

	@EventHandler
	public void onPlayerChangeTag(PlayerChangeGroupEvent event) {
		Player player = event.getPlayer();
		Group group = event.getGroup();

		DEFAULT_SCOREBOARD.updateScore(player, new Score(
				"§fGrupo: §7" + (group == Group.MEMBRO ? "Membro" : Tag.valueOf(group.name()).getPrefix()), "group"));
	}

	/*
	 * Shadow
	 */

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onShadowSearchingStart(SearchingStartEvent event) {
		Player player = event.getPlayer();
		Warp warp = GameMain.getInstance().getGamerManager().getGamer(player.getUniqueId()).getWarp();
		Scoreboard scoreboard = SEARCHING_SCOREBOARD;

		scoreboard.createScoreboard(player);
		scoreboard.updateScore(player, new Score(
				"§fProcurando: §e" + (warp instanceof ShadowWarp ? "1v1 rápido" : "Sumo rápido"), "searching"));
		scoreboard.updateScore(player, new Score("§fTempo: §e0s", "time"));
		updateScore(player, scoreboard, warp);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onShadowSearchingStop(SearchingStopEvent event) {
		Player player = event.getPlayer();
		loadScoreboard(player, GameMain.getInstance().getGamerManager().getGamer(player.getUniqueId()).getWarp());
	}

	@EventHandler
	public void onFightStart(FightStartEvent event) {
		Player player = event.getPlayer();
		Player target = event.getTarget();

		Scoreboard scoreboard = createScoreboard(player, target, event instanceof ShadowFightStartEvent);
		Scoreboard scoreboardTarget = createScoreboard(target, player, event instanceof ShadowFightStartEvent);

		scoreboard.createScoreboard(player);
		scoreboardTarget.createScoreboard(target);

		updateScore(player, scoreboard, event.getWarp());
		updateScore(target, scoreboardTarget, event.getWarp());

		observersList.add(new FightPingUpdate(player, target) {

			@Override
			public FightPingUpdate onUpdate(Player p, int ping) {
				scoreboard.updateScore(player,
						new Score("§9" + player.getName() + ": §e" + ((CraftPlayer) player).getHandle().ping + "ms",
								"playerPing"));
				scoreboard.updateScore(player,
						new Score("§c" + target.getName() + ": §e" + ((CraftPlayer) target).getHandle().ping + "ms",
								"targetPing"));

				scoreboardTarget.updateScore(getTarget(),
						new Score("§9" + target.getName() + ": §e" + ((CraftPlayer) target).getHandle().ping + "ms",
								"playerPing"));
				scoreboardTarget.updateScore(getTarget(),
						new Score("§c" + player.getName() + ": §e" + ((CraftPlayer) player).getHandle().ping + "ms",
								"targetPing"));
				return this;
			}

		}.onUpdate(player, ((CraftPlayer) player).getHandle().ping).onUpdate(target,
				((CraftPlayer) target).getHandle().ping));
	}

	@EventHandler
	public void onFightFinish(FightFinishEvent event) {
		observersList
				.removeIf(update -> update.getTarget() == event.getPlayer() || update.getTarget() == event.getTarget());

		Warp warp = GameMain.getInstance().getWarpManager()
				.getWarpByName(event instanceof ShadowFightFinishEvent ? "1v1" : "sumo");

		if (warp == null) {
			System.out.println("Not found warp " + (event instanceof ShadowFightFinishEvent ? "1v1" : "sumo") + "!");
			return;
		}

		loadScoreboard(event.getTarget(), warp);
		loadScoreboard(event.getPlayer(), warp);
	}

	/*
	 * Kit
	 */

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerSelectKit(PlayerSelectKitEvent event) {
		Player player = event.getPlayer();
		Kit kit = event.getKit();

		DEFAULT_SCOREBOARD.updateScore(player, new Score("§fKit: §6" + kit.getKitName(), "coins"));
	}

	public void loadScoreboard(Player player, Warp warp) {
		Scoreboard scoreboard = warp.getScoreboard();

		scoreboard.createScoreboard(player);
		updateScore(player, scoreboard, warp);
	}

	public void updateScore(Player player, Scoreboard scoreboard, Warp warp) {

		boolean duels = warp instanceof DuelWarp;

		Member member = CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId());
		Status playerStatus = CommonGeneral.getInstance().getStatusManager().loadStatus(player.getUniqueId(),
				duels ? StatusType.SHADOW : StatusType.PVP);

		if (duels) {
			scoreboard.updateScore(player, new Score("§fVitórias: §e" + playerStatus.getKills(), "wins"));
			scoreboard.updateScore(player, new Score("§fDerrotas: §e" + playerStatus.getDeaths(), "loses"));
			scoreboard.updateScore(player, new Score("§fWinstreak: §e" + playerStatus.getKillstreak(), "winstreak"));
		} else {
			scoreboard.updateScore(player, new Score("§fKills: §e" + playerStatus.getKills(), "kills"));
			scoreboard.updateScore(player, new Score("§fDeaths: §e" + playerStatus.getDeaths(), "deaths"));
			scoreboard.updateScore(player, new Score("§fKillstreak: §e" + playerStatus.getKillstreak(), "killstreak"));
		}

		scoreboard.updateScore(player, new Score(
				"§fRanking: §7(" + member.getLeague().getColor() + member.getLeague().getSymbol() + "§7)", "rank"));
		scoreboard.updateScore(player, new Score("§fGrupo: §7"
				+ (member.getGroup() == Group.MEMBRO ? "Membro" : Tag.valueOf(member.getGroup().name()).getPrefix()),
				"group"));
		scoreboard.updateScore(player, new Score("§fXp: §a" + member.getXp(), "xp"));

		scoreboard.updateScore(player, new Score("§fMoney: §6" + member.getMoney(), "coins"));
		scoreboard.updateScore(new Score("§fJogadores: §b" + Bukkit.getOnlinePlayers().size(), "players"));
	}

	public Scoreboard createScoreboard(Player player, Player target, boolean shadow) {
		return FIGHT_SCOREBOARD;
	}

	@AllArgsConstructor
	@Getter
	public abstract class FightPingUpdate {

		private Player player;
		private Player target;

		public void check() {
			if (player.isOnline())
				onUpdate(player, ((CraftPlayer) player).getHandle().ping);
			else {
				observersList.remove(this);
				return;
			}

			if (target.isOnline())
				onUpdate(target, ((CraftPlayer) target).getHandle().ping);
			else
				observersList.remove(this);
		}

		public abstract FightPingUpdate onUpdate(Player player, int ping);

	}

}
