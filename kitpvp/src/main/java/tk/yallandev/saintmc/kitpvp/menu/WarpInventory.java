package tk.yallandev.saintmc.kitpvp.menu;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import tk.yallandev.saintmc.bukkit.api.menu.ClickType;
import tk.yallandev.saintmc.bukkit.api.menu.MenuClickHandler;
import tk.yallandev.saintmc.bukkit.api.menu.MenuInventory;
import tk.yallandev.saintmc.bukkit.api.menu.MenuItem;
import tk.yallandev.saintmc.kitpvp.GameMain;
import tk.yallandev.saintmc.kitpvp.warp.Warp;

public class WarpInventory {

	public WarpInventory(Player player) {
		MenuInventory menuInventory = new MenuInventory("§eWarp Selector", 3);
		int i = 0;

		for (Warp warp : GameMain.getInstance().getWarpManager().getWarps()) {
			if (!warp.getWarpSettings().isWarpEnabled())
				continue;

			if (warp.getItem() == null)
				continue;

			menuInventory.setItem(11 + i, new MenuItem(warp.getItem(), new MenuClickHandler() {

				@Override
				public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
					GameMain.getInstance().getWarpManager()
							.teleport(GameMain.getInstance().getGamerManager().getGamer(player.getUniqueId()), warp, 5);
				}
			}));
			i++;
		}

		menuInventory.open(player);
	}

}
