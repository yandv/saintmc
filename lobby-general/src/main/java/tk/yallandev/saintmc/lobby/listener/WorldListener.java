package tk.yallandev.saintmc.lobby.listener;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.bukkit.BukkitMember;
import tk.yallandev.saintmc.bukkit.event.PlayerMoveUpdateEvent;
import tk.yallandev.saintmc.common.permission.Group;

public class WorldListener implements Listener {

	@EventHandler
	public void onBlockExplode(BlockExplodeEvent e) {
		e.blockList().clear();
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent e) {
		e.setCancelled(true);
	}

	@EventHandler
	public void onCreatureSpawn(CreatureSpawnEvent e) {
		if (e.getSpawnReason() == SpawnReason.CUSTOM)
			return;

		if (e.getEntity() instanceof Player)
			return;

		e.setCancelled(true);
	}

	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent e) {
		e.setCancelled(true);
	}

	@EventHandler
	public void onWeatherChange(WeatherChangeEvent e) {
		e.setCancelled(true);
	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		BukkitMember player = (BukkitMember) CommonGeneral.getInstance().getMemberManager()
				.getMember(e.getPlayer().getUniqueId());

		if (player.isBuildEnabled())
			if (player.hasGroupPermission(Group.DEVELOPER)) {
				e.setCancelled(false);
				return;
			}

		e.setCancelled(true);
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		BukkitMember player = (BukkitMember) CommonGeneral.getInstance().getMemberManager()
				.getMember(e.getPlayer().getUniqueId());

		if (player.isBuildEnabled())
			if (player.hasGroupPermission(Group.DEVELOPER)) {
				e.setCancelled(false);
				return;
			}

		e.setCancelled(true);
	}

	@EventHandler
	public void onPlayerMoveUpdate(PlayerMoveUpdateEvent event) {
		Player player = event.getPlayer();
		Material type = event.getTo().getBlock().getRelative(BlockFace.DOWN).getType();

		if (type == Material.SLIME_BLOCK) {
			player.setVelocity(player.getLocation().getDirection().multiply(1.8F).setY(0.7F));
			player.setFallDistance(-1.0F);
		}
	}

	@EventHandler
	public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
		BukkitMember player = (BukkitMember) CommonGeneral.getInstance().getMemberManager()
				.getMember(event.getPlayer().getUniqueId());

		if (player.isBuildEnabled())
			if (player.hasGroupPermission(Group.DEVELOPER)) {
				event.setCancelled(false);
				return;
			}

		event.setCancelled(true);
	}

	@EventHandler
	public void onItemSpawn(ItemSpawnEvent event) {
		event.getEntity().remove();
	}

}
