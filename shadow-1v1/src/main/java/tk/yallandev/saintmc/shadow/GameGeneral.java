package tk.yallandev.saintmc.shadow;

import lombok.Getter;
import tk.yallandev.saintmc.shadow.controller.ChallengeController;

@Getter
public class GameGeneral {
	
	@Getter
	private static GameGeneral instance;
	
	private ChallengeController challengeController;
	
	public GameGeneral() {
		instance = this;
	}

	public void onLoad() {
	}

	public void onEnable() {
		challengeController = new ChallengeController();
	}

	public void onDisable() {
		
	}

}
