package br.com.saintmc.hungergames.abilities.register;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import br.com.saintmc.hungergames.abilities.Ability;
import tk.yallandev.saintmc.bukkit.event.player.PlayerDamagePlayerEvent;

public class BoxerAbility extends Ability {

	public BoxerAbility() {
		super("Boxer", "Leve menos dano e dê mais dano", new ItemStack(Material.STONE_SWORD));
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
