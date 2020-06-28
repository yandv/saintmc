package tk.yallandev.anticheat.stats;

import java.util.UUID;

import lombok.Getter;
import tk.yallandev.anticheat.stats.constructor.Click;

@Getter
public class PlayerStats {

	private UUID uniqueId;

	private Click click = new Click();

	private long lastHit;
	private long lastClick;

	public PlayerStats(UUID uniqueId) {
		this.uniqueId = uniqueId;
	}

	public void handleClick() {
		this.lastClick = System.currentTimeMillis();
		this.click.addClick();
	}

	public void handleHit(boolean click) {
		if (click)
			this.click.addClick();

		this.lastHit = System.currentTimeMillis();
	}
}
