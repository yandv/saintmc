package tk.yallandev.saintmc.bukkit.event.restore;

import java.util.List;

import lombok.Getter;
import tk.yallandev.saintmc.bukkit.api.server.profile.Profile;

@Getter
public class RestoreInitEvent extends RestoreEvent {
	
	private List<Profile> profileList;

	public RestoreInitEvent(List<Profile> profileList) {
		this.profileList = profileList;
	}

}
