package tk.yallandev.saintmc.bukkit.api.worldedit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import lombok.Setter;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.api.item.ActionItemStack;
import tk.yallandev.saintmc.bukkit.api.item.ActionItemStack.ActionType;
import tk.yallandev.saintmc.bukkit.api.item.ActionItemStack.Interact;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.bukkit.api.worldedit.block.future.FutureBlock;
import tk.yallandev.saintmc.bukkit.api.worldedit.block.future.types.DefaultFutureBlock;

public class WorldeditController {

	private Map<UUID, Position> positionMap;
	private ActionItemStack wand;

	public WorldeditController() {
		positionMap = new HashMap<>();

		wand = new ActionItemStack(new ItemBuilder().name("§dWand").type(Material.WOOD_AXE).build(), new Interact() {

			@Override
			public boolean onInteract(Player player, Entity entity, Block block, ItemStack item, ActionType action) {

				if (block != null) {
					if (action == ActionType.LEFT) {
						setFirstPosition(player, block.getLocation());
						player.sendMessage("§dO local da primeira posição é " + block.getX() + ", " + block.getY()
								+ ", " + block.getZ());
					} else {
						setSecondPosition(player, block.getLocation());
						player.sendMessage("§dO local da segunda posição é " + block.getX() + ", " + block.getY() + ", "
								+ block.getZ());
					}
				}

				return true;
			}

		});
	}

	public void addUndo(Player player, Map<Location, BlockState> map) {
//		player.getInventory().addItem(wand.getItemStack());
		positionMap.computeIfAbsent(player.getUniqueId(), v -> new Position()).addUndo(map);
	}

	public void removeUndo(Player player, Map<Location, BlockState> map) {
//		player.getInventory().addItem(wand.getItemStack());
		positionMap.computeIfAbsent(player.getUniqueId(), v -> new Position()).removeUndo(map);
	}

	public void giveWand(Player player) {
		player.getInventory().addItem(wand.getItemStack());
	}

	public void setFirstPosition(Player player, Location location) {
		positionMap.computeIfAbsent(player.getUniqueId(), v -> new Position()).setFirstLocation(location);
		;
	}

	public void setSecondPosition(Player player, Location location) {
		positionMap.computeIfAbsent(player.getUniqueId(), v -> new Position()).setSecondLocation(location);
		;
	}

	public boolean hasFirstPosition(Player player) {
		return positionMap.computeIfAbsent(player.getUniqueId(), v -> new Position()).hasFirstLocation();
	}

	public boolean hasSecondPosition(Player player) {
		return positionMap.computeIfAbsent(player.getUniqueId(), v -> new Position()).hasSecondLocation();
	}

	public boolean hasUndoList(Player player) {
		return !positionMap.computeIfAbsent(player.getUniqueId(), v -> new Position()).getUndoList().isEmpty();
	}

	public Location getFirstPosition(Player player) {
		return positionMap.computeIfAbsent(player.getUniqueId(), v -> new Position()).getFirstLocation();
	}

	public Location getSecondPosition(Player player) {
		return positionMap.computeIfAbsent(player.getUniqueId(), v -> new Position()).getSecondLocation();
	}

	public List<Map<Location, BlockState>> getUndoList(Player player) {
		return positionMap.computeIfAbsent(player.getUniqueId(), v -> new Position()).getUndoList();
	}

	public List<Location> getLocationsFromTwoPoints(Location location1, Location location2) {
		List<Location> locations = new ArrayList<>();

		int topBlockX = (location1.getBlockX() < location2.getBlockX() ? location2.getBlockX() : location1.getBlockX());
		int bottomBlockX = (location1.getBlockX() > location2.getBlockX() ? location2.getBlockX()
				: location1.getBlockX());

		int topBlockY = (location1.getBlockY() < location2.getBlockY() ? location2.getBlockY() : location1.getBlockY());
		int bottomBlockY = (location1.getBlockY() > location2.getBlockY() ? location2.getBlockY()
				: location1.getBlockY());

		int topBlockZ = (location1.getBlockZ() < location2.getBlockZ() ? location2.getBlockZ() : location1.getBlockZ());
		int bottomBlockZ = (location1.getBlockZ() > location2.getBlockZ() ? location2.getBlockZ()
				: location1.getBlockZ());

		for (int x = bottomBlockX; x <= topBlockX; x++) {
			for (int z = bottomBlockZ; z <= topBlockZ; z++) {
				for (int y = bottomBlockY; y <= topBlockY; y++) {
					locations.add(new Location(location1.getWorld(), x, y, z));
				}
			}
		}

		return locations;
	}

	public List<FutureBlock> load(Location location, File file) {
		BufferedReader reader;
		List<FutureBlock> blocks = new ArrayList<>();

		try {
			reader = new BufferedReader(new FileReader(file));
			String line = null;

			while ((line = reader.readLine()) != null) {
				if (!line.contains(",") || !line.contains(":")) {
					continue;
				}

				String[] parts = line.split(":");
				String[] coordinates = parts[0].split(",");
				String[] blockData = parts[1].split("\\.");
				blocks.add(new DefaultFutureBlock(
						location.clone().add(Integer.valueOf(coordinates[0]), Integer.valueOf(coordinates[2]),
								Integer.valueOf(coordinates[1])),
						Material.values()[Integer.valueOf(blockData[0])],
						blockData.length > 1 ? Byte.valueOf(blockData[1]) : 0));
			}

			reader.close();
		} catch (Exception e) {
			CommonGeneral.getInstance()
					.debug("Error to load the bo2file " + file.getName() + " in the location " + location.toString());
		}

		return blocks;
	}

	public void spawn(Location location, File file) {
		for (FutureBlock futureBlock : load(location, file)) {
			futureBlock.place();
		}
	}

	public boolean setBlockFast(FutureBlock futureBlock) {
		int y = futureBlock.getLocation().getBlockY();

		if (y >= 255 || y < 0) {
			return false;
		}

		futureBlock.place();
		return true;
	}

	@Getter
	public class Position {

		@Setter
		private Location firstLocation;
		@Setter
		private Location secondLocation;

		private List<Map<Location, BlockState>> undoList;

		public Position() {
			undoList = new ArrayList<>();
		}

		public void addUndo(Map<Location, BlockState> map) {
			undoList.add(map);
		}

		public void removeUndo(Map<Location, BlockState> map) {
			undoList.remove(map);
		}

		public boolean hasFirstLocation() {
			return firstLocation != null;
		}

		public boolean hasSecondLocation() {
			return firstLocation != null;
		}
	}

}
