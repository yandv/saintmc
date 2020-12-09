package tk.yallandev.saintmc.kitpvp.event.party;

import org.bukkit.entity.Player;

import lombok.Getter;
import tk.yallandev.saintmc.kitpvp.party.Party;
import tk.yallandev.saintmc.kitpvp.party.PartyType;

@Getter
public class PartyEndEvent extends PartyEvent {
	
	private Player winner;

	public PartyEndEvent(Party party, PartyType partyType, Player winner) {
		super(party, partyType);
		this.winner = winner;
	}

}
