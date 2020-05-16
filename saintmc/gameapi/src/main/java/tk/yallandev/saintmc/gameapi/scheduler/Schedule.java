package tk.yallandev.saintmc.gameapi.scheduler;

import tk.yallandev.saintmc.common.server.loadbalancer.server.MinigameState;
import tk.yallandev.saintmc.gameapi.games.GameType;

public interface Schedule {
	
	void pulse(GameType gameType, MinigameState gameState, int time);
	
	String getSchedulerName();
	
}
