package tk.yallandev.saintmc.bukkit.listener;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.inventory.ItemStack;

public class EnchantListener implements Listener {
	
	@EventHandler
	public void onEnchantItem(EnchantItemEvent event) {
//		if (event.get)
	}

	@EventHandler
	public void onInventoryOpen(InventoryOpenEvent event) {
		if (event.getInventory() instanceof EnchantingInventory)
			((EnchantingInventory) event.getInventory()).setSecondary(new ItemStack(Material.INK_SACK, 3, (short) 4));
	}

	@EventHandler
	public void onClick(InventoryClickEvent event) {
		if (event.getInventory() instanceof EnchantingInventory)
			if (event.getRawSlot() == 1)
				event.setCancelled(true);
	}

}
