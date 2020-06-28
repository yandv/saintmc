package tk.yallandev.saintmc.kitpvp.kit;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import tk.yallandev.saintmc.bukkit.api.cooldown.CooldownController;
import tk.yallandev.saintmc.bukkit.api.cooldown.types.Cooldown;
import tk.yallandev.saintmc.common.utils.DateUtils;
import tk.yallandev.saintmc.common.utils.string.NameUtils;
import tk.yallandev.saintmc.kitpvp.GameMain;

@Getter
public abstract class Kit implements Listener {

	private String kitName;
	private String kitDescription;
	private Material kitType;

	private int price;

	private List<ItemStack> itemList;

	private boolean registred;

	public Kit(String kitName, String kitDescription, Material kitType, int price, List<ItemStack> itemList) {
		this.kitName = kitName;
		this.kitDescription = kitDescription;
		this.kitType = kitType;
		this.price = price;
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
		if (item == null)
			return false;

//		for (ItemStack kitItem : itemList) {
//			if (kitItem.getType() == item.getType()) {
//				if (kitItem.hasItemMeta() && item.hasItemMeta()) {
//					if (kitItem.getItemMeta().hasDisplayName() && item.getItemMeta().hasDisplayName()) {
//						if (item.getItemMeta().getDisplayName().equals(kitItem.getItemMeta().getDisplayName()))
//							return true;
//					} else if (!kitItem.getItemMeta().hasDisplayName() && !item.getItemMeta().hasDisplayName())
//						return true;
//				} else if (!kitItem.hasItemMeta() && !item.hasItemMeta())
//					return true;
//			}
//
//			return false;
//		}

		return itemList.contains(item);
	}

	public boolean isCooldown(Player player) {
		if (CooldownController.getInstance().hasCooldown(player.getUniqueId(),
				"Kit " + NameUtils.formatString(getName()))) {

			Cooldown cooldown = CooldownController.getInstance().getCooldown(player.getUniqueId(),
					"Kit " + NameUtils.formatString(getName()));

			if (cooldown == null)
				return false;

			String message = "§cAguarde " + DateUtils.formatDifference((long) cooldown.getRemaining())
					+ " para usar o Kit " + NameUtils.formatString(getName()) + " novamente!";

			player.sendMessage(message);
			return true;
		}

		return false;
	}

	public void addCooldown(Player player, long time) {
		CooldownController.getInstance().addCooldown(player.getUniqueId(), "Kit " + NameUtils.formatString(getName()),
				time);
	}

	public void addCooldown(UUID uniqueId, long time) {
		CooldownController.getInstance().addCooldown(uniqueId, "Kit " + NameUtils.formatString(getName()), time);
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
