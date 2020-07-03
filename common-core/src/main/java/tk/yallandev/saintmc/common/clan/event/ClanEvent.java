package tk.yallandev.saintmc.common.clan.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import tk.yallandev.saintmc.common.clan.Clan;

@Getter
@AllArgsConstructor
public abstract class ClanEvent {
	
	private Clan clan;
	
}
