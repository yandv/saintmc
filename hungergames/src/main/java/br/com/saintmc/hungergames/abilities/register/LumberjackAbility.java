package br.com.saintmc.hungergames.abilities.register;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;

import br.com.saintmc.hungergames.abilities.Ability;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;

public class LumberjackAbility extends Ability {

	public LumberjackAbility() {
		super("Lumberjack",
				Arrays.asList(new ItemBuilder().name("§6Machado do Lenhador").type(Material.WOOD_AXE).build()));
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {
		if (hasAbility(event.getPlayer()))
			if (event.getBlock().getType() == Material.LOG || event.getBlock().getType() == Material.LOG_2)
				if (event.getPlayer().getItemInHand().getType().name().contains("AXE")) {
					blockBreak(event.getBlock());
					event.setCancelled(true);
				}
	}

	public void blockBreak(Block block) {
		block.breakNaturally();

		for (BlockFace blockFace : new BlockFace[] { BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.SOUTH,
				BlockFace.EAST, BlockFace.WEST }) {
			Block relative = block.getRelative(blockFace);

			if (relative != null) {
				if (relative.getType() == Material.LOG || relative.getType() == Material.LOG_2)
					blockBreak(relative);
			}
		}
	}

}
