package br.com.saintmc.hungergames.abilities.register;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import br.com.saintmc.hungergames.abilities.Ability;

public class AchillesAbility extends Ability {

	public AchillesAbility() {
		super("Achilles", new ArrayList<>());
	}
	
	@EventHandler
	public void onEntityDamageEvent(EntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;
		
		if (!(event.getDamager() instanceof Player))
			return;
		
		Player damager = (Player) event.getDamager();
		Player entity = (Player) event.getEntity();
		
		if (!hasAbility(entity))
			return;
		
		if (damager.getItemInHand() == null)
			return;
		
		ItemStack item = damager.getItemInHand();
		
		if (item.getType().toString().contains("WOOD_")) {
			event.setDamage(event.getDamage() + 3.0D);
		} else {
			event.setDamage(event.getDamage() - 2.0D);
		}
	}
	
}
