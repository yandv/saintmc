package tk.yallandev.saintmc.bukkit.api.server.profile;

import java.util.UUID;

import org.bukkit.entity.Player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import tk.yallandev.saintmc.common.account.Member;

@Getter
@AllArgsConstructor
public class Profile {

	private String playerName;
	private UUID uniqueId;
	
	public static Profile fromPlayer(Player player) {
		return new Profile(player.getName(), player.getUniqueId());
	}
	
	public static Profile fromMember(Member member) {
		return new Profile(member.getPlayerName(), member.getUniqueId());
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
