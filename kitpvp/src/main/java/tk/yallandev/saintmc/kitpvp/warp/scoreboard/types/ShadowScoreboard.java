package tk.yallandev.saintmc.kitpvp.warp.scoreboard.types;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import lombok.AllArgsConstructor;
import lombok.Getter;
import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.api.scoreboard.Score;
import tk.yallandev.saintmc.bukkit.api.scoreboard.Scoreboard;
import tk.yallandev.saintmc.bukkit.api.scoreboard.impl.SimpleScoreboard;
import tk.yallandev.saintmc.bukkit.bukkit.BukkitMember;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.account.status.StatusType;
import tk.yallandev.saintmc.common.account.status.types.normal.NormalStatus;
import tk.yallandev.saintmc.common.utils.string.StringUtils;
import tk.yallandev.saintmc.kitpvp.event.challenge.shadow.ShadowFightFinishEvent;
import tk.yallandev.saintmc.kitpvp.event.challenge.shadow.ShadowFightStartEvent;
import tk.yallandev.saintmc.kitpvp.event.challenge.shadow.ShadowSearchingStartEvent;
import tk.yallandev.saintmc.kitpvp.event.challenge.shadow.ShadowSearchingStopEvent;
import tk.yallandev.saintmc.kitpvp.event.warp.PlayerWarpDeathEvent;
import tk.yallandev.saintmc.kitpvp.warp.scoreboard.WarpScoreboard;
import tk.yallandev.saintmc.kitpvp.warp.types.ShadowWarp;

public class ShadowScoreboard extends WarpScoreboard {

	private Scoreboard searchingScoreboard;
	private Scoreboard fightScoreboard;

	private List<FightPingUpdate> observersList;

	public ShadowScoreboard() {
		super(new SimpleScoreboard("§4§l1v1"));

		scoreboard.blankLine(11);
		scoreboard.setScore(10, new Score("Vitórias: §e0", "wins"));
		scoreboard.setScore(9, new Score("Derrotas: §e0", "loses"));
		scoreboard.setScore(8, new Score("Winstreak: §e0", "winstreak"));
		scoreboard.blankLine(7);
		scoreboard.setScore(6, new Score("Ranking: §7(§f-§7)", "rank"));
		scoreboard.setScore(5, new Score("Xp: §a0", "xp"));
		scoreboard.blankLine(4);
		scoreboard.setScore(3, new Score("Jogadores: §b" + Bukkit.getOnlinePlayers().size(), "players"));
		scoreboard.blankLine(2);
		scoreboard.setScore(1, new Score("§c" + CommonConst.SITE, "site"));

		searchingScoreboard = new SimpleScoreboard("§4§lPROCURANDO");

		searchingScoreboard.blankLine(5);
		searchingScoreboard.setScore(4, new Score("Procurando: §e1v1 rápido", "searching"));
		searchingScoreboard.setScore(3, new Score("Tempo: §e0s", "time"));
		searchingScoreboard.blankLine(2);
		searchingScoreboard.setScore(1, new Score("§c" + CommonConst.SITE, "site"));

		fightScoreboard = new SimpleScoreboard("§4§l1v1");

		fightScoreboard.blankLine(8);
		fightScoreboard.setScore(7, new Score("§9Ninguém: §e0ms", "playerPing"));
		fightScoreboard.setScore(6, new Score("§cNinguém: §e0ms", "targetPing"));
		fightScoreboard.blankLine(5);
		fightScoreboard.setScore(4, new Score("Warp: §a1v1", "warp"));
		fightScoreboard.setScore(3, new Score("Winstreak: §70", "winstreak"));
		fightScoreboard.blankLine(2);
		fightScoreboard.setScore(1, new Score("§c" + CommonConst.SITE, "site"));

		observersList = new ArrayList<>();
	}

	@EventHandler
	public void onUpdate(UpdateEvent event) {
		if (event.getType() == tk.yallandev.saintmc.bukkit.event.update.UpdateEvent.UpdateType.SECOND)
			observersList.forEach(FightPingUpdate::check);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onShadowSearchingStart(ShadowSearchingStartEvent event) {
		Player player = event.getPlayer();

		scoreboard.removeViewer(
				(BukkitMember) CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId()));
		searchingScoreboard.createScoreboard(player);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onShadowSearchingStop(ShadowSearchingStopEvent event) {
		Player player = event.getPlayer();
		loadScoreboard(player);
		searchingScoreboard.removeViewer(
				(BukkitMember) CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId()));
	}

