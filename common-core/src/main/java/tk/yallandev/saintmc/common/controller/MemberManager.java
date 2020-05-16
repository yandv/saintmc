package tk.yallandev.saintmc.common.controller;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.account.MemberModel;
import tk.yallandev.saintmc.common.permission.Group;

public class MemberManager {

	private HashMap<UUID, Member> memberMap;

	public MemberManager() {
		memberMap = new HashMap<>();
	}

	public void loadMember(Member account) {
		memberMap.put(account.getUniqueId(), account);
	}

	public void unloadMember(UUID uuid) {
		if (memberMap.containsKey(uuid))
			memberMap.remove(uuid);
	}

	public Member getMember(UUID uuid) {
		return memberMap.get(uuid);
	}

	public Member getMember(String playerName) {
		return memberMap.values().stream().filter(member -> member.getPlayerName().equalsIgnoreCase(playerName))
				.findFirst().orElse(null);
	}

	public Member getMember(long discordId) {
		return memberMap.values().stream().filter(member -> member.getDiscordId() == discordId).findFirst()
				.orElse(null);
	}

	public MemberModel getMemberAsMemberModel(UUID uuid) {
		Member member = getMember(uuid);
		return member != null ? new MemberModel(member) : null;
	}

	public MemberModel getMemberAsMemberModel(long discordId) {
		Member member = getMember(discordId);
		return member != null ? new MemberModel(member) : null;
	}
	
	public void broadcast(String message) {
		getMembers().forEach(member -> member.sendMessage(message));
	}

	public void broadcast(String message, Group group) {
		getMembers().stream().filter(member -> member.hasGroupPermission(group))
				.forEach(member -> member.sendMessage(message));
	}

	public void broadcast(String message, List<Group> groupList) {
		getMembers().stream().filter(member -> groupList.contains(member.getGroup()))
				.forEach(member -> member.sendMessage(message));
	}

	public HashMap<UUID, Member> getMemberMap() {
		return memberMap;
	}

	public Collection<Member> getMembers() {
		return memberMap.values();
	}

}
