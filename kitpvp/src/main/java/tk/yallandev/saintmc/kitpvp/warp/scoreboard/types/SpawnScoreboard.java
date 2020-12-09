package tk.yallandev.saintmc.kitpvp.warp.scoreboard.types;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.api.scoreboard.Score;
import tk.yallandev.saintmc.bukkit.api.scoreboard.impl.SimpleScoreboard;
import tk.yallandev.saintmc.bukkit.bukkit.BukkitMember;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.account.status.StatusType;
import tk.yallandev.saintmc.common.account.status.types.normal.NormalStatus;
import tk.yallandev.saintmc.kitpvp.event.kit.PlayerSelectKitEvent;
import tk.yallandev.saintmc.kitpvp.event.warp.PlayerWarpRespawnEvent;
import tk.yallandev.saintmc.kitpvp.warp.scoreboard.WarpScoreboard;

public class SpawnScoreboard extends WarpScoreboard {

	public SpawnScoreboard() {
		super(new SimpleScoreboard("§4§lKITPVP"));

		scoreboard.blankLine(12);
		scoreboard.setScore(11, new Score("§fKills: §e0", "kills"));
		scoreboard.setScore(10, new Score("§fDeaths: §e0", "deaths"));
		scoreboard.setScore(9, new Score("§fKillstreak: §e0", "killstreak"));
		scoreboard.blankLine(8);
		scoreboard.setScore(7, new Score("§fRanking: §7(§f-§7)", "rank"));
		scoreboard.setScore(6, new Score("§fXp: §a0", "xp"));
		scoreboard.blankLine(5);
		scoreboard.setScore(4, new Score("§fCoins: §60", "coins"));
		scoreboard.setScore(3, new Score("§fJogadores: §b" + Bukkit.getOnlinePlayers().size(), "players"));
		scoreboard.blankLine(2);
		scoreboard.setScore(1, new Score("§c" + CommonConst.SITE, "site"));

	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerSelectKit(PlayerSelectKitEvent event) {
		getScoreboard().updateScore(event.getPlayer(), new Score("§fKit: §6" + event.getKit().getKitName(), "coins"));
	}

	@EventHandler
	public void onPlayerWarp(PlayerWarpRespawnEvent event) {
		if (event.getWarp() == warp) {
			Member member = CommonGeneral.getInstance().getMemberManager().getMember(event.getPlayer().getUniqueId());

			if (member != null)
				getScoreboard().updateScore(event.getPlayer(), new Score("§fCoins: §6" + member.getMoney(), "coins"));
		}
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
	public void loadScoreboard(Player player) {
		scoreboard.createScoreboard(player);
		updateScore(player, UpdateType.STATUS);
	}

	@Override
	public void updateScore(UpdateType updateType) {
		switch (updateType) {
		case PLAYER: {
			scoreboard.updateScore(new Score("§fJogadores: §b" + (Bukkit.getOnlinePlayers().size()), "players"));
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
			NormalStatus killerStatus = CommonGeneral.getInstance().getStatusManager().loadStatus(player.getUniqueId(),
					StatusType.PVP, NormalStatus.class);

			scoreboard.updateScore(player, new Score("§fKills: §e" + killerStatus.getKills(), "kills"));
			scoreboard.updateScore(player, new Score("§fDeaths: §e" + killerStatus.getDeaths(), "deaths"));
			scoreboard.updateScore(player, new Score("§fKillstreak: §e" + killerStatus.getKillstreak(), "killstreak"));

			scoreboard.updateScore(player, new Score("§fXp: §a" + member.getXp(), "xp"));
			scoreboard.updateScore(player, new Score("§fCoins: §6" + member.getMoney(), "coins"));
			scoreboard.updateScore(player, new Score(
					"§fRanking: §7(" + member.getLeague().getColor() + member.getLeague().getSymbol() + "§7)", "rank"));
			break;
		}
		default:
			break;
		}
	}

	@Override
	public <T> void updateScore(Player player, T t) {

	}

}
