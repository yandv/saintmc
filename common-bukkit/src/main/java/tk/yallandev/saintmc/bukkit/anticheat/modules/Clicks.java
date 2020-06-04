package tk.yallandev.saintmc.bukkit.anticheat.modules;

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