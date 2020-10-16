package tk.yallandev.saintmc.common.utils.ip;

import lombok.Getter;

@Getter
public class Session {

	private static final long EXPIRE_TIME = 1000 * 60 * 60 * 24 * 7l;

	private long sessionTime;

	public Session() {
		this.sessionTime = System.currentTimeMillis();
	}

	public void updateSession() {
		this.sessionTime = System.currentTimeMillis();
	}

	public long getExpireTime() {
		return sessionTime + EXPIRE_TIME;
	}

	public boolean hasExpired() {
		return sessionTime + EXPIRE_TIME < System.currentTimeMillis();
	}

}
