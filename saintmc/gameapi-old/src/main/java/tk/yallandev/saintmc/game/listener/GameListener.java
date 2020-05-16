package tk.yallandev.saintmc.game.listener;

import org.bukkit.event.Listener;

import tk.yallandev.saintmc.game.GameMain;
import tk.yallandev.saintmc.game.stage.GameStage;

public abstract class GameListener implements Listener {

	private GameMain gameMain;

	public GameListener(GameMain main) {
		this.gameMain = main;
	}

	public GameMain getGameMain() {
		return gameMain;
	}

	public GameStage getStage() {
		return gameMain.getGameStage();
	}
}
