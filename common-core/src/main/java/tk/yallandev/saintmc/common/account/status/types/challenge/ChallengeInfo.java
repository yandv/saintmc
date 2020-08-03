package tk.yallandev.saintmc.common.account.status.types.challenge;

import lombok.Getter;

@Getter
public class ChallengeInfo {

	private int attemps;
	private int wins;
	private int time;
	
	public void addAttemps() {
		attemps++;
	}
	
	public void addWins() {
		wins++;
	}
	
	public void setTime(int time) {
		this.time = time;
	}

}
