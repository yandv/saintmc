package br.com.saintmc.hungergames.game;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
public enum GameState {
	
	WAITING(90),
	PREGAME(300),
	STARTING(15),
	
	INVINCIBILITY(120),
	
	GAMETIME(120, true),
	
	WINNING(15);
	
	@Setter
	private int defaultTime;
	private boolean upTime;
	
	GameState(int defaultTime) {
		this.defaultTime = defaultTime;
		this.upTime = false;
	}

	public boolean isPregame() {
		switch (this) {
		case PREGAME:
		case STARTING:
		case WAITING: {
			return true;
		}
		default:
			return false;
		}
	}
	
	public boolean isInvenciblity() {
		return this == INVINCIBILITY;
	}
	
	public static boolean isPregame(GameState gameState) {
		return gameState.isPregame();
	}

	public static boolean isInvincibility(GameState gameState) {
		return gameState == INVINCIBILITY;
	}

}
