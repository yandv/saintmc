package tk.yallandev.saintmc.common.clan;

import java.util.Map;
import java.util.UUID;

import lombok.Getter;
import tk.yallandev.saintmc.common.clan.enums.ClanRank;

@Getter
public class ClanModel {

	private UUID uniqueId;

	private String clanName;
	private String clanAbbreviation;

	private Map<UUID, ClanInfo> memberMap;

	private ClanRank clanRank;
	private int xp;

	public ClanModel(Clan clan) {
		this.uniqueId = clan.getUniqueId();
		this.clanName = clan.getClanName();
		this.clanAbbreviation = clan.getClanAbbreviation();
		this.memberMap = clan.getMemberMap();
		this.clanRank = clan.getClanRank();
		this.xp = clan.getXp();
	}

}
