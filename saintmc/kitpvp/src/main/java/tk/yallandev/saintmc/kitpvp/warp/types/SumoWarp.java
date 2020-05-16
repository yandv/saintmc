package tk.yallandev.saintmc.kitpvp.warp.types;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.api.item.ActionItemStack;
import tk.yallandev.saintmc.bukkit.api.item.ActionItemStack.ActionType;
import tk.yallandev.saintmc.bukkit.api.item.ActionItemStack.Interact;
import tk.yallandev.saintmc.bukkit.api.item.ActionItemStack.InteractType;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.bukkit.api.scoreboard.Scoreboard;
import tk.yallandev.saintmc.bukkit.event.player.PlayerDamagePlayerEvent;
import tk.yallandev.saintmc.kitpvp.event.warp.PlayerWarpDeathEvent;
import tk.yallandev.saintmc.kitpvp.event.warp.PlayerWarpJoinEvent;
import tk.yallandev.saintmc.kitpvp.event.warp.PlayerWarpQuitEvent;
import tk.yallandev.saintmc.kitpvp.event.warp.PlayerWarpRespawnEvent;
import tk.yallandev.saintmc.kitpvp.listener.ScoreboardListener;
import tk.yallandev.saintmc.kitpvp.warp.Warp;
import tk.yallandev.saintmc.kitpvp.warp.challenge.Challenge;
import tk.yallandev.saintmc.kitpvp.warp.challenge.ChallengeType;
import tk.yallandev.saintmc.kitpvp.warp.challenge.shadow.ShadowChallenge;
import tk.yallandev.saintmc.kitpvp.warp.challenge.sumo.SumoChallenge;

public class SumoWarp extends Warp {
	
	private ActionItemStack normalChallenge;
	private ActionItemStack fastChallenge;

	private Map<Player, Map<Player, Map<ChallengeType, Challenge>>> challengeMap;
	private Map<Player, Challenge> playersIn1v1;
	private Set<Player> fastQueue;

	private Location firstLocation;
	private Location secondLocation;
	
