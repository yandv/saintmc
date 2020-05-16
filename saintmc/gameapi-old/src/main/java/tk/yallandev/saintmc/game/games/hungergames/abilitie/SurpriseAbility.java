package br.com.battlebits.game.games.hungergames.abilitie;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import br.com.battlebits.game.ability.AbilityRarity;
import br.com.battlebits.game.constructor.Ability;
import br.com.battlebits.game.constructor.CustomOption;
import br.com.battlebits.game.constructor.Kit;
import br.com.battlebits.game.interfaces.Disableable;
import br.com.battlebits.game.manager.AbilityManager;
import br.com.battlebits.game.manager.KitManager;

public class SurpriseAbility extends Ability implements Disableable {
	
	private Set<UUID> surpriseList = new HashSet<>();

	public SurpriseAbility() {
		super(new ItemStack(Material.CAKE), AbilityRarity.RARE);
	}
	
	@Override
	public void giveItems(Player player) {
		if (!myPlayers.contains(player.getUniqueId()))
			return;
		
		if (surpriseList.contains(player.getUniqueId()))
			return;
		
		surpriseList.add(player.getUniqueId());
		Kit kit = KitManager.getAllKits().get(new Random().nextInt(KitManager.getAllKits().size()));
		
		do {
			kit = KitManager.getAllKits().get(new Random().nextInt(KitManager.getAllKits().size()));
		} while (kit == null || kit.getName().equalsIgnoreCase("Surprise"));
		
		for (Ability ability : kit.getAbilities())
			AbilityManager.registerPlayerAbility(player, ability.getName());
		
		for (ItemStack item : items) {
			player.getInventory().addItem(item.clone());
		}
		
		for (ItemStack item : getItems(kit)) {
			player.getInventory().addItem(item.clone());
		}
		
		player.sendMessage("Surprise §3§l> §a" + kit.getName());
		super.giveItems(player);
	}

	@Override
	public int getPowerPoints(HashMap<String, CustomOption> map) {
		return 20;
	}

}
