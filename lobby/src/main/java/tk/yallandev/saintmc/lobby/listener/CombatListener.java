package tk.yallandev.saintmc.lobby.listener;

import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.api.actionbar.ActionBarAPI;
import tk.yallandev.saintmc.bukkit.event.PlayerMoveUpdateEvent;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.account.status.StatusType;
import tk.yallandev.saintmc.common.account.status.types.normal.NormalStatus;
import tk.yallandev.saintmc.lobby.LobbyMain;
import tk.yallandev.saintmc.lobby.event.PlayerItemReceiveEvent;
import tk.yallandev.saintmc.lobby.gamer.Gamer;

@SuppressWarnings("deprecation")
public class CombatListener implements Listener {

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;

		Player player = (Player) event.getEntity();
		Gamer gamer = LobbyMain.getInstance().getPlayerManager().getGamer(player);

		if (event.getCause() == DamageCause.FALL) {
			if (!gamer.isCombat())
				if (player.getLocation().getX() > -5 && player.getLocation().getY() < 118
						&& player.getLocation().getZ() < -40) {
					player.getInventory().clear();
					player.getInventory().setItem(0, new ItemStack(Material.STONE_SWORD));

					for (int x = 0; x < 15; x++)
						player.getInventory().addItem(new ItemStack(Material.MUSHROOM_SOUP));

					player.updateInventory();
					ActionBarAPI.send(player, "§cVocê entrou na área de combate!");

					new BukkitRunnable() {

						@Override
						public void run() {
							CommonGeneral.getInstance().getStatusManager().loadStatus(player.getUniqueId(),
									StatusType.LOBBY);
						}
					}.runTaskAsynchronously(LobbyMain.getInstance());

					LobbyMain.getInstance().getPlayerManager().getGamer(player).setCombat(true);
				}
		}

		if (event instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent damageEvent = (EntityDamageByEntityEvent) event;

			if (damageEvent.getDamager() instanceof Player) {
				Player damager = (Player) damageEvent.getDamager();

				if (LobbyMain.getInstance().getPlayerManager().isCombat(damager) && gamer.isCombat()) {
					event.setCancelled(false);

					if (damager.getItemInHand().getType() != null
							&& damager.getItemInHand().getType().name().contains("SWORD")) {
						damager.getItemInHand().setDurability((short) 0);
						event.setDamage(4.0D);
						damager.updateInventory();
					}

					return;
				}
			}
		}

		if (event.getCause() == DamageCause.VOID) {
			if (gamer.isCombat())
				gamer.setCombat(false);

			Bukkit.getPluginManager().callEvent(new PlayerItemReceiveEvent(player));
			event.getEntity()
					.teleport(Member.isLogged(player.getUniqueId())
							? BukkitMain.getInstance().getLocationFromConfig("spawn")
							: BukkitMain.getInstance().getLocationFromConfig("login"));
		}

		event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveUpdateEvent event) {
		if (LobbyMain.getInstance().getPlayerManager().isCombat(event.getPlayer()))
			return;

	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		Gamer gamer = LobbyMain.getInstance().getPlayerManager().getGamer(player);

		boolean killer = false;

		if (gamer.isCombat()) {
			gamer.setCombat(false);

			if (player.getKiller() instanceof Player) {
				killer = true;
				player.getKiller().getInventory().addItem(new ItemStack(Material.RED_MUSHROOM, 16));
				player.getKiller().getInventory().addItem(new ItemStack(Material.BROWN_MUSHROOM, 16));
				player.getKiller().getInventory().addItem(new ItemStack(Material.BOWL, 16));

				new BukkitRunnable() {

					@Override
					public void run() {
						CommonGeneral.getInstance().getStatusManager()
								.loadStatus(player.getKiller().getUniqueId(), StatusType.LOBBY, NormalStatus.class)
								.addKill();
					}
				}.runTaskAsynchronously(LobbyMain.getInstance());
			}
		}

		player.setHealth(player.getMaxHealth());
		player.setFoodLevel(20);
		player.setSaturation(5);
		player.setFireTicks(0);
		player.setFallDistance(0);
		player.setLevel(0);
		player.setExp(0);
		player.setVelocity(new Vector(0, 0, 0));
		player.teleport(BukkitMain.getInstance().getLocationFromConfig("combat"));

		if (killer)
			new BukkitRunnable() {

				@Override
				public void run() {
					CommonGeneral.getInstance().getStatusManager()
							.loadStatus(player.getKiller().getUniqueId(), StatusType.LOBBY, NormalStatus.class)
							.addDeath();
					/**
					 * Wait the next tick to
					 */

					new BukkitRunnable() {

						@Override
						public void run() {
							player.sendMessage("§cVocê morreu!");
							player.sendMessage("§eVocê está com §b" + CommonGeneral.getInstance().getStatusManager()
									.loadStatus(player.getUniqueId(), StatusType.LOBBY, NormalStatus.class).getKills()
									+ " kills§e no lobby!");
							Bukkit.getPluginManager().callEvent(new PlayerItemReceiveEvent(player));
						}
					}.runTask(LobbyMain.getInstance());
				}
			}.runTaskAsynchronously(LobbyMain.getInstance());

		event.getDrops().clear();
		event.setDroppedExp(0);

		event.setDeathMessage(null);
	}

	@EventHandler
	public void onUpdate(UpdateEvent event) {
		for (Player player : LobbyMain.getInstance().getPlayerManager().getGamers().stream()
				.filter(gamer -> gamer.isCombat()).map(Gamer::getPlayer).collect(Collectors.toList())) {
			ActionBarAPI.send(player,
					"§eVocê tem §b"
							+ CommonGeneral.getInstance().getStatusManager()
									.loadStatus(player.getUniqueId(), StatusType.LOBBY, NormalStatus.class).getKills()
							+ " kills§e na arena!");
		}
	}

}
