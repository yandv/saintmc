package tk.yallandev.saintmc.bukkit.api.worldedit.block.future.types;

import org.bukkit.Location;
import org.bukkit.Material;

import tk.yallandev.saintmc.bukkit.api.worldedit.block.future.FutureBlock;

public class DefaultFutureBlock extends FutureBlock {

	public DefaultFutureBlock(Location location, Material type, byte data) {
		super(location, type, data);
	}

	@Override
	public void place() {
		setBlock();
	}

}