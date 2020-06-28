package br.com.saintmc.hungergames.abilities.register;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.material.CocoaPlant;
import org.bukkit.material.CocoaPlant.CocoaPlantSize;

import br.com.saintmc.hungergames.abilities.Ability;

public class CultivatorAbility extends Ability {

	public CultivatorAbility() {
		super("Cultivator", new ArrayList<>());
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void place(BlockPlaceEvent event) {

		Player player = event.getPlayer();

		if (!hasAbility(player))
			return;

		Block block = event.getBlock();

		switch (block.getType()) {
		case SAPLING: {
			block.setType(Material.AIR);
			block.getWorld().generateTree(block.getLocation(), TreeType.TREE);
			break;
		}
		case COCOA: {
			final BlockFace face = ((CocoaPlant) block.getState().getData()).getFacing();

			block.getWorld().getBlockAt(block.getLocation()).setType(Material.COCOA);
			BlockState state = block.getState();
			CocoaPlant coco = new CocoaPlant(CocoaPlantSize.LARGE, face);
			state.setData(coco);
			state.update();
			break;
		}
		case CROPS:
		case CARROT: {
			block.setData((byte) 7);
			break;
		}
		default:
			break;
		}
	}

}