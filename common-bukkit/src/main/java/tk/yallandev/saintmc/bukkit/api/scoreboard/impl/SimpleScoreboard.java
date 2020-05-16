package tk.yallandev.saintmc.bukkit.api.scoreboard.impl;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Team;

import lombok.Getter;
import lombok.Setter;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.account.BukkitMember;
import tk.yallandev.saintmc.bukkit.api.scoreboard.Score;
import tk.yallandev.saintmc.bukkit.api.scoreboard.Scoreboard;

/**
 * 
 * SimpleScoreboard helps when create a scoreboard
 * 
 * @author Allan
 *
 */

@Getter
@Setter
public class SimpleScoreboard implements Scoreboard {

	private int id;
	private String displayName;

	private Map<Integer, Score> scoreList;
	private Set<BukkitMember> playerList;

	public SimpleScoreboard(String displayName) {
		this.displayName = displayName;
		this.scoreList = new TreeMap<>();
		this.playerList = new HashSet<>();
	}

	public void createScoreboard(Player player) {
		org.bukkit.scoreboard.Scoreboard scoreboard = player.getScoreboard();

		scoreboard.clearSlot(DisplaySlot.SIDEBAR);

		Objective objective = scoreboard.getObjective("mainScoreboard");

		if (objective != null)
			objective.unregister();

		objective = scoreboard.registerNewObjective("mainScoreboard", "dummy");

		objective.setDisplayName(displayName);
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);

		for (Entry<Integer, Score> entry : scoreList.entrySet()) {
			Team team = scoreboard.getTeam(entry.getValue().getTeamName());

			if (team == null)
				team = scoreboard.registerNewTeam(entry.getValue().getTeamName());
			
			team.addEntry(entry.getValue().getScoreName());
			team.setPrefix(entry.getValue().getPrefix());
			team.setSuffix(entry.getValue().getSuffix());

			objective.getScore(entry.getValue().getScoreName()).setScore(entry.getKey());
		}

		BukkitMember member = (BukkitMember) CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId());

		member.setScoreboard(this);
		addViewer(member);
	}

	@Override
	public void blankLine(int scoreLine) {
		String string = String.valueOf(scoreLine);
		String[] split = string.split("");
		String teamName = scoreLine >= 10 ? "§" + split[0] + "§" + split[1] : "§" + scoreLine;

		scoreList.put(scoreLine, new Score(teamName, "bl-" + scoreLine));
	}

	@Override
	public void setScore(int scoreLine, String scoreName, String teamName, String prefix, String suffix) {
		scoreList.put(scoreLine, new Score(teamName, scoreName, prefix, suffix) {

			@Override
			public void setPrefix(String prefix) {
				super.setPrefix(prefix);
				updateScore(scoreName, prefix, suffix);
			}

			@Override
			public void setSuffix(String suffix) {
				super.setSuffix(suffix);
				updateScore(scoreName, prefix, suffix);
			}

		});
	}

	@Override
	public void updateScore(String teamName, String prefix, String suffix) {
		for (BukkitMember member : getViewerList()) {
			Player player = member.getPlayer();

			Team team = player.getScoreboard().getTeam(teamName);

			if (team != null) {
				if (prefix != null)
					team.setPrefix(prefix);
				if (suffix != null)
					team.setSuffix(suffix);
			}
		}
	}

	@Override
	public void updateScore(Player player, String teamName, String prefix, String suffix) {
		Team team = player.getScoreboard().getTeam(teamName);

		if (team != null) {
			if (prefix != null)
				team.setPrefix(prefix);
			if (suffix != null)
				team.setSuffix(suffix);
		}
	}

	@Override
	public void removeScore(int scoreId) {
		scoreList.remove(scoreId);
	}

	@Override
	public void setDisplayName(String displayName) {
		this.displayName = displayName;

		for (BukkitMember storage : getViewerList()) {
			Player player = storage.getPlayer();

			Objective objective = player.getScoreboard().getObjective("mainScoreboard");

			if (objective != null)
				objective.setDisplayName(displayName);
		}
	}

	@Override
	public int getScoreboardId() {
		return id;
	}

	@Override
	public Set<BukkitMember> getViewerList() {
		return playerList;
	}

	@Override
	public void addViewer(BukkitMember member) {
		playerList.add(member);
	}

	@Override
	public void removeViewer(BukkitMember member) {
		playerList.remove(member);
	}

	public SimpleScoreboard clone() {
		SimpleScoreboard scoreboard = new SimpleScoreboard(getDisplayName());
		
		scoreboard.setId(getId() + 1);
		scoreboard.setScoreList(getScoreList());
		scoreboard.setPlayerList(getPlayerList());
		
		return scoreboard;
	}

}
