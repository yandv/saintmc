package tk.yallandev.saintmc.bukkit.menu.anticheat;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import tk.yallandev.saintmc.bukkit.anticheat.check.Check;
import tk.yallandev.saintmc.bukkit.anticheat.check.Check.CheckLevel;
import tk.yallandev.saintmc.bukkit.anticheat.check.CheckType;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.bukkit.api.menu.MenuInventory;
import tk.yallandev.saintmc.common.utils.supertype.FutureCallback;

public class AutoclickInventory {
	
	public AutoclickInventory(Player player, Player tester) {
		MenuInventory menu = new MenuInventory("§7Check - " + tester.getName(), 5);

		ItemStack redGlass = new ItemBuilder().type(Material.STAINED_GLASS_PANE).durability(14).name("§cEm progresso")
				.build();
		
		for (int x = 0; x < 7; x++) {
			menu.setItem(10 + x, redGlass);
			menu.setItem(19 + x, redGlass);
		}
		
		menu.open(player);
		
		CheckType.AUTOCLICK.check(tester, new FutureCallback<Check.CheckLevel>() {
			
			@Override
			public void result(CheckLevel result, Throwable error) {
					
				new ResultInventory(player, tester, CheckType.AUTOCLICK, result);
				
			}
			
		});
	}

}
