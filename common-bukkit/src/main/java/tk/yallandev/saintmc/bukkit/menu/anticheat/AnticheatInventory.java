package tk.yallandev.saintmc.bukkit.menu.anticheat;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.bukkit.api.menu.MenuInventory;
import tk.yallandev.saintmc.bukkit.api.menu.MenuUpdateHandler;
import tk.yallandev.saintmc.bukkit.api.menu.click.ClickType;
import tk.yallandev.saintmc.bukkit.api.menu.click.MenuClickHandler;

public class AnticheatInventory {
	
	public AnticheatInventory(Player player, Player tester) {
		
		if (tester == null)
			return;
		
		MenuInventory menu = new MenuInventory("§7Check - " + tester.getName(), 4);
		
		menu.setItem(11, new ItemBuilder().name("§aAutosoup Test").type(Material.MUSHROOM_SOUP).build(), new MenuClickHandler() {
			
			@Override
			public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
				new AutosoupInventory(player, tester);
			}
		});
		
		menu.setItem(13, new ItemBuilder().name("§aAutoclick Test").type(Material.STONE_SWORD).build(), new MenuClickHandler() {
			
			@Override
			public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
				new AutoclickInventory(player, tester);
			}
		});
		
		menu.setItem(15, new ItemBuilder().name("§aForcefield Test").type(Material.STONE_SWORD).build(), new MenuClickHandler() {
			
			@Override
			public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
				
			}
		});
		
		menu.setUpdateHandler(new MenuUpdateHandler() {
			
			@Override
			public void onUpdate(Player player, MenuInventory menu) {
				if (!tester.isOnline()) {
					player.sendMessage("§cO jogador " + tester.getName() + " está offline!");
					player.closeInventory();
				}
			}
		});
		
		menu.open(player);
	}

}
