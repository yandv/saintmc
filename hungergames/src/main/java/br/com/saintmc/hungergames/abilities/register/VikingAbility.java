package br.com.saintmc.hungergames.abilities.register;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import br.com.saintmc.hungergames.abilities.Ability;

public class VikingAbility extends Ability {

	public VikingAbility() {
		super("Viking", new ArrayList<>());
	}

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Player) {
			Player d = (Player) e.getDamager();
			ItemStack item = d.getItemInHand();
			
			if (!hasAbility(d))
				return;
			
			if (item.getType().name().contains("_AXE"))
				e.setDamage(e.getDamage() + 2);
		}
	}
}