	@EventHandler
	public void onShadowFightStart(ShadowFightStartEvent event) {
		Player player = event.getPlayer();
		Player target = event.getTarget();

		fightScoreboard.createScoreboard(player);
		fightScoreboard.createScoreboard(target);

		fightScoreboard.updateScore(player,
				new Score("Winstreak: §7" + CommonGeneral.getInstance().getStatusManager()
						.loadStatus(player.getUniqueId(), StatusType.SHADOW, NormalStatus.class).getKillstreak(),
						"winstreak"));

		fightScoreboard.updateScore(target,
				new Score("Winstreak: §7" + CommonGeneral.getInstance().getStatusManager()
						.loadStatus(target.getUniqueId(), StatusType.SHADOW, NormalStatus.class).getKillstreak(),
						"winstreak"));

		observersList.add(new FightPingUpdate(player, target) {

			@Override
			public FightPingUpdate onUpdate(Player p, int ping) {
				fightScoreboard.updateScore(player,
						new Score("§9" + player.getName() + ": §e" + ((CraftPlayer) player).getHandle().ping + "ms",
								"playerPing"));
				fightScoreboard.updateScore(player,
						new Score("§c" + target.getName() + ": §e" + ((CraftPlayer) target).getHandle().ping + "ms",
								"targetPing"));

				fightScoreboard.updateScore(getTarget(),
						new Score("§9" + target.getName() + ": §e" + ((CraftPlayer) target).getHandle().ping + "ms",
								"playerPing"));
				fightScoreboard.updateScore(getTarget(),
						new Score("§c" + player.getName() + ": §e" + ((CraftPlayer) player).getHandle().ping + "ms",
								"targetPing"));
				return this;
			}

		}.onUpdate(player, ((CraftPlayer) player).getHandle().ping).onUpdate(target,
				((CraftPlayer) target).getHandle().ping));
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onShadowFightFinish(ShadowFightFinishEvent event) {
		observersList
				.removeIf(update -> update.getTarget() == event.getPlayer() || update.getTarget() == event.getTarget());

		fightScoreboard.removeViewer((BukkitMember) CommonGeneral.getInstance().getMemberManager()
				.getMember(event.getPlayer().getUniqueId()));
		fightScoreboard.removeViewer((BukkitMember) CommonGeneral.getInstance().getMemberManager()
				.getMember(event.getTarget().getUniqueId()));

		if (event.getWarp() instanceof ShadowWarp) {
			loadScoreboard(event.getPlayer());
			updateScore(event.getPlayer(), UpdateType.STATUS);

			if (event.getTarget() instanceof Player) {
				loadScoreboard(event.getTarget());
				updateScore(event.getTarget(), UpdateType.STATUS);
			}
		}
	}

	@EventHandler
	public void onPlayerWarpDeath(PlayerWarpDeathEvent event) {
		if (event.getWarp() instanceof ShadowWarp) {
			loadScoreboard(event.getPlayer());
			updateScore(event.getPlayer(), UpdateType.STATUS);

			if (event.getKiller() instanceof Player) {
				loadScoreboard(event.getKiller());
				updateScore(event.getKiller(), UpdateType.STATUS);
			}
		}
	}

	@Override
	public void loadScoreboard(Player player) {
		scoreboard.createScoreboard(player);
		updateScore(player, UpdateType.STATUS);
	}

	@Override
	public void unloadScoreboard(Player player) {
		BukkitMember member = (BukkitMember) CommonGeneral.getInstance().getMemberManager()
				.getMember(player.getUniqueId());

		if (member == null)
			return;

		scoreboard.removeViewer(member);
	}

	@Override
	public void updateScore(UpdateType updateType) {
		switch (updateType) {
		case PLAYER: {
			scoreboard.updateScore(new Score("Jogadores: §b" + (Bukkit.getOnlinePlayers().size()), "players"));
			break;
		}
		default:
			break;
		}
	}

	@Override
	public void updateScore(Player player, UpdateType updateType) {
		switch (updateType) {
		case PLAYER: {
			throw new IllegalStateException("Player is not a single accessible method!");
		}
		case STATUS: {
			Member member = CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId());
			NormalStatus normalStatus = CommonGeneral.getInstance().getStatusManager().loadStatus(player.getUniqueId(),
					StatusType.SHADOW, NormalStatus.class);

			scoreboard.updateScore(player, new Score("Vitórias: §e" + normalStatus.getKills(), "wins"));
			scoreboard.updateScore(player, new Score("Derrotas: §e" + normalStatus.getDeaths(), "loses"));
			scoreboard.updateScore(player, new Score("Winstreak: §e" + normalStatus.getKillstreak(), "winstreak"));

			scoreboard.updateScore(player, new Score("Xp: §a" + member.getXp(), "xp"));
			scoreboard.updateScore(player, new Score("Coins: §6" + member.getMoney(), "coins"));
			scoreboard.updateScore(player, new Score(
					"Ranking: §7(" + member.getLeague().getColor() + member.getLeague().getSymbol() + "§7)", "rank"));
			break;
		}
		default:
			break;
		}
	}

	@Override
	public <T> void updateScore(Player player, T t) {
		if (t instanceof Integer) {
			Integer integer = (Integer) t;

			searchingScoreboard.updateScore(player, new Score("Tempo: §e" + StringUtils.formatTime(integer), "time"));
		}
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
