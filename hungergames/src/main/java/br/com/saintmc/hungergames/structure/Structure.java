package br.com.saintmc.hungergames.structure;

import org.bukkit.Location;

public interface Structure {
	
	Location findplace();
	
	void spawn(Location location);
	
}
