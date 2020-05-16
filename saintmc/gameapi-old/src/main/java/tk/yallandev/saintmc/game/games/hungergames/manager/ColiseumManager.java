package tk.yallandev.saintmc.game.games.hungergames.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;

import tk.yallandev.saintmc.game.constructor.BO3Blocks;
import tk.yallandev.saintmc.game.games.hungergames.HungerGamesMode;
import tk.yallandev.saintmc.game.util.BO3Utils;

public class ColiseumManager {

	private List<BlockState> resetBlocks;
	private List<Block> coliseumBlocks;
	private static List<BO3Blocks> coliseum = BO3Utils.loadBO3("coliseum");
	private static List<BO3Blocks> doors = BO3Utils.loadBO3("doors");
	private Location spawn;
	private static int radius;
	private boolean doorsOpen;
	private boolean constructed;

	public ColiseumManager() {
		doorsOpen = true;
		constructed = false;
		resetBlocks = new ArrayList<>();
		coliseumBlocks = new ArrayList<>();
		radius = 38;
		World world = Bukkit.getWorlds().get(0);
		spawn = new Location(world, 0, world.getHighestBlockYAt(0, 0), 0);
	}

	public void spawnColiseum() {
		for (int x = -40; x <= 40; x++) {
			for (int z = -40; z <= 40; z++) {
				for (int y = -5; y <= 45; y++) {
					Block b = new Location(spawn.getWorld(), x, spawn.getY() + y, z).getBlock();
					resetBlocks.add(b.getState());
				}
			}
		}

		for (BO3Blocks bo3 : coliseum) {
			Block b = new Location(spawn.getWorld(), bo3.getX(), spawn.getY() + bo3.getY(), bo3.getZ()).getBlock();
			coliseumBlocks.add(b);
			Block block = b;
			int i = 45;
			do {
				if (block.getType() != Material.AIR) {
					block.setType(Material.AIR);
				}
				block = block.getRelative(BlockFace.UP);
				--i;
			} while (i >= 0);
			b.setType(bo3.getType());
			b.setData(bo3.getData());
		}
		constructed = true;
	}

	public void teleportRecursive(int time) {
		List<Player> players = new ArrayList<>();
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (!isInsideColiseum(p))
				players.add(p);
		}
		
		if (players.size() <= 0)
			return;
		
		Random r = new Random();
		int pla = (players.size() / time) + 1;
		for (int i = 0; i < pla; i++) {
			if (players.size() > 0) {
				Player p = players.get(r.nextInt(players.size()));
				HungerGamesMode.teleportToSpawn(p);
				if (!constructed) {
					p.sendMessage("§%cant-be-away-from-spawn%§");
				} else {
					p.sendMessage("§%cant-be-away-from-coliseum%§");
				}
				players.remove(p);
			}
		}

	}

	public void teleportOutsidePlayers() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (!isInsideColiseum(p)) {
				HungerGamesMode.teleportToSpawn(p);
				if (!constructed) {
					p.sendMessage("§%cant-be-away-from-spawn-match-start%§");
				} else {
					p.sendMessage("§%cant-be-away-from-coliseum%§");
				}
			}
		}
	}

	public boolean isDoorsOpen() {
		return doorsOpen;
	}

	public boolean isConstructed() {
		return constructed;
	}

	public void destroyColiseum() {
		for (BlockState state : resetBlocks) {
			state.update(true);
		}
		resetBlocks.clear();
		constructed = false;
	}

	public void closeDoors() {
		if (!constructed) {
			return;
		}
		
		doorsOpen = false;
		
		for (BO3Blocks bo3 : doors) {
			Block b = new Location(spawn.getWorld(), bo3.getX(), spawn.getY() + bo3.getY(), bo3.getZ()).getBlock();
			coliseumBlocks.add(b);
			b.setType(bo3.getType());
			b.setData(bo3.getData());
		}
		
		for (Player p : Bukkit.getOnlinePlayers()) {
//			TitleAPI.setTitle(p, "§%coliseum%§", "§%doors-closed%§");
		}
	}

	public void openDoors() {
		if (!doorsOpen)
			return;
		
		doorsOpen = true;
		
		for (BO3Blocks bo3 : doors) {
			Block b = new Location(spawn.getWorld(), bo3.getX(), spawn.getY() + bo3.getY(), bo3.getZ()).getBlock();
			b.setType(Material.AIR);
		}
	}

	public boolean isColiseumBlock(Block b) {
		return coliseumBlocks.contains(b);
	}

	public static boolean isInsideColiseum(Player p) {
		Location central = new Location(p.getWorld(), 0, p.getLocation().getY(), 0);
		if (central.distance(p.getLocation()) < radius)
			return true;
		return !(((p.getLocation().getBlockX() > radius) || (p.getLocation().getBlockX() < -radius) || (p.getLocation().getBlockZ() > radius) || (p.getLocation().getBlockZ() < -radius)) || central.distance(p.getLocation()) > 41);
	}

}
