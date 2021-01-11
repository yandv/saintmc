package tk.yallandev.saintmc.kitpvp.warp.scoreboard.types;

import org.bukkit.entity.Player;

import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.api.scoreboard.Score;
import tk.yallandev.saintmc.bukkit.api.scoreboard.impl.SimpleScoreboard;
import tk.yallandev.saintmc.bukkit.bukkit.BukkitMember;
import tk.yallandev.saintmc.common.utils.string.StringUtils;
import tk.yallandev.saintmc.kitpvp.warp.scoreboard.WarpScoreboard;

public class VoidScoreboard extends WarpScoreboard {

	public VoidScoreboard() {
		super(new SimpleScoreboard("§6§lVOID CHALLENGE"));

		scoreboard.blankLine(5);
		scoreboard.setScore(4, new Score("Tempo atual: §a0s", "time"));
		scoreboard.setScore(3, new Score("Tempo record: §c-/-", "record"));
		scoreboard.blankLine(2);
		scoreboard.setScore(1, new Score("§e" + CommonConst.SITE, "site"));
	}

	@Override
	public void loadScoreboard(Player player) {
		scoreboard.createScoreboard(player);
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

	}

	@Override
	public void updateScore(Player player, UpdateType updateType) {

	}

	@Override
	public <T> void updateScore(Player player, T t) {
		if (t instanceof Long) {
			Long time = (Long) t;

			if (time == -1)
				scoreboard.updateScore(player, new Score("Tempo atual: §a0s", "time"));
			else
				scoreboard.updateScore(player,
						new Score(
								"Tempo atual: §a"
										+ StringUtils.formatTime((int) ((System.currentTimeMillis() - time) / 1000)),
								"time"));
		}
	}

}
