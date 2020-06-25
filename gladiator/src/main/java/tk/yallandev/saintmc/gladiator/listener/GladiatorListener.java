package tk.yallandev.saintmc.gladiator.listener;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.api.item.ActionItemStack;
import tk.yallandev.saintmc.bukkit.api.item.ActionItemStack.ActionType;
import tk.yallandev.saintmc.bukkit.api.item.ActionItemStack.Interact;
import tk.yallandev.saintmc.bukkit.api.item.ActionItemStack.InteractType;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.bukkit.event.player.PlayerDamagePlayerEvent;
import tk.yallandev.saintmc.common.utils.DateUtils;
import tk.yallandev.saintmc.gladiator.challenge.Challenge;
import tk.yallandev.saintmc.gladiator.challenge.ChallengeType;
import tk.yallandev.saintmc.gladiator.event.shadow.ShadowSearchingStartEvent;
import tk.yallandev.saintmc.gladiator.event.shadow.ShadowSearchingStopEvent;

public class GladiatorListener implements Listener {

	private ActionItemStack normalChallenge;
	private ActionItemStack fastChallenge;
	private ActionItemStack customChallenge;

	private Map<Player, Map<Player, Map<ChallengeType, Challenge>>> challengeMap;
	private Map<Player, Challenge> playersInGladiator;
	private Map<Player, Long> fastQueue;

	private Location firstLocation;
	private Location secondLocation;

	public GladiatorListener() {
		challengeMap = new HashMap<>();
		playersInGladiator = new HashMap<>();
		fastQueue = new HashMap<>();

		customChallenge = new ActionItemStack(
				new ItemBuilder().name("§aGladiator Custom").glow().type(Material.IRON_FENCE).build(),
				new Interact(InteractType.PLAYER) {

					@Override
					public boolean onInteract(Player player, Entity entity, Block block, ItemStack item,
							ActionType action) {

						if (action == ActionType.CLICK_PLAYER)
							player.sendMessage("§aEm desenvolvimento!");

						return false;
					}
				});

		normalChallenge = new ActionItemStack(
				new ItemBuilder().type(Material.IRON_FENCE).name("§aGladiator Normal").build(),
				new Interact(InteractType.PLAYER) {

					@Override
					public boolean onInteract(Player player, Entity entity, Block block, ItemStack item,
							ActionType action) {

						if (entity == null)
							return false;

						if (!(entity instanceof Player))
							return false;

						Player target = (Player) entity;

						if (playersInGladiator.containsKey(target))
							return false;

						if (hasChallenge(player, target, ChallengeType.SHADOW_NORMAL)) {
							Challenge challenge = getChallenge(player, target, ChallengeType.SHADOW_NORMAL);

							if (!challenge.isExpired()) {
								player.sendMessage("§a§l> §fVocê foi desafiado para §aGladiator normal§f pelo §a "
										+ target.getName() + "§f!");
								target.sendMessage("§a§l> §fVocê foi desafiado para §aGladiator normal§f pelo §a "
										+ player.getName() + "§f!");

								challenge.start(firstLocation, secondLocation);
								return false;
							}
						}

						if (hasChallenge(target, player, ChallengeType.SHADOW_NORMAL)) {
							Challenge challenge = getChallenge(target, player, ChallengeType.SHADOW_NORMAL);

							if (!challenge.isExpired()) {
								player.sendMessage("§c§l> §fEspere §e" + DateUtils.getTime(challenge.getExpire())
										+ "§f para enviar outro desafio!");
								return false;
							}
						}

						newChallenge(player, target, new Challenge(player, target, ChallengeType.SHADOW_NORMAL) {

							@Override
							public void start(Location firstLocation, Location secondLocation) {
								fastQueue.remove(getTarget());
								fastQueue.remove(getPlayer());

								playersInGladiator.put(getTarget(), this);
								playersInGladiator.put(getPlayer(), this);

								challengeMap.remove(getTarget());
								challengeMap.remove(getPlayer());

								super.start(firstLocation, secondLocation);
							}

							@Override
							public void finish(Player winner) {
								playersInGladiator.remove(getTarget());
								playersInGladiator.remove(getPlayer());

								getPlayer().teleport(getSpawnLocation());
								getTarget().teleport(getSpawnLocation());

								handleInventory(getPlayer());
								handleInventory(getTarget());

								super.start(firstLocation, secondLocation);
							}

						});

						player.sendMessage("§a§l> §fVocê enviou um desafio de §aGladiator normal§f para o §a"
								+ target.getName() + "§f!");
						target.sendMessage("§a§l> §fO jogador §a" + player.getName()
								+ "§f enviou um desafio de §aGladiator normal§f para você!");
						return false;
					}
				});

		ActionItemStack itemStack = new ActionItemStack(
				new ItemBuilder().name("§aGladiator Fast").type(Material.INK_SACK).durability(10).build(),
				new Interact() {

					@Override
					public boolean onInteract(Player player, Entity entity, Block block, ItemStack item,
							ActionType action) {

						if (playersInGladiator.containsKey(player))
							return false;

						ShadowSearchingStopEvent searchingStopEvent = new ShadowSearchingStopEvent(player);

						Bukkit.getPluginManager().callEvent(searchingStopEvent);

						if (!searchingStopEvent.isCancelled()) {
							player.setItemInHand(fastChallenge.getItemStack());

							fastQueue.remove(player);
							player.sendMessage("§a§l> §fVocê §csaiu§f na fila do §aGladiator rápido§f!");
						}
						return false;
					}

				});

		fastChallenge = new ActionItemStack(
				new ItemBuilder().name("§cGladiator Fast").type(Material.INK_SACK).durability(8).build(),
				new Interact() {

					@Override
					public boolean onInteract(Player player, Entity entity, Block block, ItemStack item,
							ActionType action) {
						if (playersInGladiator.containsKey(player))
							return false;

						ShadowSearchingStartEvent searchingStartEvent = new ShadowSearchingStartEvent(player);

						Bukkit.getPluginManager().callEvent(searchingStartEvent);

						if (searchingStartEvent.isCancelled())
							return false;

						if (fastQueue.isEmpty()) {
							fastQueue.put(player, System.currentTimeMillis());
							player.setItemInHand(itemStack.getItemStack());
							player.sendMessage("§a§l> §fVocê §aentrou§f na fila do §aGladiator rápido§f!");
						} else {
							Player target = fastQueue.keySet().stream().findFirst().orElse(null);
							Challenge challenge = new Challenge(target, player, ChallengeType.SHADOW_FAST) {

								@Override
								public void start(Location firstLocation, Location secondLocation) {
									fastQueue.remove(getTarget());
									fastQueue.remove(getPlayer());

									playersInGladiator.put(getTarget(), this);
									playersInGladiator.put(getPlayer(), this);

									challengeMap.remove(getTarget());
									challengeMap.remove(getPlayer());

									super.start(firstLocation, secondLocation);
								}

								@Override
								public void finish(Player winner) {
									playersInGladiator.remove(getTarget());
									playersInGladiator.remove(getPlayer());

									getPlayer().teleport(getSpawnLocation());
									getTarget().teleport(getSpawnLocation());

									handleInventory(getTarget());
									handleInventory(getPlayer());

									super.finish(winner);
								}

							};

							player.sendMessage("§a§l> §fVocê batalhará contra o §a" + target.getName() + "§f!");
							target.sendMessage("§a§l> §fVocê batalhará contra o §a" + player.getName() + "§f!");

							challenge.start(firstLocation, secondLocation);
						}

						return false;
					}
				});

		firstLocation = BukkitMain.getInstance().getLocationFromConfig("shadow.pos1");
		secondLocation = BukkitMain.getInstance().getLocationFromConfig("shadow.pos2");
	}

//	@EventHandler
//	public void asdk(UpdateEvent event) {
//		fastQueue.forEach((player, time) -> {
//			ScoreboardListener.SEARCHING_SCOREBOARD.updateScore(player, new Score("§fTempo: §a" + StringUtils.formatTime((int)(System.currentTimeMillis() - time ) / 1000), "time"));
//		});
//	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void entitydamage(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;

