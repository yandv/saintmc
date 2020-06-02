package br.com.saintmc.hungergames.abilities.register;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;

import br.com.saintmc.hungergames.abilities.Ability;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;

public class SpecialistAbility extends Ability {

	public SpecialistAbility() {
		super("Specialist", Arrays.asList(new ItemBuilder().name("§aSpecialist").type(Material.BOOK).build()));
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		
		if (hasAbility(player))
			if (isAbilityItem(player.getItemInHand()))
				player.openInventory(Bukkit.createInventory(null, InventoryType.ENCHANTING));
	}

}
