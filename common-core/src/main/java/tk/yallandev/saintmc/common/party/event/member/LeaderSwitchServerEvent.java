package tk.yallandev.saintmc.common.party.event.member;

import lombok.Getter;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.party.Party;
import tk.yallandev.saintmc.common.party.event.PartyEvent;

@Getter
public class LeaderSwitchServerEvent extends PartyEvent {
	
	private Member leader;

	public LeaderSwitchServerEvent(Party clan, Member leader) {
		super(clan);
		this.leader = leader;
	}

}
