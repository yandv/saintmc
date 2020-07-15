package tk.yallandev.saintmc.common.party;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import lombok.Getter;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.party.event.PartyEvent;
import tk.yallandev.saintmc.common.party.event.member.LeaderSwitchServerEvent;

@Getter
public abstract class Party {
	
	public static final String MESSAGE_PREFIX = "§9Party> ";
	public static final String PARTYCHAT_PREFIX = "§cChat> ";
	
	private UUID leaderId;
	private String leaderName;
	
	private Map<UUID, PartyInfo> membersMap;
	
	public Party(Member leader) {
		this.leaderId = leader.getUniqueId();
		this.leaderName = leader.getPlayerName();
		this.membersMap = new HashMap<>();
	}
	
	public <T extends PartyEvent> T callEvent(T clan) {
		onPartyEvent(clan);
		return clan;
	}

	public void onPartyEvent(PartyEvent event) {
		if (event instanceof LeaderSwitchServerEvent)
			onLeaderSwitchServer((LeaderSwitchServerEvent) event);
	}

	public void onLeaderSwitchServer(LeaderSwitchServerEvent event) {
	}

}
