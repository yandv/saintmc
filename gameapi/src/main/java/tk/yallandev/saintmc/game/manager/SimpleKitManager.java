package tk.yallandev.saintmc.game.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public class SimpleKitManager {
	
	private HashMap<String, SimpleKit> simpleKits;
	
	public SimpleKitManager() {
		this.simpleKits = new HashMap<>();
	}
	
	public boolean loadKit(String name, SimpleKit simpleKit) {
		if (simpleKits.containsKey(name.toLowerCase()))
			return false;
		
		simpleKits.put(name, simpleKit);
		return true;
	}
	
	public SimpleKit getKit(String name) {
		return simpleKits.getOrDefault(name.toLowerCase(), null);
	}
	
	public boolean loadKit(String name, Player player) {
		player.getActivePotionEffects();
		return loadKit(name, new SimpleKit(player.getInventory().getContents(), player.getInventory().getArmorContents()));
	}
	
	public class SimpleKit {
		
		private ItemStack[] contents;
		private ItemStack[] armorContents;
		private Collection<PotionEffect> potionEffects;
		
		public SimpleKit(ItemStack[] contents, ItemStack[] armorContents, Collection<PotionEffect> potionEffects) {
			this.contents = contents;
			this.armorContents = armorContents;
			this.potionEffects = potionEffects;
		}
		
		public SimpleKit(ItemStack[] contents, ItemStack[] armorContents) {
			this(contents, armorContents, new ArrayList<>());
		}
		
		public void setPlayer(Player player) {
			player.getInventory().setContents(contents);
			player.getInventory().setArmorContents(armorContents);
			
			player.getActivePotionEffects().clear();
			for (PotionEffect potions : potionEffects) {
				player.addPotionEffect(potions);
			}
			
			player.updateInventory();
		}
		
		public void broadcast() {
			for (Player player : Bukkit.getOnlinePlayers())
				setPlayer(player);
		}
		
		public ItemStack[] getContents() {
			return contents;
		}
		
		public ItemStack[] getArmorContents() {
			return armorContents;
		}
		
		public Collection<PotionEffect> getPotionEffects() {
			return potionEffects;
		}
		
	}

}
