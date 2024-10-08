package tk.yallandev.saintmc.kitpvp.listener;

import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;

import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.bukkit.bukkit.BukkitMember;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.kitpvp.GameMain;
import tk.yallandev.saintmc.kitpvp.gamer.Gamer;
import tk.yallandev.saintmc.kitpvp.kit.Kit;

public class WorldListener implements Listener {

	@EventHandler
	public void onUpdate(UpdateEvent event) {
		if (event.getType() != UpdateEvent.UpdateType.SECOND)
			return;

		for (World world : Bukkit.getServer().getWorlds())
			for (Entity e : world.getEntitiesByClass(Item.class))
				if (e.getTicksLived() >= 200) {
					Location location = e.getLocation().clone();

					world.playEffect(location, Effect.NOTE, 1);
					e.remove();
				}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onDrop(PlayerDropItemEvent event) {
		Item drop = event.getItemDrop();
		ItemStack item = drop.getItemStack();

		if (item.toString().contains("SWORD") || item.toString().contains("AXE")) {
			event.setCancelled(true);
			return;
		}

		Player player = event.getPlayer();
		Gamer gamer = GameMain.getInstance().getGamerManager().getGamer(player.getUniqueId());

		if (gamer == null)
			return;

		if (gamer.hasKit()) {
			Kit kit = gamer.getKit();

			if (kit.isAbilityItem(item))
				event.setCancelled(true);
		} else if (!gamer.getWarp().getWarpSettings().isSpawnEnabled()) {
			event.getItemDrop().remove();
		}
	}

	@EventHandler
	public void onItemPickUp(PlayerPickupItemEvent event) {
		ItemStack item = event.getItem().getItemStack();

		if (GameMain.getInstance().getGamerManager().getGamer(event.getPlayer().getUniqueId()).isSpawnProtection()) {
			event.setCancelled(true);
			return;
		}

		if (item.getItemMeta().hasDisplayName()) {
			event.setCancelled(true);
			return;
		}

		if (item.getType().toString().contains("SWORD") || item.getType().toString().contains("AXE")) {
			event.setCancelled(true);
			return;
		}

		if (item.getType().toString().contains("HELMET") || item.getType().toString().contains("CHESTPLATE")
				|| item.getType().toString().contains("LEGGING") || item.getType().toString().contains("BOOTS")) {
			event.setCancelled(true);
			return;
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerInteractEvent event) {
		if (event.getAction() == Action.PHYSICAL)
			if (event.getClickedBlock().getType() == Material.STONE_PLATE)
				event.setCancelled(false);
			else
				event.setCancelled(true);
	}

	@EventHandler
	public void onExplode(BlockBurnEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onExplode(EntityExplodeEvent event) {
		Iterator<Block> iterator = event.blockList().iterator();

		while (iterator.hasNext()) {
			Block next = iterator.next();

			if (next.getType() != Material.AIR)
				iterator.remove();
		}

		event.setCancelled(true);
	}

	@EventHandler
	public void onWeatherChange(WeatherChangeEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onCreatureSpawnChange(CreatureSpawnEvent event) {
		if (event.getSpawnReason() != SpawnReason.CUSTOM)
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onBlockBreak(BlockBreakEvent e) {
		BukkitMember player = (BukkitMember) CommonGeneral.getInstance().getMemberManager()
				.getMember(e.getPlayer().getUniqueId());

		if (player.isBuildEnabled())
			if (player.hasGroupPermission(Group.ADMIN)) {
				e.setCancelled(false);
				return;
			}

		e.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onBlockPlace(BlockPlaceEvent e) {
		BukkitMember player = (BukkitMember) CommonGeneral.getInstance().getMemberManager()
				.getMember(e.getPlayer().getUniqueId());

		if (player.isBuildEnabled())
			if (player.hasGroupPermission(Group.ADMIN)) {
				e.setCancelled(false);
				return;
			}

		e.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onBlockPlace(PlayerBucketEmptyEvent e) {
		BukkitMember player = (BukkitMember) CommonGeneral.getInstance().getMemberManager()
				.getMember(e.getPlayer().getUniqueId());

		if (player.isBuildEnabled())
			if (player.hasGroupPermission(Group.ADMIN)) {
				e.setCancelled(false);
				return;
			}

		e.setCancelled(true);
	}

}
