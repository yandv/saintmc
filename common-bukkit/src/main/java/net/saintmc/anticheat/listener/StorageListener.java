package net.saintmc.anticheat.listener;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffectType;

import net.saintmc.anticheat.check.CheckController.CheckType;
import net.saintmc.anticheat.controller.MemberController;
import net.saintmc.anticheat.storage.DamageStorage;
import net.saintmc.anticheat.storage.InteractStorage;
import net.saintmc.anticheat.storage.InventoryChangeStorage;
import net.saintmc.anticheat.storage.InventoryCloseStorage;
import net.saintmc.anticheat.storage.MoveStorage;
import net.saintmc.anticheat.utils.Util;
import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.api.protocol.ProtocolGetter;

public class StorageListener implements Listener {

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEntityDamage(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;

		Player player = (Player) event.getEntity();
		Player damager = null;

		if (event instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent entityDamageByEntityEvent = (EntityDamageByEntityEvent) event;

			if (entityDamageByEntityEvent.getDamager() instanceof Player)
				damager = (Player) entityDamageByEntityEvent.getDamager();
		}

		DamageStorage damageStorage = new DamageStorage(MemberController.INSTANCE.load(player), player);

		damageStorage.setEntitySprintHit(player.isSprinting());
		damageStorage.setDamagerSprintHit(damager == null ? false : damager.isSprinting());
		damageStorage.setPlayerDamage(damager != null);
		damageStorage.setY(player.getLocation().getY());
		damageStorage.setCause(event.getCause());
		damageStorage.setDamage(event.getDamage());
		damageStorage.getMember().setLastDamage(damageStorage);

		if (ProtocolGetter.getPing(player) < 200)
			BukkitMain.getInstance().getAnticheatController().getCheckController().call(CheckType.DAMAGE,
					damageStorage);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		InteractStorage interactStorage = new InteractStorage(MemberController.INSTANCE.load(player), player);

		interactStorage.setItem(event.getItem());
		interactStorage.setBlock(event.getClickedBlock());
		interactStorage.setAction(event.getAction());
		MemberController.INSTANCE.load(player).setLastInteractStorage(interactStorage);

		if (ProtocolGetter.getPing(player) < 300)
			BukkitMain.getInstance().getAnticheatController().getCheckController().call(CheckType.INTERACT,
					interactStorage);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onInventoryClick(InventoryClickEvent event) {
		if (!(event.getWhoClicked() instanceof Player))
			return;

		Player player = (Player) event.getWhoClicked();

		InventoryChangeStorage inventoryChangeStorage = new InventoryChangeStorage(
				MemberController.INSTANCE.load(player), player);

		inventoryChangeStorage.setCursorItem(event.getCursor());
		inventoryChangeStorage.setCurrentItem(event.getCurrentItem());
		inventoryChangeStorage.setSlot(event.getSlot());
		inventoryChangeStorage.setRawSlot(event.getRawSlot());
		inventoryChangeStorage.getMember().setLastChangeStorage(inventoryChangeStorage);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onInventoryClose(InventoryCloseEvent event) {
		if (!(event.getPlayer() instanceof Player))
			return;

		Player player = (Player) event.getPlayer();
		InventoryCloseStorage inventoryCloseStorage = new InventoryCloseStorage(MemberController.INSTANCE.load(player),
				player);
		inventoryCloseStorage.getMember().setLastInventoryClose(inventoryCloseStorage);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();

		if (player.getAllowFlight() || player.getGameMode() == GameMode.CREATIVE
				|| player.getGameMode() == GameMode.SPECTATOR)
			return;

		Location to = event.getTo();
		Location from = event.getFrom();

		MoveStorage moveStorage = new MoveStorage(MemberController.INSTANCE.load(player), player);

		moveStorage.setTo(to);
		moveStorage.setFrom(from);

		moveStorage.setSpeed(player.hasPotionEffect(PotionEffectType.SPEED));
		moveStorage.setSpriting(player.isSprinting());
		moveStorage.setGround((isOnGround(to) && !isOnGround(to.clone().add(0.0D, 1.0D, 0.0D))));
		moveStorage.setFoodLevel(player.getFoodLevel());
		moveStorage.setMoveSpeed(from.distance(to));

		moveStorage.setSwimming(isSwimming(to));
		moveStorage.setLadder(isClimbing(to));

		double x = Math.round((to.getX() - from.getX()) * 10.0D) / 10.0D;
		double y = Math.round((to.getY() - from.getY()) * 10.0D) / 10.0D;
		double z = Math.round((to.getZ() - from.getZ()) * 10.0D) / 10.0D;

		moveStorage.setHorizontalDistance(Math.round(Math.sqrt(x * x + z * z) * 10.0D) / 10.0D);
		moveStorage.setVerticalDistance(y);
		moveStorage.setJumpHeight(from.getY() - to.getY());
		moveStorage.setFallDistance(player.getFallDistance());

		moveStorage.getMember().setLastMove(moveStorage);
		if (moveStorage.isGround())
			moveStorage.getMember().setLastMoveOnGround(moveStorage);

		if (ProtocolGetter.getPing(player) < 350)
			BukkitMain.getInstance().getAnticheatController().getCheckController().call(CheckType.MOVEMENT,
					moveStorage);
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		MemberController.INSTANCE.unload(event.getPlayer());
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

		if (location.getBlock().getType().equals(Material.VINE)
				|| location.getBlock().getType().equals(Material.LADDER))
			return true;

		for (BlockFace blockFace : new BlockFace[] { BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST,
				BlockFace.EAST }) {

			if (block.getRelative(blockFace).getType().equals(Material.VINE)
					|| block.getRelative(blockFace).getType().equals(Material.LADDER))
				return true;
		}

		return false;
	}

	public static boolean isWater(Material mat) {
		return !(mat != Material.WATER && mat != Material.STATIONARY_WATER);
	}

	public static boolean isLava(Material mat) {
		return !(mat != Material.LAVA && mat != Material.STATIONARY_LAVA);
	}

	private boolean isOnGround(Location location) {
		boolean isOnGround = false;
		for (Block b : getBlocksBelow(location)) {
			if (Util.IsSolid(b.getType())) {
				isOnGround = true;
				break;
			}
			if (Util.IsLadder(b.getType())) {
				isOnGround = true;
				break;
			}
		}
		return isOnGround;
	}

	public static List<Block> getBlocksBelow(Location location) {
		List<Block> blocksBelow = new ArrayList<>();

		double x = location.getX();
		double z = location.getZ();
		World world = location.getWorld();
		double yBelow = location.getY() % 1.0D == 0.0D ? location.getY() - 1.0E-4D : location.getY() - 1.0D;

		Block northEast = (new Location(world, x + 0.31D, yBelow, z - 0.31D)).getBlock();
		Block northWest = (new Location(world, x - 0.31D, yBelow, z - 0.31D)).getBlock();
		Block southEast = (new Location(world, x + 0.31D, yBelow, z + 0.31D)).getBlock();
		Block southWest = (new Location(world, x - 0.31D, yBelow, z + 0.31D)).getBlock();
		Block[] blocks = { northEast, northWest, southEast, southWest };

		for (Block block : blocks) {
			if (!blocksBelow.contains(block))
				blocksBelow.add(block);
		}
		return blocksBelow;
	}

}
