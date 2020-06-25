package tk.yallandev.saintmc.bukkit.api.server.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.plugin.java.JavaPlugin;

import com.github.juliarn.npc.NPCPool;

import lombok.Getter;
import lombok.Setter;
import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.api.server.Server;
import tk.yallandev.saintmc.bukkit.api.server.chat.ChatState;
import tk.yallandev.saintmc.common.profile.Profile;

public class ServerImpl implements Server {
	
	private List<Profile> whiteList;
	private Map<Profile, Long> blackMap;
	
	@Getter
	@Setter
	private ChatState chatState = ChatState.ENABLED;
	
	@Getter
	private NPCPool npcPool;
	
	private boolean whitelist;
	@Getter
	@Setter
	private boolean restoreMode;
	
	public ServerImpl() {
		whiteList = new ArrayList<>();
		blackMap = new HashMap<>();
		npcPool = new NPCPool(BukkitMain.getInstance());
	}
	
	public ServerImpl(JavaPlugin javaPlugin) {
		whiteList = new ArrayList<>();
		blackMap = new HashMap<>();
		npcPool = new NPCPool(javaPlugin);
	}

	@Override
	public boolean addWhitelist(Profile profile) {
		if (!whiteList.contains(profile)) {
			whiteList.add(profile);
			return true;
		}
		return false;
	}

	@Override
	public boolean removeWhitelist(Profile profile) {
		if (whiteList.contains(profile)) {
			whiteList.remove(profile);
			return true;
		}
		return false;
	}
	
	@Override
	public boolean hasWhitelist(Profile profile) {
		return whiteList.contains(profile);
	}

	@Override
	public boolean isWhitelist() {
		return whitelist;
	}
	
	@Override
	public void setWhitelist(boolean whitelistState) {
		this.whitelist = whitelistState;
	}

	@Override
	public List<Profile> getWhiteList() {
		return Collections.unmodifiableList(whiteList);
	}

	@Override
	public boolean isBlackedlist(Profile profile) {
		if (blackMap.containsKey(profile))
			if (blackMap.get(profile) > System.currentTimeMillis())
				return true;
			else
				unblacklist(profile);
		
		return false;
	}
	
	@Override
	public long getBlacklistTime(Profile profile) {
		return isBlackedlist(profile) ? blackMap.get(profile) : -1;
	}

	@Override
	public void blacklist(Profile profile, long time) {
		if (!blackMap.containsKey(profile))
			blackMap.put(profile, time);
	}
	
	@Override
	public void unblacklist(Profile profile) {
		if (blackMap.containsKey(profile))
			blackMap.remove(profile);
	}

	@Override
	public Collection<Profile> getBlackList() {
		return Collections.unmodifiableSet(blackMap.keySet());
	}

}
