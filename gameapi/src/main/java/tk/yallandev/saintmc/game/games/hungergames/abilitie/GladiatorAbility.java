package tk.yallandev.saintmc.game.games.hungergames.abilitie;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import tk.yallandev.saintmc.game.GameMain;
import tk.yallandev.saintmc.game.ability.AbilityRarity;
import tk.yallandev.saintmc.game.constructor.Ability;
import tk.yallandev.saintmc.game.constructor.CustomOption;
import tk.yallandev.saintmc.game.games.hungergames.HungerGamesMode;
import tk.yallandev.saintmc.game.games.hungergames.abilitie.constructor.GladiatorFightController;
import tk.yallandev.saintmc.game.interfaces.Disableable;
import tk.yallandev.saintmc.game.stage.GameStage;

public class GladiatorAbility extends Ability implements Disableable {
	
	private GladiatorFightController gladiatorFightController;

	public GladiatorAbility() {
		super(new ItemStack(Material.IRON_FENCE), AbilityRarity.LEGENDARY);
		this.gladiatorFightController = new GladiatorFightController();
		options.put("ITEM", new CustomOption("ITEM", new ItemStack(Material.IRON_FENCE), "§aGladiator"));
	}
	
	@EventHandler
	public void onPlayerInteractEntityListener(PlayerInteractEntityEvent e) {
		Player player = e.getPlayer();
		
		if ((e.getPlayer().getItemInHand() != null) && (e.getPlayer().getItemInHand().getType() == Material.IRON_FENCE) && (hasAbility(player)) && ((e.getRightClicked() instanceof Player))) {
			Player t = (Player) e.getRightClicked();
			
			if (GameStage.isInvincibility(GameMain.getPlugin().getGameStage()))
				return;
			
			e.setCancelled(true);
			
			if (!gladiatorFightController.isInFight(e.getPlayer())) {
				if (!gladiatorFightController.isInFight(t)) {
					new Gladiator(e.getPlayer(), t);
				}
			}
		}
	}

