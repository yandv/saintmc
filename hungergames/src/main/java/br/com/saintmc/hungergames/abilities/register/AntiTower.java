package br.com.saintmc.hungergames.abilities.register;

import java.util.ArrayList;

import org.bukkit.event.EventHandler;

import br.com.saintmc.hungergames.abilities.Ability;
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

}
