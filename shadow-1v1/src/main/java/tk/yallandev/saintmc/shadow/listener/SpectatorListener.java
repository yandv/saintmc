package tk.yallandev.saintmc.shadow.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import tk.yallandev.saintmc.shadow.challenge.Challenge;
import tk.yallandev.saintmc.shadow.event.GladiatorSpectatorEvent;
import tk.yallandev.saintmc.shadow.event.GladiatorSpectatorEvent.Action;

import java.util.HashMap;
import java.util.Map;

public class SpectatorListener implements Listener {

	private Map<Player, Challenge> spectatorMap;

	public SpectatorListener() {
		spectatorMap = new HashMap<>();
	}

	@EventHandler
	public void onGladiatorSpectator(GladiatorSpectatorEvent event) {
		Player player = event.getPlayer();

		if (event.getAction() == Action.LEAVE) {
			spectatorMap.remove(player);
		} else
			spectatorMap.put(player, event.getChallenge());
	}

	@EventHandler
	public void onGladiatorSpectator(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player) {
			Player player = (Player) event.getDamager();

			if (spectatorMap.containsKey(player))
				event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		if (spectatorMap.containsKey(event.getPlayer()))
			spectatorMap.get(event.getPlayer()).removeSpectator(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = (Player) event.getPlayer();

		if (spectatorMap.containsKey(player))
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = (Player) event.getPlayer();

		if (spectatorMap.containsKey(player))
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = (Player) event.getPlayer();

		if (spectatorMap.containsKey(player))
			event.setCancelled(true);
	}

}
