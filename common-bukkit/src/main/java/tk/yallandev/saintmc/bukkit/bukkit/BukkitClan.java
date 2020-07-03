package tk.yallandev.saintmc.bukkit.bukkit;

import java.util.UUID;

import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.clan.Clan;
import tk.yallandev.saintmc.common.clan.ClanModel;

public class BukkitClan extends Clan {
	
	public BukkitClan(UUID uniqueId, String clanName, String clanAbbreviation, Member owner) {
		super(uniqueId, clanName, clanAbbreviation, owner);
	}

	public BukkitClan(ClanModel clanModel) {
		super(clanModel);
	}
	
}
