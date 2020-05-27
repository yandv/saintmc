package br.com.saintmc.hungergames.constructor;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Timeout {
	
	private long expireTime;
	
	private Location location;
	
	private ItemStack[] contents;
	private ItemStack[] armorContents;
	
}