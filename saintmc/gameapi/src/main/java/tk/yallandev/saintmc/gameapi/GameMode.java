package tk.yallandev.saintmc.gameapi;

import org.bukkit.Server;

import tk.yallandev.saintmc.gameapi.games.GameType;

public abstract class GameMode {

	private GameMain gameMain;
	private GameType gameType;

	public GameMode(GameMain main, GameType gameType) {
		this.gameMain = main;
		this.gameType = gameType;
	}

	public void onLoad() {

	}

	public void onEnable() {

	}

	public void onDisable() {

	}

	public abstract void startGame();

	public GameMain getGameMain() {
		return gameMain;
	}

	public Server getServer() {
		return gameMain.getServer();
	}

	public GameType getGameType() {
		return gameType;
	}

}
