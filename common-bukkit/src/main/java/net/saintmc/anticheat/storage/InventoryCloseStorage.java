package net.saintmc.anticheat.storage;

import org.bukkit.entity.Player;

import net.saintmc.anticheat.account.Member;

public class InventoryCloseStorage extends Storage {

	public InventoryCloseStorage(Member member, Player player) {
		super(member, player);
	}

}
