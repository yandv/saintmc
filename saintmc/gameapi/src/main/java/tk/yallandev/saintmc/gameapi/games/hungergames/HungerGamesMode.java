package tk.yallandev.saintmc.gameapi.games.hungergames;

import tk.yallandev.saintmc.gameapi.GameMain;
import tk.yallandev.saintmc.gameapi.GameMode;
import tk.yallandev.saintmc.gameapi.games.GameType;

public class HungerGamesMode extends GameMode {
	
	public HungerGamesMode(GameMain main) {
		super(main, GameType.HUNGERGAMES);
	}

	private static int minimumPlayer = 5;
	private static int invincibilityTime = 120;
	
	public static int feastTime = 17 * 60 + 30;
	public static int bonusTime = 30 * 60;	
	public static int finalTime = 45 * 60;
	
	@Override
	public void startGame() {
		
	}

}
