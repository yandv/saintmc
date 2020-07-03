package tk.yallandev.saintmc.common.clan.event.member;

import java.util.List;

import lombok.Getter;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.clan.Clan;
import tk.yallandev.saintmc.common.clan.event.ClanEvent;

@Getter
public class MemberChatEvent extends ClanEvent {

	private Member member;
	private List<Member> recipients;
	private String message;

	public MemberChatEvent(Clan clan, Member member, List<Member> recipients, String message) {
		super(clan);
		this.member = member;
		this.recipients = recipients;
		this.message = message;
	}

}
