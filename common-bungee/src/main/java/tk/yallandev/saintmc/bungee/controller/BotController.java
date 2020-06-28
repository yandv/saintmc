package tk.yallandev.saintmc.bungee.controller;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import lombok.Getter;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bungee.bungee.BotMember;

public class BotController {

	private LoadingCache<String, BotMember> cache;
	@Getter
	private Set<String> blockedAddress;

	public BotController() {
		cache = CacheBuilder.newBuilder().expireAfterWrite(60L, TimeUnit.MINUTES)
				.build(new CacheLoader<String, BotMember>() {

					@Override
					public BotMember load(String key) throws Exception {
						return new BotMember(key);
					}

				});
		blockedAddress = new HashSet<>();
	}

	public void blockIp(String string) {
		blockedAddress.add(string);
	}

	public void removeIp(String string) {
		blockedAddress.remove(string);
	}

	public void clean() {
		cache.cleanUp();
	}

	public BotMember getBotMember(String playerName) {
		try {
			return cache.get(playerName);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		CommonGeneral.getInstance().debug("Error ocurred when loading " + playerName);
		return new BotMember(playerName);
	}
	
	public void removeBot(String ipAddress) {
		cache.asMap().remove(ipAddress);
	}

	public boolean containsKey(String ipAddress) {
		return cache.asMap().containsKey(ipAddress);
	}

}
