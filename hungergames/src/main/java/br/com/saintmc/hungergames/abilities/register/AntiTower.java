package br.com.saintmc.hungergames.abilities.register;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import br.com.saintmc.hungergames.abilities.Ability;
import br.com.saintmc.hungergames.event.ability.PlayerEndermageEvent;
import br.com.saintmc.hungergames.event.ability.PlayerStompedEvent;

public class AntiTower extends Ability {
	
	public AntiTower() {
		super("AntiTower", new ArrayList<>());
	}
	
	@EventHandler
	public void onPlayerStomped(PlayerStompedEvent event) {
		if (hasAbility(event.getPlayer()))
			event.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerEndermage(PlayerEndermageEvent event) {
		if (hasAbility(event.getPlayer()))
			event.setCancelled(true);
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onEntityDamage(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;
		
		if (event.getCause() != DamageCause.FALL)
			return;
		
		Player p = (Player) event.getEntity();
		
		if (event.getDamage() < 4.0D)
			return;
		
		if (hasAbility(p)) {
			event.setCancelled(true);
			p.damage(4.0D);
		}
	}

}
