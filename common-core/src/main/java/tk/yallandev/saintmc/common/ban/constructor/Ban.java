package tk.yallandev.saintmc.common.ban.constructor;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.common.ban.Category;

@Getter
public class Ban {

	private Category category;
	private UUID uniqueId;
	private String playerName;
	
	@Setter
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

	public Ban(Category category, UUID uniqueId, String playerName, String bannedBy, UUID bannedByUuid, String reason, long banExpire) {
		this.category = category;
		this.uniqueId = uniqueId;
		this.playerName = playerName;
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

		CommonGeneral.getInstance().getPunishData().updateBan(this, "unbannedBy");
		CommonGeneral.getInstance().getPunishData().updateBan(this, "unbannedByUuid");
		CommonGeneral.getInstance().getPunishData().updateBan(this, "unbanned");
		CommonGeneral.getInstance().getPunishData().updateBan(this, "unbanReason");
	}

	public enum UnbanReason {

		APPEAL, OTHER;

	}

}
