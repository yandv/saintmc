package tk.yallandev.saintmc.skwyars.listener.game;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import tk.yallandev.saintmc.CommonConst;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.api.title.Title;
import tk.yallandev.saintmc.bukkit.api.title.types.SimpleTitle;
import tk.yallandev.saintmc.bukkit.api.vanish.AdminMode;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.account.status.types.game.GameStatus;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.skwyars.GameGeneral;
import tk.yallandev.saintmc.skwyars.GameMain;
import tk.yallandev.saintmc.skwyars.event.game.GameStateChangeEvent;
import tk.yallandev.saintmc.skwyars.event.game.GameTimeEvent;
import tk.yallandev.saintmc.skwyars.game.cage.Cage;
import tk.yallandev.saintmc.skwyars.game.cage.types.DefaultCage;
import tk.yallandev.saintmc.skwyars.game.team.Team;
import tk.yallandev.saintmc.skwyars.gamer.Gamer;
import tk.yallandev.saintmc.skwyars.scheduler.MinigameState;
import tk.yallandev.saintmc.skwyars.utils.ItemUtils;

public class GameListener implements Listener {

	private List<Block> cageBlockList;

	public GameListener() {
		int cageIndex = 1;
		cageBlockList = new ArrayList<>();

		World world = null;

		for (Gamer gamer : GameGeneral.getInstance().getGamerController().getStoreMap().values()) {
			if (gamer.isPlaying()) {
				Location location = GameMain.getInstance().getLocationFromConfig("cage-" + cageIndex);

				Location realLocation = new Location(location.getWorld(), (int) location.getX(),
						GameMain.getInstance().getY(), (int) location.getZ(), location.getYaw(), location.getPitch());
				realLocation.add(0.5, 0, 0.5);

				Cage cage = new DefaultCage();

				gamer.getPlayer().setGameMode(GameMode.SURVIVAL);
				gamer.getPlayer().teleport(realLocation.subtract(0, 1, 0));
				cageBlockList.addAll(cage.generateCage(realLocation));
				world = realLocation.getWorld();

				new BukkitRunnable() {

					@Override
					public void run() {
						CommonGeneral.getInstance().getStatusManager()
								.loadStatus(gamer.getUniqueId(),
										GameMain.getInstance().getSkywarsType().getStatusType(), GameStatus.class)
								.addMatch();
					}
				}.runTaskAsynchronously(GameMain.getInstance());

			} else {
				gamer.setSpectator(true);
				gamer.getPlayer().teleport(GameMain.getInstance().getLocationFromConfig("spectator"));
			}

			cageIndex++;
		}

		World w = world;

		GameGeneral.getInstance().getLocationController().getChestList().forEach(chest -> {
			Chest c = chest.getChest(w);

			if (c != null) {
				c.getBlockInventory().clear();
				chest.fill(w);
			}
		});

		BukkitMain.getInstance().setTagControl(false);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerLogin(PlayerLoginEvent event) {
		Member member = CommonGeneral.getInstance().getMemberManager().getMember(event.getPlayer().getUniqueId());

		if (!member.hasGroupPermission(Group.TRIAL))
			event.disallow(Result.KICK_OTHER, "§cServidor disponível somente para staff!");
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerQuit(PlayerQuitEvent event) {
		Gamer gamer = GameGeneral.getInstance().getGamerController().getGamer(event.getPlayer());

		if (gamer.isPlaying())
			if (GameGeneral.getInstance().getMinigameState() != MinigameState.WINNING)
				Bukkit.broadcastMessage("§e" + event.getPlayer().getName() + " saiu da partida");

		GameGeneral.getInstance().getTeamController()
				.handleLeave(GameGeneral.getInstance().getGamerController().getGamer(event.getPlayer().getUniqueId()));
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityDamage(EntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;

		if (!(event.getDamager() instanceof Player))
			return;

		Player player = (Player) event.getEntity();
		Gamer gamer = GameGeneral.getInstance().getGamerController().getGamer(player.getUniqueId());

		if (!gamer.isPlaying()) {
			event.setCancelled(true);
			return;
		}

		Team team = gamer.getTeam();

		if (team == null) {
			event.setCancelled(true);
			return;
		}

		Gamer damagerGamer = GameGeneral.getInstance().getGamerController().getGamer(event.getDamager().getUniqueId());
		Team damagerTeam = damagerGamer.getTeam();

		if (!damagerGamer.isPlaying()) {
			event.setCancelled(true);
			return;
		}

		if (damagerTeam == null) {
			event.setCancelled(true);
			return;
		}

		if (team.isInTeam(damagerGamer)) {
			event.setCancelled(true);
			return;
		}

		event.setCancelled(false);
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;

		if (event.getCause() == DamageCause.VOID) {
			Player player = (Player) event.getEntity();
			Gamer gamer = GameGeneral.getInstance().getGamerController().getGamer(player.getUniqueId());

			if (gamer.isSpectator()) {
				player.teleport(GameMain.getInstance().getLocationFromConfig("spectator"));
			} else
				player.damage(Integer.MAX_VALUE);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		Gamer gamer = GameGeneral.getInstance().getGamerController().getGamer(player.getUniqueId());

		List<ItemStack> list = new ArrayList<>(event.getDrops());
		event.getDrops().clear();

		ItemUtils.dropItems(list, player.getKiller() == null ? player.getLocation() : player.getKiller().getLocation());

		player.setHealth(20);
		player.setFoodLevel(20);

		Title.send(player, "§c§lMORREU", "§fVocê morreu!", SimpleTitle.class);

		if (gamer.isPlaying()) {
			gamer.setSpectator(true);
		}

		event.setDeathMessage(null);
	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		if (GameGeneral.getInstance().getGamerController().getGamer(event.getPlayer()).isNotPlaying())
			event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		if (GameGeneral.getInstance().getGamerController().getGamer(event.getPlayer()).isNotPlaying())
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event) {
		event.setJoinMessage(null);

		Player player = event.getPlayer();
		Gamer gamer = GameGeneral.getInstance().getGamerController().getGamer(player);

		if (Member.hasGroupPermission(player.getUniqueId(), Group.TRIAL))
			AdminMode.getInstance().setAdmin(player,
					CommonGeneral.getInstance().getMemberManager().getMember(player.getUniqueId()));
		else
			gamer.setSpectator(true);
	}

	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		if (GameGeneral.getInstance().getMinigameState().isPregame())
			event.setCancelled(true);
		else
			event.setCancelled(CommonConst.RANDOM.nextInt(10) + 1 >= 5);
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (GameGeneral.getInstance().getMinigameState().isPregame())
			event.setCancelled(true);
		else
			event.setCancelled(false);
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if (GameGeneral.getInstance().getMinigameState().isPregame())
			event.setCancelled(true);
		else
			event.setCancelled(false);
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		boolean cancelled = false;

		if (Math.abs(event.getTo().getX()) >= GameMain.getInstance().getMaxDistance()
				|| Math.abs(event.getTo().getZ()) >= GameMain.getInstance().getMaxDistance())
			cancelled = true;

		if (!cancelled)
			if (Math.abs(event.getTo().getY()) >= GameMain.getInstance().getMaxY()) {
				cancelled = true;
			}

		if (cancelled) {
			event.getPlayer().damage(1.0d);
			event.getPlayer().sendMessage("§cVocê passou da borda do mundo!");
			event.getPlayer().setFireTicks(20);
			event.setCancelled(cancelled);
		}
	}

	@EventHandler
	public void onGameTime(GameTimeEvent event) {
		if (GameGeneral.getInstance().getMinigameState().isPregame()) {
			int time = event.getTime();
			float percentage = ((time * 100) / 10) / (float) 100;
			float realPercentage = percentage > 1f ? 1f : percentage;

			for (Player player : Bukkit.getOnlinePlayers()) {
				player.setLevel(time);
				player.setExp(realPercentage);

				if (event.getTime() <= 5) {
					Title.send(player, "§6§lJOGO", "§7O jogo iniciará em §e" + event.getTime() + "§7!",
							SimpleTitle.class);
					player.playSound(player.getLocation(), Sound.NOTE_BASS, 1f, 1f);
				}
			}
		}
	}

	@EventHandler
	public void onGameStateChange(GameStateChangeEvent event) {
		if (event.getFromState().isPregame())
			if (event.getToState() == MinigameState.GAMETIME) {
				Iterator<Block> iterator = cageBlockList.iterator();

				while (iterator.hasNext()) {
					Block block = iterator.next();

					block.setType(Material.AIR);
					iterator.remove();
				}

				for (Player player : Bukkit.getOnlinePlayers()) {
					player.addPotionEffect(PotionEffectType.DAMAGE_RESISTANCE.createEffect(20 * 5, 254));

					player.getInventory().clear();
					player.getInventory().setArmorContents(new ItemStack[4]);
					player.setHealth(20D);
					player.setFoodLevel(20);
					player.playSound(player.getLocation(), Sound.ENDERDRAGON_HIT, 0.2f, 0.2f);

					Gamer gamer = GameGeneral.getInstance().getGamerController().getGamer(player);

					if (gamer.hasKit())
						gamer.getKit().apply(player);

					new SimpleTitle("", "").reset(player);
				}
			}
	}

}
