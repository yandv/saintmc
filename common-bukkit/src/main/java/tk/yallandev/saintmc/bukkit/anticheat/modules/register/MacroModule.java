package tk.yallandev.saintmc.bukkit.anticheat.modules.register;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;

import tk.yallandev.saintmc.bukkit.anticheat.modules.Clicks;
import tk.yallandev.saintmc.bukkit.anticheat.modules.Module;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent.UpdateType;

public class MacroModule extends Module {

	private Map<Player, Clicks> clicksPerSecond;

	public MacroModule() {
		clicksPerSecond = new HashMap<>();
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onInventoryClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		
		if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.ADVENTURE)
			return;
		
		if (event.isShiftClick())
			if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY)
				return;
		
		Clicks click = clicksPerSecond.computeIfAbsent(player, v -> new Clicks());

		if (click.getExpireTime() < System.currentTimeMillis()) {
			if (click.getClicks() >= 25) {
				alert(player, click.getClicks());
			}

			clicksPerSecond.remove(player);
			return;
		}

		click.addClick();
	}

	@EventHandler
	public void onUpdate(UpdateEvent event) {
		if (event.getType() == UpdateType.SECOND) {
			Iterator<Entry<Player, Clicks>> iterator = clicksPerSecond.entrySet().iterator();

			while (iterator.hasNext()) {
				Entry<Player, Clicks> entry = iterator.next();

				if (entry.getValue().getExpireTime() < System.currentTimeMillis()) {
					if (entry.getValue().getClicks() >= 16) {
						alert(entry.getKey(), entry.getValue().getClicks());
					}

					iterator.remove();
				}
			}
		}
	}

}
