package tk.yallandev.saintmc.common.ban.constructor;

import java.util.UUID;

import lombok.Getter;

@Getter
public class Ban {
	
	private UUID uniqueId;
	private int id = 0;
	
	private String bannedBy;
	private UUID bannedByUuid;
	private String reason;
	
	private long banTime;
	private long banExpire;
	private long banDuration;
	
	private boolean unbanned;
	private String unbannedBy;
	private UUID unbannedByUuid;
	private UnbanReason unbanReason;
	
	public Ban(UUID uniqueId, String bannedBy, UUID bannedByUuid, String reason, long banExpire) {
		this.uniqueId = uniqueId;
		this.bannedBy = bannedBy;
		this.bannedByUuid = bannedByUuid;
		this.reason = reason;
		
		this.banTime = System.currentTimeMillis();
		this.banExpire = banExpire;
		this.banDuration = System.currentTimeMillis() - banExpire;
	}
	
	public boolean hasExpired() {
		return !isPermanent() && banExpire < System.currentTimeMillis();
	}
	
	public boolean isPermanent() {
		return banExpire == -1l;
	}
	
	public boolean isUnbanned() {
		return unbanned;
	}

	public void unban(UUID uniqueId, String userName, UnbanReason unbanReason) {
		unbannedBy = userName;
		unbannedByUuid = uniqueId;
		unbanned = true;
		this.unbanReason = unbanReason;
	}
	
	public enum UnbanReason {
		
		APPEAL, OTHER;
		
	}

}
