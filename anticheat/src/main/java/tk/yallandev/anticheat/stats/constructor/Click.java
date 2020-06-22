package tk.yallandev.anticheat.stats.constructor;

import lombok.Getter;

@Getter
public class Click {
	
	private long startTime = System.currentTimeMillis();
	
	private int click;
	private long lastClick;
	
	public void addClick() {
		this.click++;
		this.lastClick = System.currentTimeMillis();
	}

}
