package net.saintmc.anticheat.storage;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import lombok.Setter;
import net.saintmc.anticheat.account.Member;

@Setter
@Getter
public class InteractStorage extends Storage {
	
	public ItemStack item;
	public Block block;
	public Action action;

	public InteractStorage(Member member, Player player) {
		super(member, player);
	}
	
}
