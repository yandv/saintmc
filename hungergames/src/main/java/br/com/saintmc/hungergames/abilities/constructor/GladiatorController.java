package br.com.saintmc.hungergames.abilities.constructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import br.com.saintmc.hungergames.GameMain;
import tk.yallandev.saintmc.bukkit.event.PlayerMoveUpdateEvent;
import tk.yallandev.saintmc.bukkit.event.player.PlayerDamagePlayerEvent;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent.UpdateType;

public class GladiatorController {

	private int radius = 7;
	private int weight = 14;

	private Map<Player, Gladiator> playerList;
	private List<Block> blockList;

	private GladiatorListener listener = new GladiatorListener();

	public GladiatorController() {
		playerList = new HashMap<>();
		blockList = new ArrayList<>();
	}

	public Location[] createGladiator(List<Block> blockList, Location gladiatorLocation) {
		while (gladiatorLocation.getBlock().getType() != Material.AIR) {
			gladiatorLocation = gladiatorLocation.add(8, 0, 8);
		}

		for (int x = -radius; x <= radius; x++) {
			for (int z = -radius; z <= radius; z++) {
				if (x == radius || z == radius || x == -radius || z == -radius) {
					for (int y = 0; y <= weight; y++) {
						Block block = gladiatorLocation.clone().add(x, y, z).getBlock();
						block.setType(Material.GLASS);
						blockList.add(block);
						this.blockList.add(block);
					}
				} else {
					Block block = gladiatorLocation.clone().add(x, 0, z).getBlock();
					block.setType(Material.GLASS);
					blockList.add(block);
					this.blockList.add(block);
					
					block = gladiatorLocation.clone().add(x, weight, z).getBlock();
					block.setType(Material.STAINED_GLASS);
					blockList.add(block);
					this.blockList.add(block);
				}
			}
		}

		return new Location[] { gladiatorLocation.clone().add(radius - 1.5, 1.5, radius - 1.5),
				gladiatorLocation.clone().add(-radius + 1.5, 1.5, -radius + 1.5) };
	}

	public boolean isInFight(Player player) {
		return playerList.containsKey(player);
	}

	public Gladiator getGladiator(Player player) {
		return playerList.get(player);
	}

	public boolean isGladiatorBlock(Block block) {
		return blockList.contains(block);
	}

	public void sendGladiator(Player player, Player target) {
		Gladiator gladiator = new Gladiator(player, target);

		playerList.put(player, gladiator);
		playerList.put(target, gladiator);
		listener.register();
	}

	public void removeGladiator(Gladiator gladiator) {
		playerList.remove(gladiator.gladiator);
		playerList.remove(gladiator.player);

		if (playerList.isEmpty())
			listener.unregister();
	}

	public class Gladiator {

		private Player gladiator;
		private Player player;

		private Location gladiatorLocation;
		private Location backLocation;

		private List<Block> gladiatorBlocks;
		private List<Block> playersBlocks;

		private int time;

		public Gladiator(Player gladiator, Player player) {
			this.gladiator = gladiator;
			this.player = player;

			this.gladiatorBlocks = new ArrayList<>();
			this.playersBlocks = new ArrayList<>();

			this.gladiatorLocation = new Location(gladiator.getWorld(), gladiator.getLocation().getX(), 120,
					gladiator.getLocation().getZ());
			this.backLocation = gladiator.getLocation();

			Location[] location = createGladiator(gladiatorBlocks, gladiatorLocation);

			Location l1 = location[0];
			l1.setYaw(135.0F);

			gladiator.teleport(l1);
			gladiator.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 5, 5));

			Location l2 = location[1];
			l2.setYaw(315.0F);