	public SumoWarp() {
		super("sumo", BukkitMain.getInstance().getLocationFromConfig("sumo"));
		
		challengeMap = new HashMap<>();
		playersIn1v1 = new HashMap<>();
		fastQueue = new HashSet<>();

		normalChallenge = new ActionItemStack(
				new ItemBuilder().type(Material.BLAZE_ROD).name("§aSumo Normal").build(),
				new Interact(InteractType.PLAYER) {

					@Override
					public boolean onInteract(Player player, Entity entity, Block block, ItemStack item,
							ActionType action) {

						if (!inWarp(player))
							return false;

						if (entity == null)
							return false;

						if (!(entity instanceof Player))
							return false;

						Player target = (Player) entity;

						if (playersIn1v1.containsKey(target))
							return false;

						if (hasChallenge(player, target, ChallengeType.SUMO_NORMAL)) {
							Challenge challenge = getChallenge(player, target, ChallengeType.SUMO_NORMAL);

							if (!challenge.isExpired()) {
//								player.sendMessage(
//										MessageTag.SUMO_NORMAL_ACCEPT.tl().replace("%player%", target.getName()));
//								target.sendMessage(
//										MessageTag.SUMO_NORMAL_ACCEPTED.tl().replace("%player%", player.getName()));

								challenge.start(firstLocation, secondLocation);
								return false;
							}
						}

						if (hasChallenge(target, player, ChallengeType.SUMO_NORMAL)) {
							Challenge challenge = getChallenge(target, player, ChallengeType.SUMO_NORMAL);

							if (!challenge.isExpired()) {
//								player.sendMessage(MessageTag.SUMO_WAIT_TIME.tl().replace("%time%", "30 segundos"));
								return false;
							}
						}

						newChallenge(player, target, new SumoChallenge(player, target) {

							@Override
							public void start(Location firstLocation, Location secondLocation) {
								super.start(firstLocation, secondLocation);

								fastQueue.remove(getTarget());
								fastQueue.remove(getPlayer());

								playersIn1v1.put(getTarget(), this);
								playersIn1v1.put(getPlayer(), this);

								challengeMap.remove(getTarget());
								challengeMap.remove(getPlayer());
							}

							@Override
							public void finish(Player winner) {
								super.finish(winner);

								playersIn1v1.remove(getTarget());
								playersIn1v1.remove(getPlayer());

								getPlayer().teleport(getSpawnLocation());
								getTarget().teleport(getSpawnLocation());

								handleInventory(getPlayer());
								handleInventory(getTarget());
							}

						});

//						player.sendMessage(MessageTag.SUMO_NORMAL_SEND.tl().replace("%player%", target.getName()));
//						target.sendMessage(MessageTag.SUMO_NORMAL_SEND.tl().replace("%player%", player.getName()));
						return false;
					}
				});

		ActionItemStack itemStack = new ActionItemStack(new ItemBuilder().name("§aSumo Fast")
				.type(Material.INK_SACK).durability(10).build(), new Interact() {

					@Override
					public boolean onInteract(Player player, Entity entity, Block block, ItemStack item,
							ActionType action) {

						if (playersIn1v1.containsKey(player))
							return false;

						player.setItemInHand(fastChallenge.getItemStack());

						fastQueue.remove(player);
//						player.sendMessage(MessageTag.SUMO_FAST_STOP_SEARCHING.tl());
						return false;
					}

				});

		fastChallenge = new ActionItemStack(new ItemBuilder().name("§cSumo Fast")
				.type(Material.INK_SACK).durability(8).build(), new Interact() {

					@Override
					public boolean onInteract(Player player, Entity entity, Block block, ItemStack item,
							ActionType action) {
						if (!inWarp(player))
							return false;

						if (playersIn1v1.containsKey(player))
							return false;

						if (fastQueue.isEmpty()) {
							fastQueue.add(player);
							player.setItemInHand(itemStack.getItemStack());
//							player.sendMessage(MessageTag.SUMO_FAST_SEARCHING.tl());
						} else {
							Player target = fastQueue.stream().findFirst().orElse(null);
							Challenge challenge = new SumoChallenge(target, player) {

								@Override
								public void start(Location firstLocation, Location secondLocation) {
									super.start(firstLocation, secondLocation);

									fastQueue.remove(getTarget());
									fastQueue.remove(getPlayer());

									playersIn1v1.put(getTarget(), this);
									playersIn1v1.put(getPlayer(), this);

									challengeMap.remove(getTarget());
									challengeMap.remove(getPlayer());
								}

								@Override
								public void finish(Player winner) {
									super.finish(winner);

									playersIn1v1.remove(getTarget());
									playersIn1v1.remove(getPlayer());

									getPlayer().teleport(getSpawnLocation());
									getTarget().teleport(getSpawnLocation());

									handleInventory(getTarget());
									handleInventory(getPlayer());
								}

							};

//							player.sendMessage(MessageTag.SUMO_FAST_FOUND.tl().replace("%player%", target.getName()));
//							target.sendMessage(MessageTag.SUMO_FAST_FOUND.tl().replace("%player%", player.getName()));

							challenge.start(firstLocation, secondLocation);
						}

						return false;
					}
				});
		
		firstLocation = BukkitMain.getInstance().getLocationFromConfig("sumo.pos1");
		secondLocation = BukkitMain.getInstance().getLocationFromConfig("sumo.pos2");
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void entitydamage(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;

		Player player = (Player) event.getEntity();

		if (inWarp(player)) {
			if (playersIn1v1.containsKey(player)) {
				event.setCancelled(false);
			} else {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPlayerDamagePlayer(PlayerDamagePlayerEvent event) {
		Player player = event.getPlayer();
		Player damager = event.getDamager();

		if (inWarp(player) && inWarp(damager)) {
			if (playersIn1v1.containsKey(player) && playersIn1v1.containsKey(damager)) {
				event.setCancelled(false);
			} else {
				event.setCancelled(true);
			}
			
			event.setDamage(0.0D);
		}
	}

	@EventHandler
	public void onPlayerWarpDeath(PlayerWarpDeathEvent event) {
		Player player = event.getPlayer();

		if (!playersIn1v1.containsKey(player))
			return;

		Challenge challenge = playersIn1v1.get(player);

		challenge.finish(event.getKiller());
	}

	@EventHandler
	public void onPlayerWarpRespawn(PlayerWarpRespawnEvent event) {
		Player player = event.getPlayer();

		if (event.getWarp() != this)
			return;

		handleInventory(player);
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void drop(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		
		if (inWarp(player)) {
			if (playersIn1v1.containsKey(player)) {
				event.setCancelled(false);
			} else {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPlayerWarpJoin(PlayerWarpJoinEvent event) {
		Player player = event.getPlayer();

		if (event.getWarp() != this)
			return;

		handleInventory(player);
	}

	@EventHandler
	public void onPlayerWarpJoin(PlayerWarpQuitEvent event) {
		Player player = event.getPlayer();

		if (!inWarp(player))
			return;

		handleQuit(player);
	}

	public void handleInventory(Player player) {
		player.getInventory().clear();
		player.getInventory().setArmorContents(new ItemStack[4]);

		for (PotionEffect potion : player.getActivePotionEffects())
			player.removePotionEffect(potion.getType());

		player.setLevel(0);
		player.setFoodLevel(20);
		player.setHealth(20D);

		player.getInventory().setItem(3, normalChallenge.getItemStack());
		player.getInventory().setItem(5, fastChallenge.getItemStack());
	}

	public void handleQuit(Player player) {
		if (playersIn1v1.containsKey(player)) {
			ShadowChallenge challenge = (ShadowChallenge) playersIn1v1.get(player);
			Player winner = challenge.getPlayer();
			
			if (winner == player)
				winner = challenge.getTarget();
			
			challenge.finish(winner);
		}

		challengeMap.remove(player);
		fastQueue.remove(player);
	}
	
	@Override
	public Scoreboard getScoreboard() {
		return ScoreboardListener.SHADOW_SCOREBOARD;
	}

	@Override
	public ItemStack getItem() {
		return new ItemBuilder().name("§aSumo")
				.lore("")
				.type(Material.LEASH).build();
	}
	
	public void newChallenge(Player player, Player target, Challenge challenge) {
		Map<Player, Map<ChallengeType, Challenge>> map = challengeMap.computeIfAbsent(player, m -> new HashMap<>());
		Map<ChallengeType, Challenge> challenges = map.computeIfAbsent(target, m -> new HashMap<>());

		challenges.put(challenge.getChallengeType(), challenge);
	}

	public Challenge getChallenge(Player player, Player target, ChallengeType type) {
		return challengeMap.get(target).get(player).get(type);
	}

	private boolean hasChallenge(Player player, Player target, ChallengeType type) {
		return challengeMap.containsKey(target) && challengeMap.get(target).containsKey(player)
				&& challengeMap.get(target).get(player).containsKey(type);
	}

}
