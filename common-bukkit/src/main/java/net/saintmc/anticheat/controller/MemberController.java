package net.saintmc.anticheat.controller;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

import net.saintmc.anticheat.account.Member;

public class MemberController {

	public static final MemberController INSTANCE = new MemberController();

	private Map<UUID, Member> memberMap;

	public MemberController() {
		memberMap = new HashMap<>();
	}

	public Collection<Member> getCollection() {
		return memberMap.values();
	}

	public Member load(Player player) {
		return memberMap.computeIfAbsent(player.getUniqueId(), v -> new Member(player));
	}

	public void unload(Player player) {
		if (memberMap.containsKey(player.getUniqueId()))
			memberMap.remove(player.getUniqueId());
	}

}
