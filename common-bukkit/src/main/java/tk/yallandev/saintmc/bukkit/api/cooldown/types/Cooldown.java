package tk.yallandev.saintmc.bukkit.api.cooldown.types;

import java.util.concurrent.TimeUnit;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class Cooldown {

	@Getter
	@Setter
	@NonNull
	private String name;

	@Getter
	private long duration;
	@Getter
	private long startTime = System.currentTimeMillis();

	public Cooldown(String name, long duration) {
		this.name = name;
		this.duration = duration;
	}

	public void update(long duration, long startTime) {
		this.duration = duration;
		this.startTime = startTime;
	}

	public double getPercentage() {
		return (getRemaining() * 100) / duration;
	}

	public double getRemaining() {
		long endTime = startTime + TimeUnit.SECONDS.toMillis(duration);
		return (-(System.currentTimeMillis() - endTime)) / 1000D;
	}

	public boolean expired() {
		return getRemaining() < 0D;
	}
}
