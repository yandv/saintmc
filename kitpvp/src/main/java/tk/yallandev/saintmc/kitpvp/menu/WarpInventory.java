package tk.yallandev.saintmc.kitpvp.menu;

import org.bukkit.entity.Player;

import tk.yallandev.saintmc.bukkit.api.menu.MenuInventory;
import tk.yallandev.saintmc.bukkit.api.menu.MenuItem;
import tk.yallandev.saintmc.kitpvp.GameMain;
import tk.yallandev.saintmc.kitpvp.warp.Warp;

public class WarpInventory {

    public WarpInventory(Player player) {
        MenuInventory menuInventory = new MenuInventory("§7§nSelecionar warp", 3);
        int i = 0;

        for (Warp warp : GameMain.getInstance().getWarpManager().getWarps()) {
            if (!warp.getWarpSettings().isWarpEnabled()) {
                continue;
            }

            if (warp.getItem() == null) {
                continue;
            }

            menuInventory.setItem(11 + i, new MenuItem(warp.getItem(), (p, inv, type, stack, slot) -> {
                GameMain.getInstance().getWarpManager().teleport(p, warp, 5);
                return false;
            }));
            i++;
        }

        menuInventory.open(player);
    }
}
