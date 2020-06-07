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
import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.api.server.Server;
import tk.yallandev.saintmc.bukkit.api.server.chat.ChatState;
import tk.yallandev.saintmc.bukkit.api.server.profile.Profile;

public class StorableServer implements Server {
	
	private List<Profile> whiteList;
	private Map<Profile, Long> blackMap;
	
	private ChatState chatState = ChatState.DISABLED;
	
	@Getter
	private NPCPool npcPool;
	
	private boolean whitelist;
	
	public StorableServer() {
		whiteList = new ArrayList<>();
		blackMap = new HashMap<>();
		npcPool = new NPCPool(BukkitMain.getInstance());
	}
	
	public StorableServer(JavaPlugin javaPlugin) {
		whiteList = new ArrayList<>();
		blackMap = new HashMap<>();
		npcPool = new NPCPool(javaPlugin);
	}

	@Override
	public void addWhitelist(Profile profile) {
		if (!whiteList.contains(profile))
			whiteList.add(profile);
	}

	@Override
	public void removeWhitelist(Profile profile) {
		if (whiteList.contains(profile))
			whiteList.remove(profile);
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

	@Override
	public void setChatState(ChatState chatState) {
		this.chatState = chatState;
	}

	@Override
	public ChatState getChatState() {
		return chatState;
	}

}
