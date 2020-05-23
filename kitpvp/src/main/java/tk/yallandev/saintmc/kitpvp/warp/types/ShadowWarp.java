package tk.yallandev.saintmc.kitpvp.warp.types;

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
import tk.yallandev.saintmc.bukkit.api.scoreboard.Score;
import tk.yallandev.saintmc.bukkit.api.scoreboard.Scoreboard;
import tk.yallandev.saintmc.bukkit.event.player.PlayerDamagePlayerEvent;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent;
import tk.yallandev.saintmc.common.utils.DateUtils;
import tk.yallandev.saintmc.common.utils.string.StringUtils;
import tk.yallandev.saintmc.kitpvp.GameMain;
import tk.yallandev.saintmc.kitpvp.event.challenge.shadow.ShadowSearchingStartEvent;
import tk.yallandev.saintmc.kitpvp.event.challenge.shadow.ShadowSearchingStopEvent;
import tk.yallandev.saintmc.kitpvp.event.warp.PlayerWarpDeathEvent;
import tk.yallandev.saintmc.kitpvp.event.warp.PlayerWarpJoinEvent;
import tk.yallandev.saintmc.kitpvp.event.warp.PlayerWarpQuitEvent;
import tk.yallandev.saintmc.kitpvp.event.warp.PlayerWarpRespawnEvent;
import tk.yallandev.saintmc.kitpvp.listener.ScoreboardListener;
import tk.yallandev.saintmc.kitpvp.warp.DuelWarp;
import tk.yallandev.saintmc.kitpvp.warp.Warp;
import tk.yallandev.saintmc.kitpvp.warp.challenge.Challenge;
import tk.yallandev.saintmc.kitpvp.warp.challenge.ChallengeType;
import tk.yallandev.saintmc.kitpvp.warp.challenge.shadow.ShadowChallenge;

public class ShadowWarp extends Warp implements DuelWarp {

	private ActionItemStack normalChallenge;
	private ActionItemStack fastChallenge;
	private ActionItemStack customChallenge;

	private Map<Player, Map<Player, Map<ChallengeType, Challenge>>> challengeMap;
	private Map<Player, Challenge> playersIn1v1;
	private Map<Player, Long> fastQueue;

	private Location firstLocation;
	private Location secondLocation;

	public ShadowWarp() {
		super("1v1", BukkitMain.getInstance().getLocationFromConfig("shadow"));

		challengeMap = new HashMap<>();
		playersIn1v1 = new HashMap<>();
		fastQueue = new HashMap<>();

		customChallenge = new ActionItemStack(
				new ItemBuilder().name("§a1v1 Custom").type(Material.IRON_FENCE).build(),
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
				new ItemBuilder().type(Material.BLAZE_ROD).name("§a1v1 Normal").build(),
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

						if (hasChallenge(player, target, ChallengeType.SHADOW_NORMAL)) {
							Challenge challenge = getChallenge(player, target, ChallengeType.SHADOW_NORMAL);

							if (!challenge.isExpired()) {
								player.sendMessage("§a§l> §fVocê foi desafiado para §a1v1 normal§f pelo §a " + target.getName() + "§f!");
								target.sendMessage("§a§l> §fVocê foi desafiado para §a1v1 normal§f pelo §a " + player.getName() + "§f!");

								challenge.start(firstLocation, secondLocation);
								return false;
							}
						}

						if (hasChallenge(target, player, ChallengeType.SHADOW_NORMAL)) {
							Challenge challenge = getChallenge(target, player, ChallengeType.SHADOW_NORMAL);

							if (!challenge.isExpired()) {
								player.sendMessage("§c§l> §fEspere §e" + DateUtils.getTime(challenge.getExpire()) + "§f para enviar outro desafio!");
								return false;
							}
						}
	
						newChallenge(player, target, new ShadowChallenge(player, target, ChallengeType.SHADOW_NORMAL) {

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

						player.sendMessage("§a§l> §fVocê enviou um desafio de §a1v1 normal§f para o §a" + target.getName() + "§f!");
						target.sendMessage("§a§l> §fO jogador §a" + player.getName() + "§f enviou um desafio de §a1v1 normal§f para você!");
						return false;
					}
				});

		ActionItemStack itemStack = new ActionItemStack(new ItemBuilder().name("§a1v1 Fast")
				.type(Material.INK_SACK).durability(10).build(), new Interact() {

					@Override
					public boolean onInteract(Player player, Entity entity, Block block, ItemStack item,
							ActionType action) {

						if (playersIn1v1.containsKey(player))
							return false;
						
						ShadowSearchingStopEvent searchingStopEvent = new ShadowSearchingStopEvent(player);
						
						Bukkit.getPluginManager().callEvent(searchingStopEvent);

						if (!searchingStopEvent.isCancelled()) {
							player.setItemInHand(fastChallenge.getItemStack());

							fastQueue.remove(player);
							player.sendMessage("§a§l> §fVocê §csaiu§f na fila do §a1v1 rápido§f!");
						}
						return false;
					}

				});

		fastChallenge = new ActionItemStack(new ItemBuilder().name("§c1v1 Fast")
				.type(Material.INK_SACK).durability(8).build(), new Interact() {

					@Override
					public boolean onInteract(Player player, Entity entity, Block block, ItemStack item,
							ActionType action) {
						if (!inWarp(player))
							return false;

						if (playersIn1v1.containsKey(player))
							return false;
						
						ShadowSearchingStartEvent searchingStartEvent = new ShadowSearchingStartEvent(player);
						
						Bukkit.getPluginManager().callEvent(searchingStartEvent);
						
						if (searchingStartEvent.isCancelled())
							return false;

						if (fastQueue.isEmpty()) {
							fastQueue.put(player, System.currentTimeMillis());
							player.setItemInHand(itemStack.getItemStack());
							player.sendMessage("§a§l> §fVocê §aentrou§f na fila do §a1v1 rápido§f!");
						} else {
							Player target = fastQueue.keySet().stream().findFirst().orElse(null);
							Challenge challenge = new ShadowChallenge(target, player, ChallengeType.SHADOW_FAST) {

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
	
	@EventHandler
	public void asdk(UpdateEvent event) {
		fastQueue.forEach((player, time) -> {
			ScoreboardListener.SEARCHING_SCOREBOARD.updateScore(player, new Score("§fTempo: §a" + StringUtils.formatTime((int)(System.currentTimeMillis() - time ) / 1000), "time"));
		});
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

		player.getInventory().setItem(3, fastChallenge.getItemStack());
		player.getInventory().setItem(4, normalChallenge.getItemStack());
		player.getInventory().setItem(5, customChallenge.getItemStack());
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
		return new ItemBuilder().name("§a1v1")
				.lore("\n§7Arena 1v1 sem interrupções.\n\n§a" + GameMain.getInstance().getGamerManager().filter(gamer -> gamer.getWarp() == this).size() + " jogadores")
				.type(Material.BLAZE_ROD).build();
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

	@Override
	public void setFirstLocation(Location location) {
		firstLocation = location;
	}

	@Override
	public void setSecondLocation(Location location) {
		secondLocation = location;	
	}

}
