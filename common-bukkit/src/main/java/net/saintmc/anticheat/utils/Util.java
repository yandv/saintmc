package net.saintmc.anticheat.utils;

import org.bukkit.Material;

public class Util {
	
	public static boolean IsAir(Material type) {
		return !(type != Material.AIR && type != Material.TORCH && type != Material.REDSTONE_TORCH_OFF
				&& type != Material.REDSTONE_TORCH_ON);
	}

	public static boolean IsHalfBlock(Material material) {
		return (material == Material.STEP);
	}

	public static boolean IsLadder(Material material) {
		return !(material != Material.LADDER && material != Material.VINE);
	}

	public static boolean IsBlockAndAHalf(Material material) {
		return (material == Material.STEP);
	}

	public static boolean IsSolid(Material material) {
		return material.isSolid();
	}
	
}