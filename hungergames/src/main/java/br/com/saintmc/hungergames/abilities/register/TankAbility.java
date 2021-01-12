package br.com.saintmc.hungergames.abilities.register;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import br.com.saintmc.hungergames.abilities.Ability;

public class TankAbility extends Ability {

	public TankAbility() {
		super("Tank", new ArrayList<>());
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onDeath(PlayerDeathEvent e) {
		Location loc = e.getEntity().getLocation();
		
		if (e.getEntity().getKiller() instanceof Player && hasAbility(e.getEntity().getKiller())) {
			e.getEntity().getWorld().createExplosion(loc, 4.0F);
		}
	}

	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player && e.getCause().name().contains("EXPLOSION") && hasAbility((Player) e.getEntity())) {
			e.setDamage(0.0D);	
		}
	}

}