	@EventHandler
	public void onPlayerInteractListener(PlayerInteractEvent e) {
		Player player = e.getPlayer();
		if ((e.getAction() != Action.PHYSICAL) && (hasAbility(player)) && (e.getPlayer().getItemInHand() != null)
				&& (e.getPlayer().getItemInHand().getType() == Material.IRON_FENCE)) {
			e.getPlayer().updateInventory();
			e.setCancelled(true);
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onBlock(BlockDamageEvent event) {
		if (gladiatorFightController.isFightBlock(event.getBlock())) {
			Block b = event.getBlock();
			if (b.getType() == Material.GLASS) {
				Player p = event.getPlayer();
				p.sendBlockChange(b.getLocation(), Material.BEDROCK, (byte) 0);
			}
		}
	}

	@EventHandler
	public void onExplode(EntityExplodeEvent event) {
		Iterator<Block> blockIt = event.blockList().iterator();
		
		while (blockIt.hasNext())
			if (gladiatorFightController.isFightBlock(blockIt.next()))
				blockIt.remove();
	}

	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		if (gladiatorFightController.isFightBlock(event.getBlock())) {
			if (event.getBlock().getType() == Material.GLASS) {
				event.setCancelled(true);
			}
		}
	}
	
	public class Gladiator {
		
		private HungerGamesMode main;
		private Player gladiator;
		private Player target;
		
		private Location tpLocGladiator;
		private Location tpLocTarget;
		
		private BukkitRunnable witherEffect;
		private BukkitRunnable teleportBack;
		
		private List<Block> blocksToRemove;
		private Listener listener;
		
		private boolean ended;

		public Gladiator(Player gladiator, Player target) {
			this.main = (HungerGamesMode) GameMain.getPlugin().getGameMode();
			this.gladiator = gladiator;
			this.target = target;
			this.blocksToRemove = new ArrayList<>();
			send1v1();
			this.ended = false;
			this.listener = new Listener() {

				@EventHandler(priority = EventPriority.LOWEST)
				public void onDeath(PlayerDeathEvent e) {
					if (e.getEntity() instanceof Player) {
						if (e.getEntity().getKiller() instanceof Player) {
							if ((isIn1v1(e.getEntity())) && (!ended)) {
								ended = true;
								
								if (e.getEntity() == gladiator) {
									target.sendMessage("§a§l> §fVocê §aganhou§f o gladiator!");
									target.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 100000));
									teleportBack(target, gladiator);
									return;
								}
								
								gladiator.sendMessage("§a§l> §fVocê §aganhou§f o gladiator!");
								gladiator.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 100000));
								teleportBack(gladiator, target);
							}
						} else {
							if ((isIn1v1(e.getEntity())) && (!ended)) {
								ended = true;
								
								if (e.getEntity() == gladiator) {
									target.sendMessage("§a§l> §fVocê §aganhou§f o gladiator!");
									target.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 100000));
									teleportBack(target, gladiator);
									return;
								}
								
								if (e.getEntity() == target) {
									gladiator.sendMessage("§a§l> §fVocê §aganhou§f o gladiator!");
									gladiator.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 100000));
									teleportBack(gladiator, target);
									return;
								}
							}
						}
					}
				}

				@EventHandler
				public void onQuit(PlayerQuitEvent e) {
					Player p = e.getPlayer();
					if (!isIn1v1(p)) 
						return;
					
					if (e.getPlayer().isDead())
						return;
					
					if (!ended) {
						ended = true;
						
						if (p == gladiator) {
							target.sendMessage("§a§l> §fVocê §aganhou§f o gladiator!");
							target.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 100000));
							teleportBack(target, gladiator);
							return;
						}
						
						gladiator.sendMessage("§a§l> §fVocê §aganhou§f o gladiator!");
						gladiator.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 100000));
						teleportBack(gladiator, target);
					}
				}

				@EventHandler
				public void onKick(PlayerKickEvent event) {
					Player p = event.getPlayer();
					
					if (!isIn1v1(p))
						return;
					
					if (event.getPlayer().isDead()) 
						return;
					
					if (!ended) {
						ended = true;
						
						if (p == gladiator) {
							target.sendMessage("§a§l> §fVocê §aganhou§f o gladiator!");
							target.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 100000));
							teleportBack(target, gladiator);
							return;
						}
						
						gladiator.sendMessage("§a§l> §fVocê §aganhou§f o gladiator!");
						gladiator.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 100000));
						teleportBack(gladiator, target);
					}
				}
			};
			main.getServer().getPluginManager().registerEvents(listener, main.getGameMain());
		}
		
		public boolean isIn1v1(Player player) {
			return (player == gladiator) || (player == target);
		}

		public void destroy() {
			HandlerList.unregisterAll(listener);
		}

		public void send1v1() {
			Location loc = gladiator.getLocation();
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
			generateBlocks(mainBlock);
			
			tpLocGladiator = gladiator.getLocation().clone();
			tpLocTarget = target.getLocation().clone();
			
			gladiator.sendMessage("§a§l> §fVocê puxou o §a" + target.getName() + "§f no gladiator!");
			gladiator.sendMessage("§a§l> §fVocá está invencível por §a5 segundos§f!");
			
			target.sendMessage("§a§l> §fVocê foi puxado no §a" + gladiator.getName() + "§f no gladiator!");
			target.sendMessage("§a§l> §fVocê está invencível por §a5 segundos§f!");
			
			Location l = new Location(mainBlock.getWorld(), mainBlock.getX() + 6.5D, 121.0D, mainBlock.getZ() + 6.5D);
			l.setYaw(135.0F);
			
			target.teleport(l);
			
			Location l2 = new Location(mainBlock.getWorld(), mainBlock.getX() - 5.5D, 121.0D, mainBlock.getZ() - 5.5D);
			l2.setYaw(315.0F);
			
			gladiator.teleport(l2);
			
			gladiatorFightController.addPlayerToFights(gladiator.getUniqueId());
			gladiatorFightController.addPlayerToFights(target.getUniqueId());
			
			gladiator.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 100000));
			target.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 100000));
			
			(witherEffect = new BukkitRunnable() {
				public void run() {
					gladiator.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 1200, 5));
					target.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 1200, 5));
				}
			}).runTaskLater(main.getGameMain(), 2400L);
			
			(teleportBack = new BukkitRunnable() {
				public void run() {
					teleportBack(tpLocGladiator, tpLocTarget);
				}
			}).runTaskLater(main.getGameMain(), 3600L);
		}

		public void teleportBack(Location loc, Location loc1) {
			gladiatorFightController.removePlayerFromFight(gladiator.getUniqueId());
			gladiatorFightController.removePlayerFromFight(target.getUniqueId());
			
			gladiator.teleport(loc);
			target.teleport(loc1);
			
			removeBlocks();
			
			gladiator.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 100000));
			gladiator.removePotionEffect(PotionEffectType.WITHER);
			
			target.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 100000));
			target.removePotionEffect(PotionEffectType.WITHER);
			stop();
			destroy();
		}

		public void teleportBack(Player winner, Player loser) {
			gladiatorFightController.removePlayerFromFight(winner.getUniqueId());
			gladiatorFightController.removePlayerFromFight(loser.getUniqueId());
			
			winner.teleport(tpLocGladiator);
			removeBlocks();
			
			winner.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 100000));
			winner.removePotionEffect(PotionEffectType.WITHER);
			
			stop();
			destroy();
		}

		public void generateBlocks(Block mainBlock) {
			for (double x = -8.0D; x <= 8.0D; x += 1.0D) {
				for (double z = -8.0D; z <= 8.0D; z += 1.0D) {
					for (double y = 0.0D; y <= 9.0D; y += 1.0D) {
						Location l = new Location(mainBlock.getWorld(), mainBlock.getX() + x, 120.0D + y, mainBlock.getZ() + z);
						l.getBlock().setType(Material.GLASS);
						gladiatorFightController.addBlock(l.getBlock());
						blocksToRemove.add(l.getBlock());
					}
				}
			}
			
			for (double x = -7.0D; x <= 7.0D; x += 1.0D) {
				for (double z = -7.0D; z <= 7.0D; z += 1.0D) {
					for (double y = 1.0D; y <= 8.0D; y += 1.0D) {
						Location l = new Location(mainBlock.getWorld(), mainBlock.getX() + x, 120.0D + y, mainBlock.getZ() + z);
						l.getBlock().setType(Material.AIR);
					}
				}
			}
		}

		public void removeBlocks() {
			for (Block b : blocksToRemove) {
				if ((b.getType() != null) && (b.getType() != Material.AIR)) {
					b.setType(Material.AIR);
				}
				gladiatorFightController.removeBlock(b);
			}
			blocksToRemove.clear();
		}

		public void stop() {
			witherEffect.cancel();
			teleportBack.cancel();
		}
	}
	
	@Override
	public int getPowerPoints(HashMap<String, CustomOption> map) {
		return 0;
	}

}
