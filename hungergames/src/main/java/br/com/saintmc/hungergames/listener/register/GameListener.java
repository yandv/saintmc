package br.com.saintmc.hungergames.listener.register;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffect;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.GameMain;
import br.com.saintmc.hungergames.abilities.Ability;
import br.com.saintmc.hungergames.constructor.Gamer;
import br.com.saintmc.hungergames.constructor.Timeout;
import br.com.saintmc.hungergames.event.game.GameStartEvent;
import br.com.saintmc.hungergames.event.kit.PlayerSelectedKitEvent;
import br.com.saintmc.hungergames.event.player.PlayerItemReceiveEvent;
import br.com.saintmc.hungergames.game.GameState;
import br.com.saintmc.hungergames.kit.Kit;
import br.com.saintmc.hungergames.kit.KitType;
import br.com.saintmc.hungergames.utils.ServerConfig;
import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.api.vanish.AdminMode;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent.UpdateType;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.permission.Group;

@SuppressWarnings("deprecation")
public class GameListener extends br.com.saintmc.hungergames.listener.GameListener {
	
	private Map<UUID, Long> compassMap;
	private Set<UUID> joined;

	public GameListener() {
		compassMap = new HashMap<>();
		joined = new HashSet<>();

		ItemStack soup = new ItemStack(Material.MUSHROOM_SOUP);
		newShapelessRecipe(soup, Arrays.asList(new MaterialData(Material.CACTUS), new MaterialData(Material.BOWL)));
		newShapelessRecipe(soup,
				Arrays.asList(new MaterialData(Material.INK_SACK, (byte) 3), new MaterialData(Material.BOWL)));
		newShapelessRecipe(soup, Arrays.asList(new MaterialData(Material.PUMPKIN_SEEDS),
				new MaterialData(Material.PUMPKIN_SEEDS), new MaterialData(Material.BOWL)));
		newShapelessRecipe(soup, Arrays.asList(new MaterialData(Material.YELLOW_FLOWER),
				new MaterialData(Material.RED_ROSE), new MaterialData(Material.BOWL)));
	}

	public void newShapelessRecipe(ItemStack result, List<MaterialData> materials) {
		ShapelessRecipe recipe = new ShapelessRecipe(result);

		for (MaterialData mat : materials) {
			recipe.addIngredient(mat);
		}

		Bukkit.addRecipe(recipe);
	}

