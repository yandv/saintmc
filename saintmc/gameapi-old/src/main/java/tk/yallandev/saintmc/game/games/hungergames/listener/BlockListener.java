package tk.yallandev.saintmc.game.games.hungergames.listener;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.permission.Group;
import tk.yallandev.saintmc.game.GameMain;
import tk.yallandev.saintmc.game.games.hungergames.HungerGamesMode;
import tk.yallandev.saintmc.game.games.hungergames.structure.FeastStructure;
import tk.yallandev.saintmc.game.manager.ServerManager;
import tk.yallandev.saintmc.game.stage.GameStage;

public class BlockListener extends tk.yallandev.saintmc.game.listener.GameListener {

	public BlockListener(GameMain main) {
		super(main);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onBreak(BlockBreakEvent event) {
		if (isFeastBlock(event.getBlock()) || (!ServerManager.getInstance().isBuild() && !Member.hasGroupPermission(event.getPlayer().getUniqueId(), Group.MOD)))
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlace(BlockPlaceEvent event) {
		if (isFeastBlock(event.getBlock()) || (!ServerManager.getInstance().isPlace() && !Member.hasGroupPermission(event.getPlayer().getUniqueId(), Group.MOD)))
			event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityDamage(EntityDamageEvent event) {
		if (!ServerManager.getInstance().isDamage()) {
			event.setCancelled(true);
			return;
		}
		
		if (event.getCause() == DamageCause.ENTITY_ATTACK)
			if (!ServerManager.getInstance().isPvp())
				event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerDrop(PlayerDropItemEvent e) {
		if (!ServerManager.getInstance().isDrop())
			e.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerPickup(PlayerPickupItemEvent e) {
		if (!ServerManager.getInstance().isPickup())
			e.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerBucket(PlayerBucketEmptyEvent e) {
		if ((!ServerManager.getInstance().isPlace() && !Member.hasGroupPermission(e.getPlayer().getUniqueId(), Group.MOD)))
			e.setCancelled(true);
	}

	private boolean isFeastBlock(Block b) {
		return FeastStructure.isFeastBlock(b) && getGameMain().getGameStage() == GameStage.GAMETIME && getGameMain().getTimer() < HungerGamesMode.FEAST_SPAWN;
	}

}
