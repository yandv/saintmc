package tk.yallandev.saintmc.bukkit.anticheat.modules.register;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import tk.yallandev.saintmc.bukkit.anticheat.modules.Module;

public class AutosoupModule extends Module {
	
	private Map<UUID, Long> time;
	
	public AutosoupModule() {
		setAlertBungee(true);
		time = new HashMap<>();
	}

	@EventHandler
	private void onClick(InventoryClickEvent event) {	
		if (!event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY) || event.getCurrentItem() == null || !event.getCurrentItem().getType().equals(Material.MUSHROOM_SOUP))
			return;
		
		time.put(event.getWhoClicked().getUniqueId(), System.currentTimeMillis());
	}
	
	@EventHandler
	private void onPlayerInteract(PlayerInteractEvent event) {
		if (event.getItem() == null || event.getPlayer().getHealth() == 20 || (event.getItem() != null && !event.getItem().getType().equals(Material.MUSHROOM_SOUP)))
			return;
		
		Player player  = event.getPlayer();
		UUID uniqueId = player.getUniqueId();
		
		if (time.containsKey(uniqueId)) {
			Long spentTime = System.currentTimeMillis() - time.get(uniqueId);
			
			if (spentTime <= 2) {
				alert(player);
			}
			
			time.remove(uniqueId);
		}
	}

}
