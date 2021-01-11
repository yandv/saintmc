package tk.yallandev.saintmc.kitpvp.warp.scoreboard.types;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.api.scoreboard.Score;
import tk.yallandev.saintmc.bukkit.api.scoreboard.impl.SimpleScoreboard;
import tk.yallandev.saintmc.bukkit.bukkit.BukkitMember;
import tk.yallandev.saintmc.common.account.status.StatusType;
import tk.yallandev.saintmc.common.account.status.types.normal.NormalStatus;
import tk.yallandev.saintmc.kitpvp.warp.scoreboard.WarpScoreboard;

public class FpsScoreboard extends WarpScoreboard {

	public FpsScoreboard() {
		super(new SimpleScoreboard("§6§lFPS"));

		scoreboard.blankLine(8);
		scoreboard.setScore(7, new Score("§fKills: §a0", "kills"));
		scoreboard.setScore(6, new Score("§fDeaths: §a0", "deaths"));
		scoreboard.setScore(5, new Score("§fKillstreak: §a0", "killstreak"));
		scoreboard.blankLine(4);
		scoreboard.setScore(3, new Score("§fJogadores: §a" + Bukkit.getOnlinePlayers().size(), "players"));
		scoreboard.blankLine(2);
		scoreboard.setScore(1, new Score("§e" + CommonConst.SITE, "site"));
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
			NormalStatus killerStatus = CommonGeneral.getInstance().getStatusManager().loadStatus(player.getUniqueId(),
					StatusType.FPS, NormalStatus.class);

			scoreboard.updateScore(player, new Score("§fKills: §e" + killerStatus.getKills(), "kills"));
			scoreboard.updateScore(player, new Score("§fDeaths: §e" + killerStatus.getDeaths(), "deaths"));
			scoreboard.updateScore(player, new Score("§fKillstreak: §e" + killerStatus.getKillstreak(), "killstreak"));
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