			player.teleport(l2);
			player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 5, 5));
		}

		public void handleEscape() {
			clearGladiator();
			teleportBack();

			gladiator.removePotionEffect(PotionEffectType.WITHER);
			player.removePotionEffect(PotionEffectType.WITHER);
			removeGladiator(this);
		}

		public void handleWin(Player death) {
			Player winner = (death == gladiator ? player : gladiator);

			clearGladiator();

			winner.teleport(backLocation);
			winner.removePotionEffect(PotionEffectType.WITHER);
			winner.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 5, 5));
			removeGladiator(this);
		}

		public void handleFinish() {
			clearGladiator();
			teleportBack();
		}

		public void pulse() {
			time++;

			if (time == 120) {
				gladiator.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20 * 60, 1));
				player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20 * 60, 1));
			}

			if (time == 180) {
				handleFinish();
			}
		}

		public void addBlock(Block block) {
			if (!this.playersBlocks.contains(block))
				this.playersBlocks.add(block);
		}

		public void removeBlock(Block block) {
			if (this.playersBlocks.contains(block))
				this.playersBlocks.remove(block);
		}

		private void clearGladiator() {
			for (Block block : Stream.concat(gladiatorBlocks.stream(), playersBlocks.stream())
					.collect(Collectors.toList())) {
				block.setType(Material.AIR);

				if (blockList.contains(block))
					blockList.remove(block);
			}
		}

		private void teleportBack() {
			gladiator.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 5, 5));
			gladiator.teleport(backLocation);

			player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 5, 5));
			player.teleport(backLocation);
		}

		public boolean isInGladiator(Player player) {
			return player == this.player || player == gladiator;
		}
	}

	public class GladiatorListener implements Listener {

		private boolean registered;

		@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
		public void onPlayerDeath(PlayerDeathEvent event) {
			Player player = event.getEntity();

			if (isInFight(player))
				getGladiator(player).handleWin(player);
		}

		@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
		public void onPlayerQuit(PlayerQuitEvent event) {
			Player player = event.getPlayer();

			if (isInFight(player))
				getGladiator(player).handleWin(player);
		}

		@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
		public void onPlayerDamage(PlayerDamagePlayerEvent event) {
			Player damager = event.getDamager();
			Player player = event.getPlayer();
			
			if (isInFight(damager)) {
				if (isInFight(player)) {
					Gladiator gladiator = getGladiator(damager);

					if (gladiator.isInGladiator(player)) {
						event.setCancelled(false);
					} else {
						event.setCancelled(true);
					}
				} else {
					event.setCancelled(true);
				}
			}
		}

		@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
		public void onBlockPlace(BlockPlaceEvent event) {
			Player player = event.getPlayer();

			if (isInFight(player))
				getGladiator(player).addBlock(event.getBlock());
		}

		@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
		public void onBlockBreak(BlockBreakEvent event) {
			Player player = event.getPlayer();

			if (blockList.contains(event.getBlock())) {
				event.setCancelled(true);
				return;
			}

			if (isInFight(player))
				getGladiator(player).removeBlock(event.getBlock());
		}

		@SuppressWarnings("deprecation")
		@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
		public void onBlockBreak(BlockDamageEvent event) {
			if (blockList.contains(event.getBlock())) {
				Player player = event.getPlayer();
				Block block = event.getBlock();

				player.sendBlockChange(block.getLocation(), Material.BEDROCK, (byte) 0);
				return;
			}
		}

		@EventHandler
		public void onUpdate(UpdateEvent event) {
			if (event.getType() == UpdateType.SECOND)
				playerList.values().forEach(Gladiator::pulse);
		}

		@EventHandler
		public void onPlayerMoveUpdate(PlayerMoveUpdateEvent event) {
			Player player = event.getPlayer();

			if (isInFight(player)) {
				Gladiator gladiator = getGladiator(player);

				if (event.getFrom().getY() - gladiator.gladiatorLocation.getY() > weight)
					gladiator.handleEscape();
			}
		}

		public void register() {
			if (!registered) {
				Bukkit.getPluginManager().registerEvents(this, GameMain.getInstance());
				registered = true;
			}
		}

		public void unregister() {
			if (registered) {
				HandlerList.unregisterAll(this);
				registered = false;
			}
		}

	}

}
