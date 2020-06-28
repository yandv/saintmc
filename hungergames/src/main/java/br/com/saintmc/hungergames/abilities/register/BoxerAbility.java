package br.com.saintmc.hungergames.abilities.register;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import br.com.saintmc.hungergames.abilities.Ability;
import tk.yallandev.saintmc.bukkit.event.player.PlayerDamagePlayerEvent;

public class BoxerAbility extends Ability {

	public BoxerAbility() {
		super("Boxer", new ArrayList<>());
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerDamagePlayer(PlayerDamagePlayerEvent event) {
		Player player = event.getDamager();

		if (hasAbility(event.getPlayer()) && event.getDamage() > 1.0D)
			event.setDamage(event.getDamage() - 0.25D);

		if (hasAbility(player) && player.getItemInHand().getType() == Material.AIR) {
			event.setDamage(event.getDamage() + 2.0D);
			return;
		}

		if (hasAbility(player) && player.getItemInHand().getType() != Material.AIR)
			event.setDamage(event.getDamage() + 0.25D);
	}

}
