package tk.yallandev.saintmc.skwyars.game.cage.types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import tk.yallandev.saintmc.skwyars.game.cage.Cage;
import tk.yallandev.saintmc.skwyars.game.cage.CageType;

public class DefaultCage extends Cage {

	public DefaultCage() {
		super("Default", CageType.DEFAULT);
	}

	@Override
	public List<Block> generateCage(Location location) {

		List<Block> blockList = new ArrayList<>();

		for (Block block : Arrays.asList(location.clone().add(0, -1, 0).getBlock(),
				location.clone().add(0, 0, 1).getBlock(), location.clone().add(1, 0, 0).getBlock(),
				location.clone().add(-1, 0, 0).getBlock(), location.clone().add(0, 0, 1).getBlock(),
				location.clone().add(0, 0, -1).getBlock(), location.clone().add(0, 1, 1).getBlock(),
				location.clone().add(0, 1, 1).getBlock(), location.clone().add(1, 1, 0).getBlock(),
				location.clone().add(-1, 1, 0).getBlock(), location.clone().add(0, 1, 1).getBlock(),
				location.clone().add(0, 1, -1).getBlock(), location.clone().add(0, 2, 1).getBlock(),
				location.clone().add(1, 2, 0).getBlock(), location.clone().add(-1, 2, 0).getBlock(),
				location.clone().add(0, 2, 1).getBlock(), location.clone().add(0, 2, -1).getBlock(),
				location.clone().add(0, 3, 0).getBlock())) {
			block.setType(Material.GLASS);
			blockList.add(block);
		}

		return blockList;
	}

}
