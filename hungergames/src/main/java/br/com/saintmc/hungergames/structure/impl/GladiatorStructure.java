package br.com.saintmc.hungergames.structure.impl;

import org.bukkit.Location;
import org.bukkit.Material;

import br.com.saintmc.hungergames.structure.Structure;


/**
 * 
 * Create Custom Gladiator Structure
 * 
 * @author yandv
 *
 */

public class GladiatorStructure implements Structure {

	@Override
	public Location findPlace() {
		return null;
	}

	@Override
	public void spawn(Location location) {
		
		for (int x = 0; x <= 8; x++) {
			for (int z = 0; z <= 8; z++) {
				if (x == 8 || z == 8) {
					location.add(x, 0, z).getBlock().setType(Material.GLASS);
				} else {
					for (int y = 0; y <= 8; y++) {
						location.add(x, 0, z).getBlock().setType(Material.GLASS);
					}
				}
			}
		}
		
		
	}
	
	public void open() {
		
	}

}
