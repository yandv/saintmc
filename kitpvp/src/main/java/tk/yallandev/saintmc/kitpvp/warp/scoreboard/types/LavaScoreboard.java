package tk.yallandev.saintmc.kitpvp.warp.scoreboard.types;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.api.scoreboard.Score;
import tk.yallandev.saintmc.bukkit.api.scoreboard.Scoreboard;
import tk.yallandev.saintmc.bukkit.api.scoreboard.impl.SimpleScoreboard;
import tk.yallandev.saintmc.bukkit.bukkit.BukkitMember;
import tk.yallandev.saintmc.common.account.status.StatusType;
import tk.yallandev.saintmc.common.account.status.types.challenge.ChallengeStatus;
import tk.yallandev.saintmc.common.account.status.types.challenge.ChallengeType;
import tk.yallandev.saintmc.common.utils.string.StringUtils;
import tk.yallandev.saintmc.kitpvp.event.lava.PlayerFinishChallengeEvent;
import tk.yallandev.saintmc.kitpvp.event.lava.PlayerStartChallengeEvent;
import tk.yallandev.saintmc.kitpvp.event.lava.PlayerStopChallengeEvent;
import tk.yallandev.saintmc.kitpvp.warp.lava.ChallengeInfo;
import tk.yallandev.saintmc.kitpvp.warp.lava.ChallengeStage;
import tk.yallandev.saintmc.kitpvp.warp.scoreboard.WarpScoreboard;

public class LavaScoreboard extends WarpScoreboard {

	private Scoreboard challengeScoreboard;

	public LavaScoreboard() {
		super(new SimpleScoreboard("§6§lLAVA CHALLENGE"));

		scoreboard.blankLine(15);
		scoreboard.setScore(14, new Score("§4Extremo: ", "top"));
		scoreboard.setScore(13, new Score(" Passou: §a0", "pass"));
		scoreboard.setScore(12, new Score(" Tempo record: §70s", "record"));
		scoreboard.setScore(11, new Score("§cDificil: ", "top1"));
		scoreboard.setScore(10, new Score(" Passou: §a0", "pass1"));
		scoreboard.setScore(9, new Score(" Tempo record: §70s", "record1"));
		scoreboard.setScore(8, new Score("§eMédio: ", "top2"));
		scoreboard.setScore(7, new Score(" Passou: §a0", "pass2"));
		scoreboard.setScore(6, new Score(" Tempo record: §70s", "record2"));
		scoreboard.setScore(5, new Score("§aFácil: ", "top3"));
		scoreboard.setScore(4, new Score(" Passou: §a0", "pass3"));
		scoreboard.setScore(3, new Score(" Tempo record: §70s", "record3"));
		scoreboard.blankLine(2);
		scoreboard.setScore(1, new Score("§e" + CommonConst.SITE, "site"));

		challengeScoreboard = new SimpleScoreboard("§6§lLAVA CHALLENGE");

		challengeScoreboard.blankLine(6);
		challengeScoreboard.setScore(5, new Score("§aDesafio: §7", "mode"));
		challengeScoreboard.setScore(4, new Score(" Tempo atual: §a0", "time"));
		challengeScoreboard.setScore(3, new Score(" Tempo record: §c0", "record"));
		challengeScoreboard.blankLine(2);
		challengeScoreboard.setScore(1, new Score("§e" + CommonConst.SITE, "site"));
	}

	@EventHandler
	public void onPlayerStartChallenge(PlayerStartChallengeEvent event) {
		challengeScoreboard.createScoreboard(event.getPlayer());
		challengeScoreboard
				.updateScore(event.getPlayer(),
						new Score(
								" Tempo record: §c"
										+ StringUtils.formatTime(CommonGeneral.getInstance().getStatusManager()
												.loadStatus(event.getPlayer().getUniqueId(), StatusType.LAVA,
														ChallengeStatus.class)
												.getTime(ChallengeType.valueOf(event.getChallengeType().name()))),
								"record"));
		challengeScoreboard.updateScore(event.getPlayer(),
				new Score((event.getChallengeType() == ChallengeStage.EASY ? "§a"
						: event.getChallengeType() == ChallengeStage.MEDIUM ? "§e"
								: event.getChallengeType() == ChallengeStage.HARD ? "§c" : "§4")
						+ event.getChallengeType().getName() + ": ", "mode"));
	}

	@EventHandler
	public void onPlayerFinishChallenge(PlayerFinishChallengeEvent event) {
		scoreboard.createScoreboard(event.getPlayer());
		updateScore(event.getPlayer(), UpdateType.STATUS);
		challengeScoreboard.removeViewer((BukkitMember) CommonGeneral.getInstance().getMemberManager()
				.getMember(event.getPlayer().getUniqueId()));
	}

	@EventHandler
	public void onPlayerStopChallenge(PlayerStopChallengeEvent event) {
		scoreboard.createScoreboard(event.getPlayer());
		updateScore(event.getPlayer(), UpdateType.STATUS);
		challengeScoreboard.removeViewer((BukkitMember) CommonGeneral.getInstance().getMemberManager()
				.getMember(event.getPlayer().getUniqueId()));
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
		challengeScoreboard.removeViewer(member);
	}

	@Override
	public void updateScore(UpdateType updateType) {

	}

	@Override
	public void updateScore(Player player, UpdateType updateType) {
		switch (updateType) {
		case STATUS: {
			ChallengeStatus status = CommonGeneral.getInstance().getStatusManager().loadStatus(player.getUniqueId(),
					StatusType.LAVA, ChallengeStatus.class);

			scoreboard.updateScore(player, new Score(" Passou: §a" + status.getWins(ChallengeType.HARDCORE), "pass"));
			scoreboard.updateScore(player, new Score(
					" Tempo record: §7" + StringUtils.formatTime(status.getTime(ChallengeType.HARDCORE)), "record"));

			scoreboard.updateScore(player, new Score(" Passou: §a" + status.getWins(ChallengeType.HARD), "pass1"));
			scoreboard.updateScore(player, new Score(
					" Tempo record: §7" + StringUtils.formatTime(status.getTime(ChallengeType.HARD)), "record1"));

			scoreboard.updateScore(player, new Score(" Passou: §a" + status.getWins(ChallengeType.MEDIUM), "pass2"));
			scoreboard.updateScore(player, new Score(
					" Tempo record: §7" + StringUtils.formatTime(status.getTime(ChallengeType.MEDIUM)), "record2"));

			scoreboard.updateScore(player, new Score(" Passou: §a" + status.getWins(ChallengeType.EASY), "pass3"));
			scoreboard.updateScore(player, new Score(
					" Tempo record: §7" + StringUtils.formatTime(status.getTime(ChallengeType.EASY)), "record3"));
			break;
		}
		default:
			break;
		}
	}

	@Override
	public <T> void updateScore(Player player, T t) {
		if (t instanceof ChallengeInfo) {
			ChallengeInfo challengeInfo = (ChallengeInfo) t;

			int time = (int) ((challengeInfo.getLastDamage() - challengeInfo.getStartTime()) / 1000);
			scoreboard.updateScore(player, new Score(" Tempo atual: §a" + StringUtils.formatTime(time), "time"));
		}
	}

}
