package tk.yallandev.saintmc.common.party.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import tk.yallandev.saintmc.common.party.Party;

@Getter
@AllArgsConstructor
public abstract class PartyEvent {
	
	private Party clan;
	
}
