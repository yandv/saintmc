package tk.yallandev.saintmc.bukkit.api.scoreboard.impl;

import java.util.Set;

import org.bukkit.entity.Player;

import tk.yallandev.saintmc.bukkit.account.BukkitMember;
import tk.yallandev.saintmc.bukkit.api.scoreboard.Scoreboard;

/**
 * AnimatedScoreboard with
 * 
 * @author Allan
 *
 */

public class AnimatedScoreboard implements Scoreboard {

	@Override
	public void createScoreboard(Player player) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void clear() {
		
	}

	@Override
	public void setScore(int scoreLine, String scoreName, String teamName, String prefix, String suffix) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void blankLine(int scoreLine) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateScore(String teamName, String prefix, String suffix) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateScore(Player player, String teamName, String prefix, String suffix) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDisplayName(String displayName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeScore(int scoreLine) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getScoreboardId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void addViewer(BukkitMember member) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeViewer(BukkitMember member) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Set<BukkitMember> getViewerList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Scoreboard clone() {
		// TODO Auto-generated method stub
		return null;
	}

}
