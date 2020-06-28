package tk.yallandev.saintmc.common.ban.constructor;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
public class Warn {
	
	private UUID uniqueId;
	@Setter
	private int id;
	
	private String warnedBy;
	private UUID warnedByUuid;
	private String reason;
	
	private long warnExpire;
	
	public boolean hasExpired() {
		return warnExpire < System.currentTimeMillis();
	}
	
}
