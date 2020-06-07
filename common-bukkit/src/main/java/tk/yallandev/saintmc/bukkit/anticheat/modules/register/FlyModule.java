package tk.yallandev.saintmc.bukkit.anticheat.modules.register;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import tk.yallandev.saintmc.bukkit.anticheat.modules.Module;

public class FlyModule extends Module {

	private Map<Player, Fly> ticks = new HashMap<>();

	public FlyModule() {
		setMaxAlerts(30);
		setAlertBungee(true);
	}

	public class Fly {

		private int ticks;
		private long expireTime = System.currentTimeMillis() + 1000l;

	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();

		if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR || player.getAllowFlight())
			return;

		if (player.isInsideVehicle() || isClimbing(player.getLocation()))
			return;

		Location location = player.getLocation();

		if (hasSurrondingBlocks(location.getBlock()) || isSwimming(location) || isFalling(event) || isGoingUp(event)) {
			reset(player);
			return;
		}

		Fly fly = ticks.computeIfAbsent(player, v -> new Fly());

		if (fly.expireTime < System.currentTimeMillis()) {

			if (fly.ticks >= 15) {
				alert(player);
			}

			reset(player);
			return;
		}

		fly.ticks++;
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		reset(event.getPlayer());
	}

	public void reset(Player player) {
		ticks.remove(player);
	}

	public static boolean isSwimming(Location location) {
		String type = location.getBlock().getType().name();
		return (type.contains("WATER") || type.contains("LAVA"));
	}

	public static boolean isFalling(PlayerMoveEvent event) {
		return event.getFrom().getY() - event.getTo().getY() > 0.0D;
	}

	public static boolean isGoingUp(PlayerMoveEvent event) {
		return event.getTo().getBlockY() > event.getFrom().getBlockY();
	}

	public static boolean isClimbing(Location location) {
		Block block = location.getBlock();
		
		if (location.getBlock().getType().equals(Material.VINE) || location.getBlock().getType().equals(Material.LADDER))
			return true;

		for (BlockFace blockFace : new BlockFace[] { BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST,
				BlockFace.EAST }) {
			
			if (block.getRelative(blockFace).getType().equals(Material.VINE) || block.getRelative(blockFace).getType().equals(Material.LADDER)) 
				return true;
		}
		
		return false;
	}

	public static boolean hasSurrondingBlocks(Block block) {
		Block relative = block.getRelative(BlockFace.DOWN);
		
		for (BlockFace blockFace : new BlockFace[] { BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST,
				BlockFace.EAST, BlockFace.NORTH_EAST, BlockFace.NORTH_WEST, BlockFace.SOUTH_EAST, BlockFace.SOUTH_WEST }) {
			
			if (relative.getRelative(blockFace).getType() != Material.AIR)
				return true;
		}

		return false;
	}

}
