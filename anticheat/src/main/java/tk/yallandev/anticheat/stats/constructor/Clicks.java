package tk.yallandev.anticheat.stats.constructor;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class Clicks {
	
	private int clicks;
	private long expireTime = System.currentTimeMillis() + 1000;
	
	public void addClick() {
		clicks++;
	}
	
}