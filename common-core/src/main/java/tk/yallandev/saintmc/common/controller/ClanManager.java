package tk.yallandev.saintmc.common.controller;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import tk.yallandev.saintmc.common.clan.Clan;
import tk.yallandev.saintmc.common.clan.ClanModel;

public class ClanManager extends StoreController<UUID, Clan> {

	public Clan getClan(UUID key) {
		return super.getValue(key);
	}

	public Clan getClan(String clanName, boolean ignoreCase) {
		return getStoreMap().values().stream()
				.filter(clan -> (ignoreCase ? clan.getClanName().equalsIgnoreCase(clanName)
						: clan.getClanName().equals(clanName)))
				.findFirst().orElse(null);
	}

	public Clan getClanByAbbreviation(String clanAbbreviation, boolean ignoreCase) {
		return getStoreMap().values().stream()
				.filter(clan -> (ignoreCase ? clan.getClanAbbreviation().equalsIgnoreCase(clanAbbreviation)
						: clan.getClanAbbreviation().equals(clanAbbreviation)))
				.findFirst().orElse(null);
	}

	public void loadClan(UUID key, Clan value) {
		super.load(key, value);
	}

	public boolean unloadClan(UUID key) {
		return super.unload(key);
	}

	public ClanModel getClanAsModel(UUID uniqueId) {
		if (containsKey(uniqueId))
			return new ClanModel(getClan(uniqueId));

		return null;
	}

	public ClanModel getClanAsModel(String clanName, boolean ignoreCase) {
		Optional<Clan> optinal = getStoreMap().values().stream()
				.filter(clan -> (ignoreCase ? clan.getClanName().equalsIgnoreCase(clanName)
						: clan.getClanName().equals(clanName)))
				.findFirst();

		if (optinal.isPresent())
			return new ClanModel(optinal.get());

		return null;
	}

	public ClanModel getClanAsModelByAbbreviation(String clanAbbreviation, boolean ignoreCase) {
		Optional<Clan> optinal = getStoreMap().values().stream()
				.filter(clan -> (ignoreCase ? clan.getClanAbbreviation().equalsIgnoreCase(clanAbbreviation)
						: clan.getClanAbbreviation().equals(clanAbbreviation)))
				.findFirst();

		if (optinal.isPresent())
			return new ClanModel(optinal.get());

		return null;
	}

	public Collection<? extends Clan> getClans() {
		return getStoreMap().values();
	}

}
