package tk.yallandev.saintmc.common.clan;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.clan.enums.ClanHierarchy;
import tk.yallandev.saintmc.common.permission.Group;

@Getter
public class ClanInfo {

	private String playerName;
	private UUID playerId;

	@Setter
	private ClanHierarchy clanHierarchy;
	private Group group;

	private int xpEarned;

	public ClanInfo(Member member) {
		this(member, ClanHierarchy.MEMBER);
	}

	public ClanInfo(Member member, ClanHierarchy clanHierarchy) {
		this.playerName = member.getPlayerName();
		this.playerId = member.getUniqueId();
		this.clanHierarchy = clanHierarchy;
	}

	public boolean updateMember(Member member) {
		if (this.playerName.equals(member.getPlayerName()))
			return false;

		this.playerName = member.getPlayerName();
		this.group = member.getGroup();
		return true;
	}

	public Group getGroup() {
		return group == null ? Group.MEMBRO : group;
	}

}