		Player player = (Player) event.getEntity();

		if (playersInGladiator.containsKey(player)) {
			event.setCancelled(false);
		} else {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerDamagePlayer(PlayerDamagePlayerEvent event) {
		Player player = event.getPlayer();
		Player damager = event.getDamager();

		if (playersInGladiator.containsKey(player) && playersInGladiator.containsKey(damager)) {
			event.setCancelled(false);
		} else {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerWarpDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();

		player.setHealth(20);
		player.setFoodLevel(20);
		
		for (PotionEffect potion : player.getActivePotionEffects())
			player.removePotionEffect(potion.getType());
		
		handleInventory(player);

		if (!playersInGladiator.containsKey(player))
			return;

		Challenge challenge = playersInGladiator.get(player);

		challenge.finish(player.getKiller());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void drop(PlayerDropItemEvent event) {
		Player player = event.getPlayer();

		if (playersInGladiator.containsKey(player)) {
			event.setCancelled(false);
		} else {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		handleInventory(player);
	}

	@EventHandler
	public void onPlayerJoin(PlayerQuitEvent event) {
		Player player = event.getPlayer();

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

		player.getInventory().setItem(3, fastChallenge.getItemStack());
		player.getInventory().setItem(4, normalChallenge.getItemStack());
		player.getInventory().setItem(5, customChallenge.getItemStack());
	}

	public void handleQuit(Player player) {
		if (playersInGladiator.containsKey(player)) {
			Challenge challenge = playersInGladiator.get(player);
			Player winner = challenge.getPlayer();

			if (winner == player)
				winner = challenge.getTarget();

			challenge.finish(winner);
		}

		challengeMap.remove(player);
		fastQueue.remove(player);
	}

	public void newChallenge(Player player, Player target, Challenge challenge) {
		Map<Player, Map<ChallengeType, Challenge>> map = challengeMap.computeIfAbsent(player, m -> new HashMap<>());
		Map<ChallengeType, Challenge> challenges = map.computeIfAbsent(target, m -> new HashMap<>());

		challenges.put(challenge.getChallengeType(), challenge);
	}

	public Challenge getChallenge(Player player, Player target, ChallengeType type) {
		return challengeMap.get(target).get(player).get(type);
	}

	private Location getSpawnLocation() {
		return BukkitMain.getInstance().getLocationFromConfig("spawn");
	}

	private boolean hasChallenge(Player player, Player target, ChallengeType type) {
		return challengeMap.containsKey(target) && challengeMap.get(target).containsKey(player)
				&& challengeMap.get(target).get(player).containsKey(type);
	}

	public void setFirstLocation(Location location) {
		firstLocation = location;
	}

	public void setSecondLocation(Location location) {
		secondLocation = location;
	}

}
