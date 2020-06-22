package tk.yallandev.saintmc.kitpvp.warp.types;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import tk.yallandev.saintmc.kitpvp.GameMain;
import tk.yallandev.saintmc.kitpvp.event.party.PartyEndEvent;
import tk.yallandev.saintmc.kitpvp.warp.Warp;
import tk.yallandev.saintmc.kitpvp.warp.types.party.Party;
import tk.yallandev.saintmc.kitpvp.warp.types.party.PartyType;

@Getter
public class PartyWarp extends Warp {

	private PartyType partyType = PartyType.NONE;
	private Party party;

	public PartyWarp() {
		super("Party", new Location(Bukkit.getWorlds().stream().findFirst().orElse(null), 0, 180, 0));
	}

	public void setPartyType(PartyType partyType) {
		if (this.partyType != PartyType.NONE)
			HandlerList.unregisterAll(this.partyType.getParty());
		
		if (partyType == PartyType.RDM) {
			Bukkit.broadcastMessage("§6§lRDM §fO evento §aRei da mesa§f foi iniciado§f!");
			Bukkit.broadcastMessage("§6§lRDM §fEntre usando §e/party entrar§f para entrar no evento§f!");
		}

		this.partyType = partyType;
		this.party = partyType.getParty();
		Bukkit.getPluginManager().registerEvents(party, GameMain.getInstance());
	}

	@EventHandler
	public void onPartyEnd(PartyEndEvent event) {
		HandlerList.unregisterAll(this.party);
		this.partyType = PartyType.NONE;
		this.party = null;
	}

	@Override
	public ItemStack getItem() {
		return null;
	}

}
