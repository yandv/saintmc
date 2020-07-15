package net.saintmc.anticheat.storage;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import lombok.Setter;
import net.saintmc.anticheat.account.Member;

@Getter
@Setter
public class InventoryChangeStorage extends Storage {

	private ItemStack cursorItem;
	private ItemStack currentItem;
	private int slot;
	private int rawSlot;

	public InventoryChangeStorage(Member member, Player player) {
		super(member, player);
	}
}
