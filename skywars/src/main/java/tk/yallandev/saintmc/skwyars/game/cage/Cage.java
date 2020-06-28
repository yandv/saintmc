package tk.yallandev.saintmc.skwyars.game.cage;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;

import lombok.Getter;

@Getter
public abstract class Cage {
	
	private String cageName;
	private CageType cageType;
	
	public Cage(String cageName, CageType cageType) {
		this.cageName = cageName;
		this.cageType = cageType;
	}
	
	public abstract List<Block> generateCage(Location location);

}
