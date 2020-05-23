package tk.yallandev.saintmc.game.constructor;

import tk.yallandev.saintmc.game.GameType;
import tk.yallandev.saintmc.game.stage.GameStage;

public class ScheduleArgs {
	
	private GameType gameType;
	private GameStage stage;
	private int timer;

	public ScheduleArgs(GameType gameType, GameStage stage, int timer) {
		this.gameType = gameType;
		this.stage = stage;
		this.timer = timer;
	}

	public GameType getGameType() {
		return gameType;
	}

	public GameStage getStage() {
		return stage;
	}

	public int getTimer() {
		return timer;
	}

}
