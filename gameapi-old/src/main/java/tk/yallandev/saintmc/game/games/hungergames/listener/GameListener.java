package tk.yallandev.saintmc.game.games.hungergames.listener;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

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
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffect;

import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.api.vanish.AdminMode;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.game.GameMain;
import tk.yallandev.saintmc.game.constructor.Ability;
import tk.yallandev.saintmc.game.constructor.Gamer;
import tk.yallandev.saintmc.game.constructor.Kit;
import tk.yallandev.saintmc.game.event.game.GameStartEvent;
import tk.yallandev.saintmc.game.event.player.PlayerSelectedKitEvent;
import tk.yallandev.saintmc.game.games.hungergames.HungerGamesMode;
import tk.yallandev.saintmc.game.stage.GameStage;

public class GameListener extends tk.yallandev.saintmc.game.listener.GameListener {
	
	public static final boolean SPECTATOR = true;

	private Random random = new Random();
	private Set<UUID> joined = new HashSet<UUID>();

	public GameListener(GameMain main) {
		super(main);
		getGameMain().getServer().getPluginManager().registerEvents(new BlockListener(getGameMain()), getGameMain());
		ItemStack soup = new ItemStack(Material.MUSHROOM_SOUP);
		newShapelessRecipe(soup, Arrays.asList(new MaterialData(Material.CACTUS), new MaterialData(Material.BOWL)));
		newShapelessRecipe(soup,
				Arrays.asList(new MaterialData(Material.NETHER_STALK), new MaterialData(Material.BOWL)));
		newShapelessRecipe(soup,
				Arrays.asList(new MaterialData(Material.INK_SACK, (byte) 3), new MaterialData(Material.BOWL)));
		newShapelessRecipe(soup, Arrays.asList(new MaterialData(Material.SUGAR), new MaterialData(Material.BOWL)));
		newShapelessRecipe(soup, Arrays.asList(new MaterialData(Material.PUMPKIN_SEEDS),
				new MaterialData(Material.PUMPKIN_SEEDS), new MaterialData(Material.BOWL)));
		newShapelessRecipe(soup, Arrays.asList(new MaterialData(Material.CARROT_ITEM),
				new MaterialData(Material.POTATO_ITEM), new MaterialData(Material.BOWL)));
	}

	public void newShapelessRecipe(ItemStack result, List<MaterialData> materials) {
		ShapelessRecipe recipe = new ShapelessRecipe(result);
		
		for (MaterialData mat : materials) {
			recipe.addIngredient(mat);
		}
		
		Bukkit.addRecipe(recipe);
	}

