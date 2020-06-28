package tk.yallandev.saintmc.bungee.bungee;

import java.util.UUID;

import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.clan.Clan;
import tk.yallandev.saintmc.common.clan.ClanModel;

public class BungeeClan extends Clan {
	
	public BungeeClan(UUID uniqueId, String clanName, String clanAbbreviation, Member owner) {
		super(uniqueId, clanName, clanAbbreviation, owner);
	}

	public BungeeClan(ClanModel clanModel) {
		super(clanModel);
	}

}
