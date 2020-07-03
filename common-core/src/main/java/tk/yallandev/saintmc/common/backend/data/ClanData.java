package tk.yallandev.saintmc.common.backend.data;

import java.util.Collection;
import java.util.UUID;

import tk.yallandev.saintmc.common.clan.Clan;
import tk.yallandev.saintmc.common.clan.ClanModel;

public interface ClanData {
	
	void createClan(Clan clan);
	
	ClanModel loadClan(UUID uniqueId);
	
	ClanModel loadClan(String clanName);
	
	ClanModel loadClanByAbbreviation(String clanAbbreviation);
	
	void updateClan(Clan clan, String fieldName);
	
	void deleteClan(Clan clan);
	
	Collection<ClanModel> ranking(String fieldName, int limit, int order);
	
	default Collection<ClanModel> ranking(String fieldName) {
		return ranking(fieldName, 10, -1);
	}

}
