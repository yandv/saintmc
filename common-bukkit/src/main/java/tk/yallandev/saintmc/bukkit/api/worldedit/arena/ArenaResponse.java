package tk.yallandev.saintmc.bukkit.api.worldedit.arena;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.block.BlockState;

import lombok.Getter;

@Getter
public class ArenaResponse {
	
	private Map<Location, BlockState> map;
	private int blocks;
	
	public ArenaResponse(int blocks) {
		this.blocks = -1;
		map = new HashMap<>();
	}
	
	public ArenaResponse() {
		map = new HashMap<>();
	}
	
	public void addMap(Location location, BlockState blockState) {
		map.put(location, blockState);
		blocks++;
	}
	
	public void addBlock() {
		blocks++;
	}

}
