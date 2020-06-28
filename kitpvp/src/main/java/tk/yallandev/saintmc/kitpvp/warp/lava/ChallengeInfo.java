package tk.yallandev.saintmc.kitpvp.warp.lava;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChallengeInfo {

	private boolean start;
	private long startTime;
	private long lastDamage;
	
	private boolean finished;

	public void start() {
		start = true;
		startTime = System.currentTimeMillis();
	}

	public boolean isRunning() {
		return start;
	}

}