package tk.yallandev.saintmc.kitpvp.party;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tk.yallandev.saintmc.kitpvp.party.types.RdmParty;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum PartyType {
	
	RDM(new RdmParty()), NONE;
	
	private Party party;
	
}
