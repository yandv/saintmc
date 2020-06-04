package tk.yallandev.saintmc.bukkit.api.worldedit.block.future.types;

import org.bukkit.Location;
import org.bukkit.Material;

import tk.yallandev.saintmc.bukkit.api.worldedit.block.future.FutureBlock;

public class VoidFutureBlock extends FutureBlock {

	public VoidFutureBlock(Location location, Material type, byte data) {
		super(location, type, data);
	}

	@Override
	public void place() {
		
	}

}