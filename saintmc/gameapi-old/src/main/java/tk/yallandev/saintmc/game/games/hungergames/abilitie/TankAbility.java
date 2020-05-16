package br.com.battlebits.game.games.hungergames.abilitie;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import br.com.battlebits.game.ability.AbilityRarity;
import br.com.battlebits.game.constructor.Ability;
import br.com.battlebits.game.constructor.CustomOption;
import br.com.battlebits.game.interfaces.Disableable;
import tk.yallandev.saintmc.bukkit.api.item.ItemBuilder;

public class TankAbility extends Ability implements Disableable {

	public TankAbility() {
		super(new ItemBuilder().type(Material.TNT).build(), AbilityRarity.COMMON);
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

	@Override
	public int getPowerPoints(HashMap<String, CustomOption> map) {
		return 0;
	}

}
