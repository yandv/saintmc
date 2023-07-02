package br.com.saintmc.hungergames.abilities.constructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import br.com.saintmc.hungergames.GameMain;
import br.com.saintmc.hungergames.event.ability.GladiatorScapeEvent;
import br.com.saintmc.hungergames.event.ability.PlayerStompedEvent;
import br.com.saintmc.hungergames.event.player.PlayerDeathDropItemEvent;
import tk.yallandev.saintmc.BukkitConst;
import tk.yallandev.saintmc.bukkit.event.player.PlayerDamagePlayerEvent;
import tk.yallandev.saintmc.bukkit.event.player.TeleportAllEvent;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent.UpdateType;

public class GladiatorController {

	public static final GladiatorController GLADIATOR_CONTROLLER = new GladiatorController();

	private int radius = 8;
	private int height = 12;

	private Map<Player, Gladiator> playerList;
	private List<Gladiator> gladiatorList;
	private List<Block> blockList;

	private GladiatorListener listener = new GladiatorListener();

	public GladiatorController() {
		playerList = new HashMap<>();
		gladiatorList = new ArrayList<>();
		blockList = new ArrayList<>();
	}

	public Location[] createGladiator(List<Block> blockList, Location gladiatorLocation) {
		Location loc = gladiatorLocation;
		boolean hasGladi = true;

		while (hasGladi) {
			hasGladi = false;
			boolean stop = false;
			for (double x = -8.0D; x <= 8.0D; x += 1.0D) {
				for (double z = -8.0D; z <= 8.0D; z += 1.0D) {
					for (double y = 0.0D; y <= 10.0D; y += 1.0D) {
						Location l = new Location(loc.getWorld(), loc.getX() + x, 120.0D + y, loc.getZ() + z);
						if (l.getBlock().getType() != Material.AIR) {
							hasGladi = true;
							loc = new Location(loc.getWorld(), loc.getX() + 20.0D, loc.getY(), loc.getZ());
							stop = true;
						}
						if (stop) {
							break;
						}
					}
					if (stop) {
						break;
					}
				}
				if (stop) {
					break;
				}
			}
		}

		Block mainBlock = loc.getBlock();

		for (double x = -radius; x <= radius; x += 1.0D) {
			for (double z = -radius; z <= radius; z += 1.0D) {
				for (double y = 0.0D; y <= height; y += 1.0D) {
					Location l = new Location(mainBlock.getWorld(), mainBlock.getX() + x, 120.0D + y,
							mainBlock.getZ() + z);
					l.getBlock().setType(Material.GLASS);
					blockList.add(l.getBlock());
					this.blockList.add(l.getBlock());
				}
			}
		}

		for (double x = -radius + 1; x <= radius - 1; x += 1.0D) {
			for (double z = -radius + 1; z <= radius - 1; z += 1.0D) {
				for (double y = 1.0D; y <= height; y += 1.0D) {
					Location l = new Location(mainBlock.getWorld(), mainBlock.getX() + x, 120.0D + y,
							mainBlock.getZ() + z);
					l.getBlock().setType(Material.AIR);
					this.blockList.remove(l.getBlock());
				}
			}
		}

		return new Location[] {
				new Location(mainBlock.getWorld(), mainBlock.getX() + 6.5D, 121.0D, mainBlock.getZ() + 6.5D),
				new Location(mainBlock.getWorld(), mainBlock.getX() - 5.5D, 121.0D, mainBlock.getZ() - 5.5D) };
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
		gladiatorList.add(gladiator);
		listener.register();
	}

