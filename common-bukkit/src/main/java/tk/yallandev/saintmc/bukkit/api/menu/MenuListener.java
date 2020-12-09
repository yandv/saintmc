package tk.yallandev.saintmc.bukkit.api.menu;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.scheduler.BukkitRunnable;

import tk.yallandev.saintmc.bukkit.BukkitMain;
import tk.yallandev.saintmc.bukkit.api.menu.click.ClickType;
import tk.yallandev.saintmc.bukkit.event.player.MenuOpenEvent;
import tk.yallandev.saintmc.bukkit.event.update.UpdateEvent;

public class MenuListener implements Listener {

	private Map<Player, InventoryHolder> playerMap;

	public MenuListener() {
		playerMap = new HashMap<>();
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onInventoryClickListener(InventoryClickEvent event) {
		if (event.getInventory() == null)
			return;

		Inventory inv = event.getInventory();

		if (inv.getType() != InventoryType.CHEST || inv.getHolder() == null || !(inv.getHolder() instanceof MenuHolder))
			return;

		event.setCancelled(true);

		if (event.getClickedInventory() != inv || !(event.getWhoClicked() instanceof Player) || event.getSlot() < 0)
			return;

		MenuHolder holder = (MenuHolder) inv.getHolder();
		MenuInventory menu = holder.getMenu();

		if (menu.hasItem(event.getSlot())) {
			Player p = (Player) event.getWhoClicked();
			MenuItem item = menu.getItem(event.getSlot());
			item.getHandler().onClick(p, inv,
					((event.getAction() == InventoryAction.PICKUP_HALF) ? ClickType.RIGHT : ClickType.LEFT),
					event.getCurrentItem(), event.getSlot());
		} else {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onMenuOpen(MenuOpenEvent event) {
		if (event.getInventory() == null)
			return;

		Inventory inventory = event.getInventory();

		if (inventory.getHolder() instanceof MenuHolder) {
			MenuInventory menu = ((MenuHolder) inventory.getHolder()).getMenu();

			if (menu.getUpdateHandler() != null)
				playerMap.put((Player) event.getPlayer(), inventory.getHolder());
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onInventoryClose(InventoryCloseEvent event) {
		if (event.getInventory() == null)
			return;

		Inventory inventory = event.getInventory();

		if (inventory.getHolder() instanceof MenuHolder) {
			MenuHolder menuHolder = (MenuHolder) inventory.getHolder();

			Player player = (Player) event.getPlayer();

			if (playerMap.containsKey(player))
				playerMap.remove(player);

			if (menuHolder.getMenu().isReopenInventory())
				new BukkitRunnable() {

					@Override
					public void run() {
						if (player.isOnline())
							menuHolder.getMenu().open((Player) event.getPlayer());
					}
				}.runTaskLater(BukkitMain.getInstance(), 10l);
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		playerMap.remove(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onUpdate(UpdateEvent event) {
		if (event.getType() != UpdateEvent.UpdateType.SECOND)
			return;

		for (Entry<Player, InventoryHolder> entry : playerMap.entrySet()) {
			MenuInventory menu = ((MenuHolder) entry.getValue()).getMenu();

			if (menu.getUpdateHandler() != null)
				menu.getUpdateHandler().onUpdate(entry.getKey(), menu);
		}
	}
}
