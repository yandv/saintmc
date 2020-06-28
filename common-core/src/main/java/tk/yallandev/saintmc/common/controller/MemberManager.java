package tk.yallandev.saintmc.common.controller;

import java.util.*;
import java.util.function.Predicate;

import net.md_5.bungee.api.chat.TextComponent;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.account.MemberModel;
import tk.yallandev.saintmc.common.permission.Group;

public class MemberManager extends StoreController<UUID, Member> {

	public void loadMember(Member account) {
		load(account.getUniqueId(), account);
	}

	public void unloadMember(UUID uuid) {
		unload(uuid);
	}

	public Member getMember(UUID uuid) {
		return getValue(uuid);
	}

	public Member getMember(String playerName) {
		return getStoreMap().values().stream().filter(member -> member.getPlayerName().equalsIgnoreCase(playerName))
				.findFirst().orElse(null);
	}

	public Member getMember(long discordId) {
		return getStoreMap().values().stream().filter(member -> member.getDiscordId() == discordId).findFirst()
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

		System.out.println(message);
	}

	public void broadcast(String message, Predicate<Member> predicate) {
		getMembers().stream().filter(predicate).forEach(member -> member.sendMessage(message));

		System.out.println(message);
	}

	public void broadcast(String message, Group group) {
		getMembers().stream().filter(member -> member.hasGroupPermission(group))
				.forEach(member -> member.sendMessage(message));

		System.out.println(message);
	}

	public void broadcast(TextComponent message, Group group) {
		getMembers().stream().filter(member -> member.hasGroupPermission(group))
				.forEach(member -> member.sendMessage(message));

		System.out.println(message);
	}

	public void broadcast(TextComponent message, Predicate<Member> predicate) {
		getMembers().stream().filter(predicate).forEach(member -> member.sendMessage(message));

		System.out.println(message);
	}

	public void broadcast(String message, List<Group> groupList) {
		getMembers().stream().filter(member -> groupList.contains(member.getGroup()))
				.forEach(member -> member.sendMessage(message));

		System.out.println(message);
	}

	public Map<UUID, Member> getMemberMap() {
		return getStoreMap();
	}

	public Collection<Member> getMembers() {
		return getStoreMap().values();
	}

	public Member getMemberByFake(String fakeName) {
		return getStoreMap().values().stream().filter(member -> member.getFakeName().equals(fakeName)).findFirst()
				.orElse(null);
	}

    public boolean hasServerGroup(UUID uniqueId, Group group) {
		return Optional.ofNullable(getMember(uniqueId)).map(member -> member.hasGroupPermission(group)).orElse(false);
    }
}
