package tk.yallandev.saintmc.bukkit.api.menu;

import org.bukkit.entity.Player;

public interface MenuUpdateHandler {
	void onUpdate(Player player, MenuInventory menu);
}
