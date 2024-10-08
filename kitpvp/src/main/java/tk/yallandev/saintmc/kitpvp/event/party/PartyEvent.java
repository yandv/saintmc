package tk.yallandev.saintmc.kitpvp.event.party;

import lombok.AllArgsConstructor;
import lombok.Getter;
import tk.yallandev.saintmc.bukkit.event.NormalEvent;
import tk.yallandev.saintmc.kitpvp.party.Party;
import tk.yallandev.saintmc.kitpvp.party.PartyType;

@Getter
@AllArgsConstructor
public class PartyEvent extends NormalEvent {
	
	private Party party;
	private PartyType partyType;

}
