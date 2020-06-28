package br.com.saintmc.hungergames.abilities.register;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import br.com.saintmc.hungergames.abilities.Ability;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;

public class FiremanAbility extends Ability {

	public FiremanAbility() {
		super("Fireman", Arrays.asList(new ItemBuilder().name("§aBalde de água").type(Material.WATER_BUCKET).build()));
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;

		Player player = (Player) event.getEntity();

		if (!hasAbility(player))
			return;

		if (event.getCause() == DamageCause.LAVA || event.getCause() == DamageCause.FIRE
				|| event.getCause() == DamageCause.FIRE_TICK)
			event.setCancelled(true);
	}

}
