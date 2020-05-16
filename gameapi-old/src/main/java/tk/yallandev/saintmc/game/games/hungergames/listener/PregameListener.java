package tk.yallandev.saintmc.game.games.hungergames.listener;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.api.title.TitleAPI;
import tk.yallandev.saintmc.bukkit.api.vanish.AdminMode;
import tk.yallandev.saintmc.bukkit.event.admin.PlayerAdminModeEvent;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.game.GameMain;
import tk.yallandev.saintmc.game.constructor.Gamer;
import tk.yallandev.saintmc.game.event.game.GameStageChangeEvent;
import tk.yallandev.saintmc.game.stage.GameStage;

public class PregameListener extends GameListener {

	public PregameListener(GameMain main) {
		super(main);
	}

	private boolean isPregame() {
		return GameStage.isPregame(getGameMain().getGameStage());
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		if (!isPregame())
			return;
		
		Player p = event.getPlayer();
		Gamer gamer = Gamer.getGamer(p);
		
		event.setJoinMessage(null);
		
		if (gamer == null) {
			GameMain.getPlugin().sendPlayerToLobby(p);
			return;
		}
		
		Member player = CommonGeneral.getInstance().getMemberManager().getMember(p.getUniqueId());
		p.setHealth(20.0);
		p.setGameMode(GameMode.ADVENTURE);
		p.setAllowFlight(false);
		p.setFlying(false);
		p.setFoodLevel(20);
		p.setExp(0);
		
		for (int x = 0; x < 5; x++)
			p.sendMessage("§");
		
//		p.sendMessage(T.t(BukkitMain.getInstance(), BattlePlayer.getLanguage(p.getUniqueId()), "welcome-message"));
		p.sendMessage("");
		TitleAPI.setTitle(p, "§%title-message%§", "§%subtitle-message%§");
		
		if (player.hasGroupPermission(Group.TRIAL)) {
			AdminMode.getInstance().setAdmin(p, player);
		}
		
//		if (player.hasPermission("tag.winner"))
//			p.sendMessage(T.t(BukkitMain.getInstance(),BattlePlayer.getLanguage(p.getUniqueId()), "you-win-last-game"));
	}
	
	@EventHandler
	public void onPlayerAdminModeEvent(PlayerAdminModeEvent event) {
		Gamer gamer = getGameMain().getGamerManager().getGamer(event.getPlayer().getUniqueId());
		
		if (event.getAdminMode() == PlayerAdminModeEvent.AdminMode.ADMIN) {
			gamer.setGamemaker(true);
		} else {
			gamer.setGamemaker(false);
			gamer.setSpectator(false);
		}
	}

	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		if (isPregame())
			event.setCancelled(true);
	}

	@EventHandler
	public void onPlace(BlockPlaceEvent event) {
		if (isPregame())
			event.setCancelled(true);
	}

	@EventHandler
	public void onRegen(EntityRegainHealthEvent event) {
		if (isPregame())
			event.setCancelled(true);
	}

	@EventHandler
	public void onExpChange(PlayerExpChangeEvent event) {
		if (isPregame())
			event.setAmount(0);
	}

	@EventHandler
	public void onMobTarget(EntityTargetEvent event) {
		if (isPregame())
			event.setCancelled(true);
	}

	@EventHandler
	public void onDropItem(PlayerDropItemEvent event) {
		if (isPregame())
			event.setCancelled(true);
	}

	@EventHandler
	public void onPickupItem(PlayerPickupItemEvent event) {
		if (isPregame())
			event.setCancelled(true);
	}

	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		if (isPregame())
			event.setCancelled(true);
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if (isPregame())
			event.setCancelled(true);
	}

	@EventHandler
	public void onInteractEntity(PlayerInteractEntityEvent event) {
		if (isPregame())
			event.setCancelled(true);
	}

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (isPregame())
			event.setCancelled(true);
	}

	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if (isPregame())
			event.setCancelled(true);
	}

	@EventHandler
	public void onShear(PlayerShearEntityEvent event) {
		if (isPregame())
			event.setCancelled(true);
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		Player p = event.getEntity();
		p.setHealth(20.0);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityExplode(EntityExplodeEvent event) {
		if (isPregame())
			event.setCancelled(true);
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
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		if (event.getSpawnReason() == SpawnReason.CUSTOM)
			return;
		
		event.setCancelled(true);
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		event.setQuitMessage(null);
//		BattlePlayer.getPlayer(event.getPlayer().getUniqueId()).removePermission("tag.winner");
	}

	@EventHandler
	public void onGameStageChange(GameStageChangeEvent event) {
		if (GameStage.isPregame(event.getLastStage())) {
			if (!GameStage.isPregame(event.getNewStage())) {
				HandlerList.unregisterAll(this);
			}
		}
	}
}