	public void removeGladiator(Gladiator gladiator) {
		playerList.remove(gladiator.gladiator);
		playerList.remove(gladiator.player);
		gladiatorList.remove(gladiator);

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

			this.gladiatorLocation = gladiator.getLocation();
			this.backLocation = gladiator.getLocation();

			Location[] location = createGladiator(gladiatorBlocks, gladiatorLocation);

			Location l1 = location[0];
			l1.setYaw(135.0F);

			gladiator.teleport(l1);
			gladiator.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, BukkitConst.TPS * 5, 5));

			Location l2 = location[1];
			l2.setYaw(315.0F);

			player.teleport(l2);
			player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, BukkitConst.TPS * 5, 5));
		}

		public void handleEscape(boolean teleportBack) {
			clearGladiator();

			if (teleportBack)
				teleportBack();

			gladiator.removePotionEffect(PotionEffectType.WITHER);
			player.removePotionEffect(PotionEffectType.WITHER);
			removeGladiator(this);

			Bukkit.getPluginManager().callEvent(new GladiatorScapeEvent(gladiator));
		}

		public void handleWin(Player death) {
			Player winner = (death == gladiator ? player : gladiator);

			clearGladiator();

			winner.teleport(backLocation);
			winner.removePotionEffect(PotionEffectType.WITHER);
			winner.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, BukkitConst.TPS * 5, 5));
			removeGladiator(this);
		}

		public void handleFinish() {
			clearGladiator();
			teleportBack();

			if (gladiator.isOnline())
				gladiator.removePotionEffect(PotionEffectType.WITHER);

			if (player.isOnline())
				player.removePotionEffect(PotionEffectType.WITHER);

			removeGladiator(this);
		}

		public void pulse() {
			time++;

			if (time == 10) {
				for (Block block : gladiatorBlocks) {
					if (block.hasMetadata("gladiatorBreakable")) {
						block.setType(Material.AIR);
					}
				}
			}

			if (time == 120) {
				gladiator.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, BukkitConst.TPS * 60, 3));
				player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, BukkitConst.TPS * 60, 3));
			}

			if (time == 180) {
				handleFinish();
			}
		}

		public void addBlock(Block block) {
			if (!this.playersBlocks.contains(block))
				this.playersBlocks.add(block);
		}

		public boolean removeBlock(Block block) {
			if (this.playersBlocks.contains(block)) {
				this.playersBlocks.remove(block);
				return true;
			}

			return false;
		}

		private void clearGladiator() {
			for (Block block : gladiatorBlocks) {
				block.setType(Material.AIR);

				if (blockList.contains(block))
					blockList.remove(block);
			}

			for (Block block : playersBlocks) {
				block.setType(Material.AIR);

				if (blockList.contains(block))
					blockList.remove(block);
			}
		}

		private void teleportBack() {
			gladiator.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, BukkitConst.TPS * 5, 5));
			gladiator.teleport(backLocation);

			player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, BukkitConst.TPS * 5, 5));
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

			if (isInFight(player)) {
				event.getDrops().clear();
				getGladiator(player).handleWin(player);
			}
		}

		@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
		public void onPlayerQuit(PlayerQuitEvent event) {
			Player player = event.getPlayer();

			if (isInFight(player)) {
				getGladiator(player).handleWin(player);
				player.damage(200d);
			}
		}

		@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
		public void onPlayerDamage(PlayerDamagePlayerEvent event) {
			Player damager = event.getDamager();
			Player player = event.getPlayer();

			if (isInFight(player)) {
				Gladiator gladiator = getGladiator(player);

				if (gladiator.isInGladiator(damager)) {
					event.setCancelled(false);
				} else
					event.setCancelled(true);
			} else if (isInFight(damager)) {
				Gladiator gladiator = getGladiator(damager);

				if (gladiator.isInGladiator(player)) {
					event.setCancelled(false);
				} else
					event.setCancelled(true);
			}
		}

		@EventHandler
		public void onPlayerDeathDropItem(PlayerDeathDropItemEvent event) {
			if (isInFight(event.getPlayer()))
				event.setLocation(getGladiator(event.getPlayer()).backLocation);
		}

		@EventHandler(priority = EventPriority.MONITOR)
		public void onPlayerStomped(PlayerStompedEvent event) {
			Player stomper = event.getStomper();
			Player stomped = event.getPlayer();

			if (isInFight(stomped)) {
				Gladiator gladiator = getGladiator(stomped);

				if (gladiator.isInGladiator(stomper)) {
					event.setCancelled(false);
				} else
					event.setCancelled(true);
			}
		}

		@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
		public void onBlockPlace(BlockPlaceEvent event) {
			Player player = event.getPlayer();

			if (isInFight(player))
				getGladiator(player).addBlock(event.getBlock());
		}

		@EventHandler(priority = EventPriority.MONITOR)
		public void onBlockBreak(BlockBreakEvent event) {
			Player player = event.getPlayer();

			if (blockList.contains(event.getBlock())) {
				event.setCancelled(true);
				return;
			}

			if (isInFight(player)) {
				getGladiator(player).removeBlock(event.getBlock());
			}
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
				gladiatorList.forEach(Gladiator::pulse);
		}

		@EventHandler
		public void onPlayerMove(PlayerMoveEvent event) {
			Player player = event.getPlayer();

			if (isInFight(player)) {
				Gladiator gladiator = getGladiator(player);

				if (event.getFrom().getY() - 120 > height)
					gladiator.handleEscape(true);
				else if (event.getFrom().getY() <= 118 && gladiator.time > 2)
					gladiator.handleEscape(true);
			}
		}

		@EventHandler
		public void onTeleportAll(TeleportAllEvent event) {
			for (Gladiator gladiator : gladiatorList)
				gladiator.handleEscape(false);
		}

		@EventHandler
		public void onExplode(EntityExplodeEvent event) {
			Iterator<Block> blockIt = event.blockList().iterator();

			while (blockIt.hasNext()) {
				Block b = (Block) blockIt.next();
				if (blockList.contains(b)) {
					blockIt.remove();
				}
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
