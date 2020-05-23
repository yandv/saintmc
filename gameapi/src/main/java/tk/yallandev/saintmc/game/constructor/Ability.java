package tk.yallandev.saintmc.game.constructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import tk.yallandev.saintmc.game.GameMain;
import tk.yallandev.saintmc.game.ability.AbilityRarity;
import tk.yallandev.saintmc.game.games.hungergames.util.ItemUtils;
import tk.yallandev.saintmc.game.interfaces.Disableable;
import tk.yallandev.saintmc.game.interfaces.Optional;
import tk.yallandev.saintmc.game.stage.GameStage;

public abstract class Ability implements Listener {

	private String name;
	public transient Set<UUID> myPlayers = new HashSet<UUID>();
	public transient Set<ItemStack> items = new HashSet<>();
	public transient HashMap<String, CustomOption> options = new HashMap<>();
	private transient ItemStack icon;
	private transient AbilityRarity rarity;

	public Ability(ItemStack abilityIcon, AbilityRarity rarity) {
		this.icon = abilityIcon;
		this.rarity = rarity;
	}

	public ItemStack getIcon() {
		return icon;
	}

	public AbilityRarity getRarity() {
		return rarity;
	}

	public boolean hasAbility(Player p) {
		return hasAbility(p.getUniqueId());
	}

	public HashMap<String, CustomOption> getOptions() {
		return options;
	}

	public List<String> getItemOptions() {
		List<String> list = new ArrayList<>();
		for (CustomOption option : getOptions().values()) {
			if (option.isItem())
				list.add(option.getName());
		}
		return list;
	}

	public CustomOption getOption(String optionName) {
		return options.get(optionName);
	}

	public CustomOption getOption(Player player, String optionName) {
		for (Kit playerKit : GameMain.getPlugin().getKitManager().getPlayerKit(player))
			if (playerKit.hasAbility(getName()))
				return getOption(optionName, playerKit);
		
		return getOption(optionName, (Kit)null);
	}

	public CustomOption getOption(String optionName, HashMap<String, CustomOption> map) {
		if (map != null)
			if (map.containsKey(optionName)) {
				return getOption(optionName).copy(map.get(optionName));
			}
		return getOption(optionName);
	}

	public CustomOption getOption(String optionName, Kit kit) {
		CustomOption option = getOption(optionName);
		if (kit != null && kit instanceof Optional) {
			Optional optional = (Optional) kit;
			CustomOption opt = optional.getOption(getName(), optionName);
			if (opt.getValue() != -1)
				return option.copy(opt);
		}
		return option;
	}

	public abstract int getPowerPoints(HashMap<String, CustomOption> map);

	public boolean hasAbility(UUID uuid) {
		return myPlayers.contains(uuid);
	}

	public String getName() {
		return name;
	}

	public void giveItems(Player player) {
		for (ItemStack item : items) {
			player.getInventory().addItem(item.clone());
		}
		
		for (Kit kit : GameMain.getPlugin().getKitManager().getPlayerKit(player)) {
			for (ItemStack item : getItems(kit)) {
				player.getInventory().addItem(item.clone());
			}
		}
	}

	public void registerPlayer(Player player) {
		if (!GameStage.isPregame(GameMain.getPlugin().getGameStage()) && this instanceof Disableable && myPlayers.size() == 0) {
			Bukkit.getPluginManager().registerEvents(this, GameMain.getPlugin());
		}
		
		myPlayers.add(player.getUniqueId());
	}

	public void unregisterPlayer(Player player) {
		myPlayers.remove(player.getUniqueId());
		
		if (!GameStage.isPregame(GameMain.getPlugin().getGameStage()) && this instanceof Disableable && myPlayers.size() == 0) {
			HandlerList.unregisterAll(this);
		}
	}

	public Set<ItemStack> getItems(Kit kit) {
		Set<ItemStack> items = new HashSet<>(this.items);
		
		for (String option : options.keySet()) {
			CustomOption opt = getOption(option, kit);
			
			if (!opt.isItem())
				continue;
			
			items.add(opt.getItemStack());
		}
		
		return items;
	}

	public boolean isAbilityItem(Kit kit, ItemStack item) {
		for (ItemStack it : items) {
			if (ItemUtils.isEquals(item, it))
				return true;
		}
		for (String option : options.keySet()) {
			CustomOption opt = getOption(option, kit);
			if (!opt.isItem())
				continue;
			ItemStack it = opt.getItemStack();
			if (ItemUtils.isEquals(item, it))
				return true;
		}
		return false;
	}

}
