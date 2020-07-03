package tk.yallandev.saintmc.common.clan.event;

import java.util.UUID;

import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.clan.Clan;
import tk.yallandev.saintmc.common.clan.ClanModel;

public class ClanVoid extends Clan {
	
	public ClanVoid(UUID uniqueId, String clanName, String clanAbbreviation, Member owner) {
		super(uniqueId, clanName, clanAbbreviation, owner);
	}

	public ClanVoid(ClanModel clanModel) {
		super(clanModel);
	}


}
