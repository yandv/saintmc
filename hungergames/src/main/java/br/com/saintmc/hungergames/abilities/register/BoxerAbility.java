package br.com.saintmc.hungergames.abilities.register;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import br.com.saintmc.hungergames.abilities.Ability;
import tk.yallandev.saintmc.bukkit.event.player.PlayerDamagePlayerEvent;

public class BoxerAbility extends Ability {

	public BoxerAbility() {
		super("Boxer", new ArrayList<>());
	}

	@EventHandler
	public void onBoxer(PlayerDamagePlayerEvent event) {
		Player damaged = event.getPlayer();

		if (hasAbility(damaged)) {
			if (event.getDamage() - 1 >= 1)
				event.setDamage(event.getDamage() - 1);
			else
				event.setDamage(1);
		}

		Player damager = (Player) event.getDamager();

		if (hasAbility(damager))
			if (damager.getItemInHand().getType() == Material.AIR) {
				event.setDamage(event.getDamage() + 2.5D);
			} else {
				event.setDamage(event.getDamage() + 1);
			}
	}

}
