package tk.yallandev.saintmc.game.event.game;

import tk.yallandev.saintmc.game.event.Event;
import tk.yallandev.saintmc.game.stage.GameStage;

public class GameStageChangeEvent extends Event {
	
	private GameStage lastStage;
	private GameStage newStage;

	public GameStageChangeEvent(GameStage lastStage, GameStage newStage) {
		this.lastStage = lastStage;
		this.newStage = newStage;
	}

	public GameStage getNewStage() {
		return newStage;
	}

	public GameStage getLastStage() {
		return lastStage;
	}

}
