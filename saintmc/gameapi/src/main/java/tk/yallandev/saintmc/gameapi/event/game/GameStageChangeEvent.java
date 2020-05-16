package tk.yallandev.saintmc.gameapi.event.game;

import tk.yallandev.saintmc.common.server.loadbalancer.server.MinigameState;
import tk.yallandev.saintmc.gameapi.event.Event;

public class GameStageChangeEvent extends Event {
	
	private MinigameState lastStage;
	private MinigameState newStage;

	public GameStageChangeEvent(MinigameState lastState, MinigameState newState) {
		this.lastStage = lastState;
		this.newStage = newState;
	}

	public MinigameState getNewState() {
		return newStage;
	}

	public MinigameState getLastState() {
		return lastStage;
	}

}
