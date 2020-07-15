package tk.yallandev.saintmc.common.clan;

import java.util.UUID;

import tk.yallandev.saintmc.common.account.Member;

public class ClanVoid extends Clan {
	
	public ClanVoid(UUID uniqueId, String clanName, String clanAbbreviation, Member owner) {
		super(uniqueId, clanName, clanAbbreviation, owner);
	}

	public ClanVoid(ClanModel clanModel) {
		super(clanModel);
	}


}
