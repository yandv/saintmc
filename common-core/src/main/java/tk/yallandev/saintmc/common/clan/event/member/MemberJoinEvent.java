package tk.yallandev.saintmc.common.clan.event.member;

import lombok.Getter;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.clan.Clan;
import tk.yallandev.saintmc.common.clan.event.ClanEvent;

@Getter
public class MemberJoinEvent extends ClanEvent {
	
	private Member member;

	public MemberJoinEvent(Clan clan, Member member) {
		super(clan);
		this.member = member;
	}

}
