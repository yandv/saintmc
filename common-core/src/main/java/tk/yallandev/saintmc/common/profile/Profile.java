package tk.yallandev.saintmc.common.profile;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import tk.yallandev.saintmc.common.account.Member;

@Getter
@AllArgsConstructor
public class Profile {

	private String playerName;
	private UUID uniqueId;
	
	public static Profile fromMember(Member member) {
		return new Profile(member.getPlayerName(), member.getUniqueId());
	}
	
	public boolean equals(String playerName) {
		return this.playerName.equals(playerName);
	}
	
	public boolean equals(UUID uniqueId) {
		return this.uniqueId.equals(uniqueId);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Profile) {
			Profile profile = (Profile) obj;
			
			return profile.getUniqueId().equals(uniqueId) || profile.getPlayerName().equals(playerName);
		}
		
		return super.equals(obj);
	}

}
