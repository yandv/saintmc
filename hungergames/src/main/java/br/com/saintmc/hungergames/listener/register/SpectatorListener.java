package br.com.saintmc.hungergames.listener.register;

import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.GameMain;
import br.com.saintmc.hungergames.constructor.Gamer;
import br.com.saintmc.hungergames.event.player.PlayerSpectateEvent;
import br.com.saintmc.hungergames.game.GameState;
import br.com.saintmc.hungergames.listener.GameListener;
import br.com.saintmc.hungergames.menu.spectator.SpectatorInventory;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.api.item.ActionItemStack;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.bukkit.api.item.ActionItemStack.ActionType;
import tk.yallandev.saintmc.bukkit.api.vanish.AdminMode;
import tk.yallandev.saintmc.bukkit.api.vanish.VanishAPI;
import tk.yallandev.saintmc.bukkit.bukkit.BukkitMember;
import tk.yallandev.saintmc.bukkit.event.teleport.PlayerTeleportCommandEvent;
import tk.yallandev.saintmc.bukkit.event.teleport.PlayerTeleportCommandEvent.TeleportResult;
import tk.yallandev.saintmc.bukkit.event.vanish.PlayerHideToPlayerEvent;
import tk.yallandev.saintmc.bukkit.event.vanish.PlayerShowToPlayerEvent;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.permission.Group;

public class SpectatorListener extends GameListener {

	private ActionItemStack itemStack = new ActionItemStack(
			new ItemBuilder().name("§aPróxima partida").type(Material.COMPASS).build(), new ActionItemStack.Interact() {

				@Override
				public boolean onInteract(Player player, Entity entity, Block block, ItemStack item,
						ActionType action) {
					BukkitMain.getInstance().sendPlayer(player, "Hungergames");
					return false;
				}
			});

	@EventHandler(priority = EventPriority.MONITOR)
	public void onSpectate(PlayerSpectateEvent event) {
		Player player = event.getPlayer();

		if (player == null)
			return;

		for (Player online : Bukkit.getOnlinePlayers())
			if (online != null)
				if (online.isOnline())
					if (online.getUniqueId() != player.getUniqueId())
						if (online.canSee(player))
							online.hidePlayer(player);

		new BukkitRunnable() {

			@Override
			public void run() {
				if (!player.isOnline())
					return;

				VanishAPI.getInstance().setPlayerVanishToGroup(player, Group.BUILDER);
				player.setGameMode(GameMode.ADVENTURE);
				player.setAllowFlight(true);
				player.setFlying(true);
				player.sendMessage("§dVocê entrou no modo espectador!");
				player.getInventory().setItem(0, new ItemBuilder().name("§aPlayers").type(Material.COMPASS).build());
				player.getInventory().setItem(1, itemStack.getItemStack());
				player.updateInventory();
			}
		}.runTaskLater(GameMain.getPlugin(), 20l);
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		if (GameGeneral.getInstance().getGameState() == GameState.WINNING)
			return;

		if (!isSpectator(event.getPlayer()))
			return;

		event.setCancelled(true);

		BukkitMember player = (BukkitMember) CommonGeneral.getInstance().getMemberManager()
				.getMember(event.getPlayer().getUniqueId());

		if (player == null) {
			event.setCancelled(true);
			return;
		}

		String tag = player.getTag().getPrefix();

		for (Player r : event.getRecipients().stream().filter(r -> isSpectator(r) || AdminMode.getInstance().isAdmin(r))
				.collect(Collectors.toList())) {

			r.sendMessage("§7[SPECTATOR] " + tag + (ChatColor.stripColor(tag).trim().length() > 0 ? " " : "")
					+ event.getPlayer().getName() + "§7: " + event.getMessage());
		}

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
			if (!Member.hasGroupPermission(event.getPlayer().getUniqueId(), Group.MODPLUS))
				event.setCancelled(true);

			if (event.getItem() == null)
				return;

			if (event.getItem().getType() == Material.COMPASS)
				new SpectatorInventory(event.getPlayer(), 1);
			else if (event.getItem().getType() == Material.DIAMOND_SWORD)
				handleCombat(event.getPlayer());
		}
	}

	private void handleCombat(Player player) {

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
	public void onVisible(PlayerShowToPlayerEvent event) {
		if (!isSpectator(event.getPlayer()))
			return;

		Gamer toGamer = GameGeneral.getInstance().getGamerController().getGamer(event.getToPlayer());

		if (!toGamer.isNotPlaying()) {
			if (!toGamer.isSpectatorsEnabled())
				event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerShowToPlayer(PlayerShowToPlayerEvent event) {
		Gamer toGamer = GameGeneral.getInstance().getGamerController().getGamer(event.getToPlayer());

		if (isSpectator(event.getPlayer()) || AdminMode.getInstance().isAdmin(event.getPlayer())) {
			if (toGamer == null || !toGamer.isSpectatorsEnabled()) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerHideToPlayer(PlayerHideToPlayerEvent event) {
		Gamer toGamer = GameGeneral.getInstance().getGamerController().getGamer(event.getToPlayer());

		if (isSpectator(event.getPlayer()) || AdminMode.getInstance().isAdmin(event.getPlayer()))
			if (toGamer.isSpectatorsEnabled()) {
				event.setCancelled(true);
			}
	}

	@EventHandler
	public void onPlayerTeleportCommand(PlayerTeleportCommandEvent event) {
		if (event.getResult() != TeleportResult.NO_PERMISSION)
			return;

		if (GameGeneral.getInstance().getGamerController().getGamer(event.getPlayer()).isSpectator())
			event.setResult(TeleportResult.ONLY_PLAYER_TELEPORT);
	}

	private boolean isSpectator(Player player) {
		Gamer gamer = GameGeneral.getInstance().getGamerController().getGamer(player.getUniqueId());
		return gamer.isSpectator();
	}

}
