package tk.yallandev.saintmc.common.clan;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ClanInvite {
	
	private UUID clanId;
	private String clanName;
	
	private UUID playerId;
	private String playerName;
	
	private long expireTime;
	
	public boolean hasExpired() {
		return expireTime < System.currentTimeMillis();
	}
	
}
