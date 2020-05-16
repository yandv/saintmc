package tk.yallandev.saintmc.game.games.hungergames.structure;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

public class FeastStructure implements Structure {
	private int radius;
	private int maxSpawnDistance;
	private static Set<Block> feastBlocks = new HashSet<>();

	public FeastStructure() {
		this(25, 150);
	}

	public FeastStructure(int radius, int maxSpawnDistance) {
		this.radius = radius;
		this.maxSpawnDistance = maxSpawnDistance;
	}

	@Override
	public Location findPlace() {
		World w = Bukkit.getWorld("world");
		Random r = new Random();
		int x = -maxSpawnDistance + r.nextInt(2 * maxSpawnDistance);
		int z = -maxSpawnDistance + r.nextInt(2 * maxSpawnDistance);
		int y = w.getHighestBlockYAt(x, z);
		return new Location(w, x, y, z);
	}

	@Override
	public void place(Location central) {
		System.out.println("New Feast: " + central.getBlockX() + " " + central.getBlockY() + " " + central.getBlockZ());
		central.getChunk().load(true);
		for (int x = -radius; x <= radius; x++) {
			for (int z = -radius; z <= radius; z++) {
				Location feastBlock = central.clone().add(x, 0, z);
				if (central.distance(feastBlock) < radius) {
					feastBlock.getBlock().setType(Material.GRASS);
					feastBlocks.add(feastBlock.getBlock());
					for (int i = 1; i < 10; i++) {
						Location airBlock = feastBlock.clone().add(0, i, 0);
						airBlock.getBlock().setType(Material.AIR);
						feastBlocks.add(airBlock.getBlock());
					}
				}
			}
		}
	}

	public static boolean isFeastBlock(Block block) {
		return feastBlocks.contains(block);
	}

}
