package tk.yallandev.saintmc.game.games.hungergames.structure;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class FinalBattleStructure implements Structure {

	@Override
	public Location findPlace() {
		World w = Bukkit.getWorld("world");
		return new Location(w, 0, 6, 0);
	}

	@Override
	public void place(Location spawn) {
		int radius = 36;
		int bed = 52;
		while (spawn.getBlockY() <= spawn.getWorld().getMaxHeight()) {
			for (int x = -radius; x <= radius; x++) {
				for (int z = -radius; z <= radius; z++) {
					Location l = new Location(spawn.getWorld(), x, spawn.getBlockY(), z);
					if (spawn.distance(l) < radius) {
						l.getBlock().setType(Material.AIR);
					}
				}
			}
			for (int x = -bed; x <= bed; x++) {
				for (int z = -bed; z <= bed; z++) {
					if (Math.abs(x) >= bed - 2 || Math.abs(z) >= bed - 2) {
						Location l = new Location(spawn.getWorld(), x, spawn.getBlockY() - 5.0, z);
						l.getBlock().setType(Material.BEDROCK);
					}
				}
			}
			spawn = spawn.add(0, 1, 0);
		}
	}

	public void teleportPlayers(Location spawn) {
		Random r = new Random();
		int radius = 25;
		List<Location> locations = new ArrayList<>();
		spawn.setY(11);
		for (int x = -radius; x <= radius; x++) {
			for (int z = -radius; z <= radius; z++) {
				Location l = new Location(spawn.getWorld(), x, 11, z);
				if (spawn.distance(l) <= radius && spawn.distance(l) >= radius - 2) {
					locations.add(l);
				}
			}
		}
		for (Player p : Bukkit.getOnlinePlayers()) {
			Location loc;
			if (locations.size() > 0) {
				int nexR = r.nextInt(locations.size() + 1);
				if (nexR < locations.size())
					loc = locations.get(nexR);
				else
					loc = locations.get(0);
			} else {
				loc = new Location(spawn.getWorld(), 0, 8, 0);
			}
			p.setFallDistance(-5);
			p.teleport(loc);
		}
	}
}