	@EventHandler
	public void onLogin(PlayerLoginEvent event) {
		Player p = event.getPlayer();
		Member player = CommonGeneral.getInstance().getMemberManager().getMember(p.getUniqueId());
		
		if (GameStage.isPregame(getGameMain().getGameStage()))
			return;
		
		if (player.hasGroupPermission(Group.TRIAL))
			return;
		
		if (player.hasGroupPermission(Group.SAINT) && SPECTATOR)
			return;
		
		if (player.hasGroupPermission(Group.LIGHT) && !joined.contains(p.getUniqueId()) && getGameMain().getTimer() < 300)
			return;
		
		if (DeathListener.containsDeathMessage(p.getUniqueId()))
			event.disallow(Result.KICK_OTHER, DeathListener.getDeathMessage(p.getUniqueId()));
//		else
//			event.disallow(Result.KICK_OTHER, T.t(BukkitMain.getInstance(),player.getLanguage(), "game-already-started"));
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onJoin(PlayerJoinEvent event) {
		Player p = event.getPlayer();
		
		if (DeathListener.relogProcess.contains(p.getUniqueId()))
			return;
		
		Gamer gamer = getGameMain().getGamerManager().getGamer(p.getUniqueId());
		
		if (getGameMain().getTimer() <= 300 && !joined.contains(p.getUniqueId())) {
			p.getInventory().clear();
			
			for (PotionEffect effect : p.getActivePotionEffects())
				p.removePotionEffect(effect.getType());
			
			p.getInventory().addItem(new ItemStack(Material.COMPASS, 1));
			joined.add(p.getUniqueId());
			p.sendMessage("§a§l> §fVocê entrou na partida!");
//			gamer.setNoKit(true);
		} else {
			event.setJoinMessage(null);
			
			if (Member.hasGroupPermission(p.getUniqueId(), Group.TRIAL))
				AdminMode.getInstance().setAdmin(p, Member.getMember(p.getUniqueId()));
			else
				gamer.setSpectator(true);
		}
	}

	@EventHandler
	public void onReceipe(CraftItemEvent event) {
		if (!(event.getView().getPlayer() instanceof Player))
			return;
		
		Player p = (Player) event.getView().getPlayer();
		Kit kit = Gamer.getGamer(p).getKit();
		
		if (kit == null)
			return;
		
		for (ItemStack item : event.getInventory().getContents()) {
			if (item == null)
				continue;
			
			for (Ability ability : kit.getAbilities()) {
				if (ability.isAbilityItem(kit, item)) {
					event.setCancelled(true);
					break;
				}
			}
		}
	}

	@EventHandler
	public void onInventoryMove(InventoryClickEvent event) {
		if (!(event.getWhoClicked() instanceof Player))
			return;
		
		if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY || event.getClickedInventory() == event.getInventory()) {
			ItemStack currentItem = event.getCursor();
			
			if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
				if (event.getClickedInventory().getItem(event.getSlot()) != null) {
					currentItem = event.getClickedInventory().getItem(event.getSlot());
				}
			}
			
			if (currentItem.getType() != Material.AIR) {
				Player p = (Player) event.getWhoClicked();
				Kit kit = Gamer.getGamer(p).getKit();
				
				if (kit == null)
					return;
				
				for (Ability ability : kit.getAbilities()) {
					if (ability.isAbilityItem(kit, currentItem)) {
						event.setCancelled(true);
						p.updateInventory();
						break;
					}
				}
			}
		}

	}

	@EventHandler
	public void onDrop(PlayerDropItemEvent event) {
		Player p = event.getPlayer();
		Kit kit = Gamer.getGamer(p).getKit();
		ItemStack item = event.getItemDrop().getItemStack();
		
		if (item == null)
			return;
		
		if (kit == null)
			return;
		
		for (Ability ability : kit.getAbilities()) {
			if (ability.isAbilityItem(kit, item)) {
				event.setCancelled(true);
				p.updateInventory();
				break;
			}
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

	@EventHandler
	public void onCompass(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		
		if (getStage() == GameStage.PREGAME) 
			return;
		
		ItemStack item = event.getItem();
		
		if (item == null || item.getType() == Material.AIR)
			return;
		
		if (item.getType() == Material.COMPASS) {
			Player target = null;
			double distance = 10000;
			
			for (Player game : Bukkit.getOnlinePlayers()) {
				if (AdminMode.getInstance().isAdmin(game))
					continue;
				
				Gamer gamer = getGameMain().getGamerManager().getGamer(game.getUniqueId());
				
				if (gamer.isSpectator() || gamer.isGamemaker())
					continue;
				
				double distOfPlayerToVictim = p.getLocation().distance(game.getPlayer().getLocation());
				if (distOfPlayerToVictim < distance && distOfPlayerToVictim > 25) {
					distance = distOfPlayerToVictim;
					target = game;
				}
			}
			
			if (target == null) {
				p.sendMessage("§c§l> §fNingu§m foi encontrado, bussola apontando para o spawn!");
				p.setCompassTarget(Bukkit.getWorlds().get(0).getSpawnLocation());
			} else {
				p.setCompassTarget(target.getLocation());
				p.sendMessage("§a§l> §fBussola apontando para o §e" + target.getName() + "§f!");
			}
		}
	}

	@EventHandler
	public void onSoup(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		ItemStack item = event.getItem();
		
		if (item == null || item.getType() == Material.AIR)
			return;
		
		if (item.getType() == Material.MUSHROOM_SOUP) {
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
							p.setSaturation(3);
						} else {
							p.setFoodLevel(20);
							p.setSaturation(3);
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
		
		if (random.nextInt(5) != 0) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onStart(GameStartEvent event) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			joined.add(player.getUniqueId());
		}
	}

	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;
		
		if (HungerGamesMode.FINISHED)
			event.setCancelled(true);
	}

	@EventHandler
	public void onRespawn(PlayerRespawnEvent event) {
		Player p = event.getPlayer();
		event.setRespawnLocation(p.getLocation());
		Member player = CommonGeneral.getInstance().getMemberManager().getMember(p.getUniqueId());
		Gamer gamer = getGameMain().getGamerManager().getGamer(p.getUniqueId());
		
		if (player != null && player.hasGroupPermission(Group.TRIAL)) {
			gamer.setGamemaker(true);
			if (!AdminMode.getInstance().isAdmin(p))
				AdminMode.getInstance().setAdmin(p, player);
		} else {
			gamer.setSpectator(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		if (AdminMode.getInstance().isAdmin(event.getPlayer())) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerSelected(PlayerSelectedKitEvent e) {
		Player p = e.getPlayer();
		
		if (e.getKit() == null)
			return;
		
		if (AdminMode.getInstance().isAdmin(p))
			p.sendMessage("§cVocê está no modo admin!");
		
		p.getInventory().addItem(new ItemStack(Material.COMPASS));
		
		Kit playerKit = e.getKit();
		
		for (Ability ability : playerKit.getAbilities()) {
			ability.giveItems(p);
		}
	}
	
//	public Language getLanguage(Player p) {
//		return BattlePlayer.getLanguage(p.getUniqueId());
//	}

}
