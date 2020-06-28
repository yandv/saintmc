package tk.yallandev.saintmc.common.clan.event.member;

import lombok.Getter;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.clan.Clan;
import tk.yallandev.saintmc.common.clan.event.ClanEvent;

@Getter
public class MemberOnlineEvent extends ClanEvent {
	
	private Member member;
	private boolean online;
	
	public MemberOnlineEvent(Clan clan, Member member, boolean online) {
		super(clan);
		this.member = member;
		this.online = online;
	}

}