	/*
	 * player Game
	 */

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerJoin(PlayerLoginEvent event) {
		Gamer gamer = GameGeneral.getInstance().getGamerController().getGamer(event.getPlayer());
		Member player = CommonGeneral.getInstance().getMemberManager().getMember(event.getPlayer().getUniqueId());

		if (isPregame())
			return;

		if (GameGeneral.getInstance().getTimeoutController().containsKey(event.getPlayer().getUniqueId())) {
			event.allow();
			return;
		}

		if (player.hasGroupPermission(Group.TRIAL))
			return;

		if (player.hasGroupPermission(ServerConfig.getInstance().getSpectatorGroup()) && GameMain.SPECTATOR)
			return;

		if (player.hasGroupPermission(ServerConfig.getInstance().getRespawnGroup()) && !joined.contains(event.getPlayer().getUniqueId())
				&& getGameGeneral().getTime() < 300)
			return;

		if (gamer.isTimeout()) {
			event.disallow(Result.KICK_OTHER, "§cVocê demorou muito para relogar e foi desclassificado!");
			return;
		}

		if (gamer.getDeathCause() == null) {
			event.disallow(Result.KICK_OTHER,
					player.hasGroupPermission(Group.LIGHT) ? "§cO jogo já iniciou e você não pode espectar!"
							: "§cO jogo já iniciou!");
		} else {
			event.disallow(Result.KICK_OTHER, "§cVocê demorou muito para relogar e foi desclassificado!");
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		Gamer gamer = GameGeneral.getInstance().getGamerController().getGamer(player);

		if (GameGeneral.getInstance().getTimeoutController().containsKey(player.getUniqueId())) {
			GameGeneral.getInstance().getTimeoutController().unload(player.getUniqueId());
			event.setJoinMessage("§e" + player.getName() + " entrou do torneio.");
			return;
		}

		if (getGameGeneral().getTime() <= 300 && !joined.contains(player.getUniqueId())) {
			player.getInventory().clear();

			for (PotionEffect effect : player.getActivePotionEffects())
				player.removePotionEffect(effect.getType());

			joined.add(player.getUniqueId());
			event.setJoinMessage(null);
			player.sendMessage("§aVocê entrou na partida!");
			
			if (Member.hasGroupPermission(player.getUniqueId(), Group.LIGHT)) {
				if (!gamer.hasKit(KitType.PRIMARY))
					gamer.setNoKit(KitType.PRIMARY);
				
				if (!gamer.hasKit(KitType.SECONDARY))
					gamer.setNoKit(KitType.SECONDARY);
			}
			
			gamer.setGame(GameMain.GAME);
			gamer.getStatus().addMatch();
			Bukkit.getPluginManager().callEvent(new PlayerItemReceiveEvent(player));
		} else {
			event.setJoinMessage(null);

			if (Member.hasGroupPermission(player.getUniqueId(), Group.TRIAL))
				AdminMode.getInstance().setAdmin(player, Member.getMember(player.getUniqueId()));
			else
				gamer.setSpectator(true);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		Gamer gamer = GameGeneral.getInstance().getGamerController().getGamer(player);

		if (gamer.isNotPlaying()) {
			event.setQuitMessage(null);
			return;
		}

		GameGeneral.getInstance().getTimeoutController().load(player.getUniqueId(),
				new Timeout(System.currentTimeMillis() + 50000, player.getLocation(),
						player.getInventory().getContents(), player.getInventory().getArmorContents()));
		event.setQuitMessage("§e" + player.getName() + " saiu do torneio.");
	}

	@EventHandler
	public void onRespawn(PlayerRespawnEvent event) {
		Player p = event.getPlayer();
		event.setRespawnLocation(p.getLocation());
		Member player = CommonGeneral.getInstance().getMemberManager().getMember(p.getUniqueId());
		Gamer gamer = GameGeneral.getInstance().getGamerController().getGamer(p.getUniqueId());

		if (player.hasGroupPermission(Group.TRIAL)) {
			gamer.setGamemaker(true);

			if (!AdminMode.getInstance().isAdmin(p))
				AdminMode.getInstance().setAdmin(p, player);
		} else {
			gamer.setSpectator(true);
		}
	}
	
	@EventHandler
	public void onPlayerItemReceive(PlayerItemReceiveEvent event) {
		Player player = event.getPlayer();
		Gamer gamer = GameGeneral.getInstance().getGamerController().getGamer(player);
		
		/*
		 * Check if has default kit
		 */
		
		if (ServerConfig.getInstance().hasDefaultSimpleKit()) {
			ServerConfig.getInstance().getDefaultSimpleKit().applySilent(player);
		}
		
		/*
		 * Add compass
		 */
		
		player.getInventory().addItem(new ItemStack(Material.COMPASS));
		
		/*
		 * Add Kit item
		 */
		
		if (GameState.isInvincibility(GameGeneral.getInstance().getGameState())) {
			
			for (Kit kit : gamer.getKitMap().values()) {
				for (Ability ability : kit.getAbilities()) {
					for (ItemStack item : ability.getItemList()) {
						player.getInventory().addItem(item);
					}
				}
			}
			
		} else {
			Member member = CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId());
			
			if (member.hasGroupPermission(ServerConfig.getInstance().getKitSpawnGroup()))
				for (Kit kit : gamer.getKitMap().values()) {
					for (Ability ability : kit.getAbilities()) {
						for (ItemStack item : ability.getItemList()) {
							player.getInventory().addItem(item);
						}
					}
				}
		}
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;

		if (GameGeneral.getInstance().getGameState() == GameState.WINNING)
			event.setCancelled(true);
	}

	/*
	 * General Events
	 */

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player p = event.getPlayer();

		ItemStack item = event.getItem();

		if (item == null || item.getType() == Material.AIR)
			return;

		if (item.getType() == Material.COMPASS) {
			if (compassMap.containsKey(p.getUniqueId()) && compassMap.get(p.getUniqueId()) > System.currentTimeMillis())
				return;

			Player target = null;
			double distance = 10000;

			for (Player game : Bukkit.getOnlinePlayers().stream().filter(
					game -> !GameGeneral.getInstance().getGamerController().getGamer(game.getUniqueId()).isNotPlaying()
							&& !AdminMode.getInstance().isAdmin(game))
					.collect(Collectors.toList())) {
				
				double distOfPlayerToVictim = p.getLocation().distance(game.getPlayer().getLocation());
				if (distOfPlayerToVictim < distance && distOfPlayerToVictim > 25) {
					distance = distOfPlayerToVictim;
					target = game;
				}
			}

			if (target == null) {
				p.sendMessage("§c§l> §fNinguém foi encontrado, bussola apontando para o spawn!");
				p.setCompassTarget(Bukkit.getWorlds().get(0).getSpawnLocation());
			} else {
				p.setCompassTarget(target.getLocation());
				p.sendMessage("§a§l> §fBussola apontando para o §e" + target.getName() + "§f!");
			}

			compassMap.put(p.getUniqueId(), System.currentTimeMillis() + 3000l);
		} else if (item.getType() == Material.MUSHROOM_SOUP) {
			if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				if (((Damageable) p).getHealth() < ((Damageable) p).getMaxHealth() || p.getFoodLevel() < 20) {
					int restores = 7;

					event.setCancelled(true);

					if (((Damageable) p).getHealth() < ((Damageable) p).getMaxHealth())
						if (((Damageable) p).getHealth() + restores <= ((Damageable) p).getMaxHealth())
							p.setHealth(((Damageable) p).getHealth() + restores);
						else
							p.setHealth(((Damageable) p).getMaxHealth());
					else if (p.getFoodLevel() < 20)
						if (p.getFoodLevel() + restores <= 20) {
							p.setFoodLevel(p.getFoodLevel() + restores);
							p.setSaturation(5f);
						} else {
							p.setFoodLevel(20);
							p.setSaturation(5f);
						}

					item = new ItemStack(Material.BOWL);
					p.setItemInHand(item);
				}
			}
		}
	}

	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		if (event.getEntity() instanceof Player) {
			((Player) event.getEntity()).setSaturation(5f);
		}
	}

	@EventHandler
	public void onEntitySpawn(CreatureSpawnEvent event) {
		if (event.getEntityType() == EntityType.GHAST || event.getEntityType() == EntityType.PIG_ZOMBIE) {
			event.setCancelled(true);
			return;
		}

		if (event.getSpawnReason() != SpawnReason.NATURAL)
			return;

		if (CommonConst.RANDOM.nextInt(5) != 0) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onGameStart(GameStartEvent event) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			joined.add(player.getUniqueId());
		}
	}

	@EventHandler
	public void onPortal(PlayerPortalEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		if (event.getCause() == TeleportCause.NETHER_PORTAL || event.getCause() == TeleportCause.END_PORTAL)
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		if (AdminMode.getInstance().isAdmin(event.getPlayer())) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerSelected(PlayerSelectedKitEvent e) {
		if (e.getKit() == null)
			return;

		Player player = e.getPlayer();

		player.getInventory().addItem(new ItemStack(Material.COMPASS));

		Gamer gamer = GameGeneral.getInstance().getGamerController().getGamer(player);

		if (AdminMode.getInstance().isAdmin(player) || gamer.isGamemaker())
			player.sendMessage("§c§lAVISO §fVocê não está jogando!");

		for (Kit kit : gamer.getKitMap().values()) {
			for (Ability ability : kit.getAbilities()) {
				for (ItemStack items : ability.getItemList()) {
					player.getInventory().addItem(items);
				}
			}
		}
	}

	/*
	 * Ability
	 */

	@EventHandler
	public void onCraftItem(CraftItemEvent event) {
		if (!(event.getView().getPlayer() instanceof Player))
			return;

		Player p = (Player) event.getView().getPlayer();
		Gamer gamer = GameGeneral.getInstance().getGamerController().getGamer(p);

		for (Kit kit : gamer.getKitMap().values()) {
			for (ItemStack item : event.getInventory().getContents()) {
				if (item == null)
					continue;

				for (Ability ability : kit.getAbilities()) {
					if (ability.isAbilityItem(item)) {
						event.setCancelled(true);
						break;
					}
				}
			}
		}
	}

	@EventHandler
	public void onInventoryMove(InventoryClickEvent event) {
		if (!(event.getWhoClicked() instanceof Player))
			return;

		if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY
				|| event.getClickedInventory() == event.getInventory()) {
			ItemStack currentItem = event.getCursor();

			if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
				if (event.getClickedInventory().getItem(event.getSlot()) != null) {
					currentItem = event.getClickedInventory().getItem(event.getSlot());
				}
			}

			if (currentItem.getType() != Material.AIR) {
				Player player = (Player) event.getWhoClicked();
				Gamer gamer = GameGeneral.getInstance().getGamerController().getGamer(player);

				for (Kit kit : gamer.getKitMap().values()) {
					for (ItemStack item : event.getInventory().getContents()) {
						if (item == null)
							continue;

						for (Ability ability : kit.getAbilities()) {
							if (ability.isAbilityItem(item)) {
								event.setCancelled(true);
								player.updateInventory();
								break;
							}
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onDrop(PlayerDropItemEvent event) {
		ItemStack item = event.getItemDrop().getItemStack();
		Player player = (Player) event.getPlayer();
		Gamer gamer = GameGeneral.getInstance().getGamerController().getGamer(player);

		for (Kit kit : gamer.getKitMap().values()) {
			for (Ability ability : kit.getAbilities()) {
				if (ability.isAbilityItem(item)) {
					event.setCancelled(true);
					player.updateInventory();
					break;
				}
			}
		}
	}

	/*
	 * Timeout
	 */

	@EventHandler
	public void onUpdate(UpdateEvent event) {
		if (event.getType() != UpdateType.SECOND)
			return;

		Iterator<Entry<UUID, Timeout>> iterator = GameGeneral.getInstance().getTimeoutController().getStoreMap()
				.entrySet().iterator();

		if (iterator.hasNext()) {
			Entry<UUID, Timeout> entry = iterator.next();

			Timeout timeout = entry.getValue();

			if (timeout.getExpireTime() < System.currentTimeMillis()) {
				Gamer gamer = GameGeneral.getInstance().getGamerController().getGamer(entry.getKey());

				for (ItemStack itemStack : Stream
						.concat(Arrays.stream(timeout.getContents()), Arrays.stream(timeout.getArmorContents()))
						.toArray(ItemStack[]::new)) {
					if (itemStack == null || itemStack.getType() == Material.AIR)
						continue;
					
					timeout.getLocation().getWorld().dropItemNaturally(timeout.getLocation(), itemStack);
				}

				gamer.setTimeout(true);
				Bukkit.broadcastMessage(
						"§b" + gamer.getPlayerName() + " demorou demais para relogar e foi desclassificado!");

				iterator.remove();
			}
		}
	}

}
