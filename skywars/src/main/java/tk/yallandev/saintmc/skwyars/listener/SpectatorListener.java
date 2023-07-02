package tk.yallandev.saintmc.skwyars.listener;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.scheduler.BukkitRunnable;

import net.md_5.bungee.api.ChatColor;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.bukkit.api.vanish.AdminMode;
import tk.yallandev.saintmc.bukkit.event.admin.PlayerAdminModeEvent;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.skwyars.GameGeneral;
import tk.yallandev.saintmc.skwyars.GameMain;
import tk.yallandev.saintmc.skwyars.event.player.PlayerSpectateEvent;
import tk.yallandev.saintmc.skwyars.gamer.Gamer;
import tk.yallandev.saintmc.skwyars.menu.spectator.SpectatorInventory;
import tk.yallandev.saintmc.skwyars.scheduler.MinigameState;

public class SpectatorListener implements Listener {

	@EventHandler(priority = EventPriority.MONITOR)
	public void onSpectate(PlayerSpectateEvent event) {
		Player player = event.getPlayer();

		if (player == null)
			return;

		for (Player online : Bukkit.getOnlinePlayers())
			if (online.isOnline())
				if (online.getUniqueId() != player.getUniqueId())
					if (online.canSee(player))
						online.hidePlayer(player);

		player.sendMessage("§dVocê entrou no modo espectador!");
		player.setGameMode(GameMode.ADVENTURE);
		player.setAllowFlight(true);
		player.setFlying(true);

		new BukkitRunnable() {

			@Override
			public void run() {
				if (!player.isOnline())
					return;

				player.getInventory().setItem(0, new ItemBuilder().name("§aPlayers").type(Material.COMPASS).build());
				player.getInventory().setItem(8,
						new ItemBuilder().name("§aJogar novamente").type(Material.PAPER).build());
				player.updateInventory();
			}
		}.runTaskLater(GameMain.getPlugin(), 7l);
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		if (GameGeneral.getInstance().getMinigameState() == MinigameState.WINNING)
			return;

		if (!isSpectator(event.getPlayer()))
			return;

		event.setCancelled(true);

		Member player = CommonGeneral.getInstance().getMemberManager().getMember(event.getPlayer().getUniqueId());

		if (player == null) {
			event.setCancelled(true);
			return;
		}

		String tag = player.getTag().getPrefix();

		event.getRecipients().stream().filter(r -> isSpectator(r) || AdminMode.getInstance().isAdmin(r)).forEach(
				r -> r.sendMessage("§7[SPECTATOR] " + tag + (ChatColor.stripColor(tag).trim().length() > 0 ? " " : "")
						+ event.getPlayer().getName() + "§7: " + event.getMessage()));

		System.out.println("<SPECTATOR - " + player.getPlayerName() + "> " + event.getMessage());
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (isSpectator(event.getPlayer()))
			event.setCancelled(true);
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if (isSpectator(event.getPlayer()))
			event.setCancelled(true);
	}

	@EventHandler
	public void onEntityRegainHealth(EntityRegainHealthEvent event) {
		if (event.getEntity() instanceof Player)
			if (isSpectator((Player) event.getEntity()))
				event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerExpChange(PlayerExpChangeEvent event) {
		if (isSpectator(event.getPlayer()))
			event.setAmount(0);
	}

	@EventHandler
	public void onEntityTarget(EntityTargetEvent event) {
		if (event.getTarget() instanceof Player)
			if (isSpectator((Player) event.getTarget()))
				event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		if (isSpectator(event.getPlayer()))
			event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		if (isSpectator(event.getPlayer()))
			event.setCancelled(true);
	}

	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		if (event.getEntity() instanceof Player)
			if (isSpectator((Player) event.getEntity()))
				event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (isSpectator(event.getPlayer()) || AdminMode.getInstance().isAdmin(event.getPlayer())) {
			if (!Member.hasGroupPermission(event.getPlayer().getUniqueId(), Group.ADMIN))
				event.setCancelled(true);

			if (event.getItem() == null)
				return;

			if (event.getItem().getType() == Material.COMPASS)
				new SpectatorInventory(event.getPlayer(), 1);
			else if (event.getItem().getType() == Material.DIAMOND_SWORD)
				handleCombat(event.getPlayer());
			else if (event.getItem().getType() == Material.PAPER) {
				GameMain.getInstance().sendPlayAgain(event.getPlayer());
			}
		}
	}

	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
		if (isSpectator(event.getPlayer()))
			event.setCancelled(true);
	}

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player)
			if (isSpectator((Player) event.getEntity()))
				event.setCancelled(true);

		if (event.getDamager() instanceof Player)
			if (isSpectator((Player) event.getDamager()))
				event.setCancelled(true);
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player)
			if (isSpectator((Player) event.getEntity()))
				event.setCancelled(true);
	}

	@EventHandler
	protected void onBlockCanBuild(BlockCanBuildEvent e) {
		if (!e.isBuildable()) {
			Location blockL = e.getBlock().getLocation();
			boolean allowed = false;

			for (Player target : Bukkit.getOnlinePlayers()) {
				Gamer targetGamer = GameGeneral.getInstance().getGamerController().getGamer(target);

				if (!targetGamer.isNotPlaying())
					continue;

				if (target.getWorld().equals(e.getBlock().getWorld())) {
					Location playerL = target.getLocation();
					if (playerL.getX() > blockL.getBlockX() - 1 && playerL.getX() < blockL.getBlockX() + 1) {
						if (playerL.getZ() > blockL.getBlockZ() - 1 && playerL.getZ() < blockL.getBlockZ() + 1) {
							if (playerL.getY() > blockL.getBlockY() - 1 && playerL.getY() < blockL.getBlockY() + 2) {
								allowed = true;
								break;
							}
						}
					}

				}
			}

			e.setBuildable(allowed);
		}
	}

	@EventHandler
	public void onPlayerAdminMode(PlayerAdminModeEvent event) {
		if (GameGeneral.getInstance().getMinigameState().isPregame()) {
			Gamer gamer = GameGeneral.getInstance().getGamerController().getGamer(event.getPlayer().getUniqueId());

			if (event.getAdminMode() == PlayerAdminModeEvent.AdminMode.ADMIN) {
				gamer.setGamemaker(true);
			} else {
				gamer.setGamemaker(false);
				gamer.setSpectator(false);
			}
		} else {
			Gamer gamer = GameGeneral.getInstance().getGamerController().getGamer(event.getPlayer().getUniqueId());

			if (event.getAdminMode() == PlayerAdminModeEvent.AdminMode.ADMIN) {
				gamer.setGamemaker(true);
			} else {
				gamer.setGamemaker(false);
				gamer.setSpectator(false);
			}
		}
	}

	private void handleCombat(Player player) {

	}

	private boolean isSpectator(Player player) {
		Gamer gamer = GameGeneral.getInstance().getGamerController().getGamer(player.getUniqueId());
		return gamer.isSpectator();
	}

}
