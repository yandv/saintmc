package tk.yallandev.saintmc.bukkit.api.scoreboard;

import java.util.Set;

import org.bukkit.entity.Player;

import tk.yallandev.saintmc.bukkit.account.BukkitMember;

public interface Scoreboard extends Cloneable {
	
	void createScoreboard(Player player);
	
	/*
	 * Line
	 */
	
	void setScore(int scoreLine, String scoreName, String teamName, String prefix, String suffix);
	
	void blankLine(int scoreLine);
	
	/*
	 * update
	 */
	
	void updateScore(String teamName, String prefix, String suffix);
	
	void updateScore(Player player, String teamName, String prefix, String suffix);
	
	void setDisplayName(String displayName);
	
	/*
	 * Remove
	 */
	
	void removeScore(int scoreLine);
	
	/*
	 * Scoreboard
	 */
	
	int getScoreboardId();
	
	void addViewer(BukkitMember member);
	
	void removeViewer(BukkitMember member);
	
	Set<BukkitMember> getViewerList();
	
	Scoreboard clone();
	
	/*
	 * Default
	 */
	
	default void setScore(int scoreId, Score score) {
		setScore(scoreId, score.getScoreName(), score.getTeamName(), score.getPrefix(), score.getSuffix());
	};
	
	default void updateScore(Score score) {
		updateScore(score.getTeamName(), score.getPrefix(), score.getSuffix());
	};
	
	default void updateScore(Player player, Score score) {
		updateScore(player, score.getTeamName(), score.getPrefix(), score.getSuffix());
	}

}
