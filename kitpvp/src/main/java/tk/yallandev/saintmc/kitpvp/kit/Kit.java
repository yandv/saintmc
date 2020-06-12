package tk.yallandev.saintmc.kitpvp.kit;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import tk.yallandev.saintmc.kitpvp.GameMain;

@Getter
public abstract class Kit implements Listener {

	private String kitName;
	private String kitDescription;
	private Material kitType;

	private List<ItemStack> itemList;

	private boolean registred;

	public Kit(String kitName, String kitDescription, Material kitType, List<ItemStack> itemList) {
		this.kitName = kitName;
		this.kitDescription = kitDescription;
		this.kitType = kitType;
		this.itemList = itemList;
	}

	public void register() {
		if (registred)
			return;

		Bukkit.getPluginManager().registerEvents(this, GameMain.getInstance());
		registred = true;
	}

	public void unregister() {
		if (!registred)
			return;

		HandlerList.unregisterAll(this);
		registred = false;
	}

	public String getName() {
		return kitName;
	}

	public boolean hasAbility(Player player) {
		return GameMain.getInstance().getGamerManager().getGamer(player.getUniqueId()).hasKit(getKitName());
	}

	public boolean isAbilityItem(ItemStack item) {
		for (ItemStack kitItem : itemList) {
			if (kitItem.getType() == item.getType()) {
				if (kitItem.hasItemMeta() && item.hasItemMeta()) {
					if (kitItem.getItemMeta().hasDisplayName() && item.getItemMeta().hasDisplayName()) {
						if (item.getItemMeta().getDisplayName().equals(kitItem.getItemMeta().getDisplayName()))
							return true;
					} else if (!kitItem.getItemMeta().hasDisplayName() && !item.getItemMeta().hasDisplayName())
						return true;
				} else if (!kitItem.hasItemMeta() && !item.hasItemMeta())
					return true;
			}

			return false;
		}

		return false;
	}

	/**
	 * O inventário já vai estar pronto, só adicionar o kit no inv
	 */

	public void applyKit(Player player) {
		int x = 0;
		
		for (ItemStack item : itemList) {
			player.getInventory().setItem(x + 1, item);
			x++;
		}
	}
}
