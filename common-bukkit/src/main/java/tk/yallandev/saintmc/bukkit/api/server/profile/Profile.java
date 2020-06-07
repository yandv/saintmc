package tk.yallandev.saintmc.bukkit.api.server.profile;

import java.util.UUID;

import org.bukkit.entity.Player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import tk.yallandev.saintmc.common.account.Member;

@Getter
@AllArgsConstructor
public class Profile implements Comparable<Profile> {

	private String playerName;
	private UUID uniqueId;
	
	@Override
	public int compareTo(Profile profile) {
		int x = playerName.compareTo(profile.getPlayerName());
		
		if (x == 0)
			return uniqueId.compareTo(profile.getUniqueId());
		
		return x;
	}
	
	public static Profile fromPlayer(Player player) {
		return new Profile(player.getName(), player.getUniqueId());
	}
	
	public static Profile fromMember(Member member) {
		return new Profile(member.getPlayerName(), member.getUniqueId());
	}
	
}
