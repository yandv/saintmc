package br.com.saintmc.hungergames.abilities.register;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import br.com.saintmc.hungergames.GameGeneral;
import br.com.saintmc.hungergames.abilities.Ability;
import br.com.saintmc.hungergames.constructor.Gamer;
import br.com.saintmc.hungergames.event.ability.PlayerStompedEvent;

public class StomperAbility extends Ability {

	public StomperAbility() {
		super("Stomper", new ArrayList<>());
	}

	@EventHandler
	public void onStomper(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;
		
		Player player = (Player) event.getEntity();
		Gamer gamer = GameGeneral.getInstance().getGamerController().getGamer(player.getUniqueId());
		
		if (!hasAbility(player))
			return;
		
		if (gamer.isGamemaker() || gamer.isSpectator())
			return;
		
		DamageCause cause = event.getCause();
		
		if (cause != DamageCause.FALL)
			return;
		
		double dmg = event.getDamage();
		
		for (Player stomped : Bukkit.getOnlinePlayers()) {
			if (stomped.getUniqueId() == player.getUniqueId())
				continue;
			
			if (stomped.getLocation().distance(player.getLocation()) > 5)
				continue;
			
			if (stomped.isSneaking() && dmg > 8)
				dmg = 8;
			
			PlayerStompedEvent playerStomperEvent = new PlayerStompedEvent(stomped, player);
			Bukkit.getPluginManager().callEvent(playerStomperEvent);
			
			if (!playerStomperEvent.isCancelled())
				stomped.damage(dmg, player);
		}
		
		player.getWorld().playSound(player.getLocation(), Sound.ANVIL_LAND, 1, 1);
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
