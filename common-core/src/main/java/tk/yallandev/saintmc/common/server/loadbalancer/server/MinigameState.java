package tk.yallandev.saintmc.common.server.loadbalancer.server;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public enum MinigameState {

	PREGAME(true), WAITING(true), PREPARING(true), STARTING(true), INVINCIBILITY(true), INGAME, GAMETIME, ENDING, NONE;

	private boolean decrementTime;

	public boolean isPregame() {
		switch (this) {
		case STARTING:
		case WAITING:
		case PREPARING:
		case PREGAME:
			return true;
		default:
			return false;
		}
	}
	
	public boolean isInvencibility() {
		if (this == INVINCIBILITY)
			return true;
		return false;
	}
	
	public boolean isGametime() {
		if (this == GAMETIME || this == INGAME)
			return true;
		
		return false;
	}
	
	public boolean isEnding() {
		if (this == ENDING)
			return true;
		
		return false;
	}
	
	public boolean isState(MinigameState state) {
		return this == state;
	}

}
