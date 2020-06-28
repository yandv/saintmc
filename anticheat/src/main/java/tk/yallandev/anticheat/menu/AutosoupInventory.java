package tk.yallandev.anticheat.menu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import tk.yallandev.anticheat.test.Test;
import tk.yallandev.anticheat.test.Test.CheckLevel;
import tk.yallandev.anticheat.test.TestType;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;
import tk.yallandev.saintmc.bukkit.api.menu.MenuInventory;
import tk.yallandev.saintmc.common.utils.supertype.FutureCallback;

public class AutosoupInventory {

	public AutosoupInventory(Player player, Player tester) {
		MenuInventory menu = new MenuInventory("§7Check - " + tester.getName(), 5);

		ItemStack redGlass = new ItemBuilder().type(Material.STAINED_GLASS_PANE).durability(14).name("§cEm progresso")
				.build();

		for (int x = 0; x < 7; x++) {
			menu.setItem(10 + x, redGlass);
			menu.setItem(19 + x, redGlass);
		}
		
		menu.open(player);
		
		TestType.AUTOSOUP.check(tester, new FutureCallback<Test.CheckLevel>() {
			
			@Override
			public void result(CheckLevel result, Throwable error) {
					
				new ResultInventory(player, tester, TestType.AUTOSOUP, result);
				
			}
			
		});
	}

}
