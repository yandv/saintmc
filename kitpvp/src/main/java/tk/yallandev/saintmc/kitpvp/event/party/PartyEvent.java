package tk.yallandev.saintmc.kitpvp.event.party;

import lombok.AllArgsConstructor;
import lombok.Getter;
import tk.yallandev.saintmc.bukkit.event.NormalEvent;
import tk.yallandev.saintmc.kitpvp.warp.types.party.Party;
import tk.yallandev.saintmc.kitpvp.warp.types.party.PartyType;

@Getter
@AllArgsConstructor
public class PartyEvent extends NormalEvent {
	
	private Party party;
	private PartyType partyType;

}
