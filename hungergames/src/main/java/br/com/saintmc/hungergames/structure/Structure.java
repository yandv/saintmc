package br.com.saintmc.hungergames.structure;

import org.bukkit.Location;

public interface Structure {
	
	Location findPlace();
	
	void spawn(Location location);
	
}
