package tk.yallandev.saintmc.bukkit.api.scoreboard;

import java.util.Set;

import org.bukkit.entity.Player;

import tk.yallandev.saintmc.bukkit.bukkit.BukkitMember;

/**
 * 
 * @author yandv
 *
 */

public interface Scoreboard extends Cloneable {
	
	/**
	 * Create the player scoreboard
	 * 
	 * @param player
	 */
	
	void createScoreboard(Player player);
	
	/**
	 * Set a line of scoreboard
	 * 
	 * @param player
	 */
	
	void setScore(int scoreLine, String scoreName, String teamName, String prefix, String suffix);
	
	/**
	 * Set a blank line of scoreboard
	 * 
	 * @param player
	 */
	
	void blankLine(int scoreLine);
	
	/**
	 * Clear the scoreboard
	 * 
	 * @param player
	 */
	
	void clear();
	
	/**
	 * Update the team of scoreboard
	 * 
	 * @param player
	 */
	
	void updateScore(String teamName, String prefix, String suffix);
	
	/**
	 * Update the team of player (no necessary in scoreboard)
	 * 
	 * @param player
	 */
	
	void updateScore(Player player, String teamName, String prefix, String suffix);
	
	/**
	 * Change the displayName of scoreboard
	 * 
	 * @param player
	 */
	
	void setDisplayName(String displayName);
	
	/**
	 * Remove the score
	 * 
	 * @param player
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
